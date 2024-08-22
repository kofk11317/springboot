package com.example.server.controller;

import com.example.server.dto.Member;
import com.example.server.service.JwtService;
import com.example.server.testinterface.TestMapper;
import com.example.server.token.JwtIssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
//192.168.0.81

@Controller
public class DefaultController
{

    @Autowired
    private JwtIssueService jwtIssueService;

    @Autowired
    private JwtService jwtService;


    @GetMapping("/")
    @ResponseBody
    public String home(HttpSession session)
    {
        if (session.getAttribute("userId") != null) {
            return "Hello, " + session.getAttribute("userId") ;  // 로그인 상태
        } else {
            return "Hello, World!";  // 로그인 안된 상태
        }
    }

    @Autowired
    private TestMapper testMapper;
//   뉴스 기사 가져오기

    @GetMapping("/getnews")
    @ResponseBody // 리턴값이 view가 아닌 데이터 자체임을 나타냄
    public List<String> getNews()
    {
        try {
            List<String> result = testMapper.getDescriptions();
            return Collections.singletonList("데이터베이스 연결 성공! 결과: " + result);
        } catch (Exception e) {
            return Collections.singletonList("데이터베이스 연결 실패: " + e.getMessage());
        }
    }




    // 프론트 없이 테스트해보는 코드를 작성하려면?
//
//    회원가입 기능


    @PostMapping("/signup")
    @ResponseBody
    public String signup(@RequestBody Member member)

    {
//        이메일 중복체크
        if (testMapper.checkEmailExists(member.getEmail()) > 0)
        {
            return "이미 존재하는 이메일입니다.";
        }
        else
        {
        testMapper.SignUp(member.getId(), member.getPassword(), member.getName(), member.getEmail(), member.getAge(), member.getGender(),member.getMainInterest(),member.getSubInterest());
        return "회원가입 성공!";
        }
    }


    @PostMapping("/signin")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Member member, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request)
    {
        Member loggedInMember = testMapper.SignIn(member.getId(), member.getPassword());

        if (loggedInMember != null)
        {
            String token = jwtIssueService.createJwt(loggedInMember);

            Map<String, String> response = new HashMap<>();
            response.put("userId", loggedInMember.getId());
            response.put("token", token);

//
////            Flask 서버와 통신
//            WebClient webClient = WebClient.create();
//            Map<String, Integer> map = new HashMap<>();
////            flask에서 받을 때 변수명이 memberNum으로
//            map.put("memberNum", loggedInMember.getMemberNum());
//            //로그인한 사용자의 memberNum을 Flask 서버에 전달
//            Mono<Integer[]> flask = webClient.post()
//                    .uri( "http://127.0.0.1:5000") //메서드를 사용하여 요청을 보낼 URL을 설정합니다 Flask 서버 URL을 적절하게 변경 필요
//                    .bodyValue(map)//메서드를 사용하여 요청 본문을 설정합니다. 이 예제에서는 map이라는 Map 객체를 요청 본문으로 사용합니다.
//                    .retrieve()//메서드를 호출하여 HTTP 요청을 보냅니다. 이 메서드는 WebClient.ResponseSpec 객체를 반환합니다. 이 객체는 HTTP 응답을 처리하는 메서드를 제공합니다.
//                    .bodyToMono(Integer[].class);//메서드를 사용하여 HTTP 응답 본문을 Mono로 변환합니다. 이 메서드는 HTTP 응답 본문을 지정된 클래스 타입으로 변환한 Mono를 반환합니다. 이 예제에서는 응답 본문을 String으로 변환합니다
//
//            flask.subscribe(result -> {
//                for (Integer num : result) {
//                    System.out.println(num);}
//            });
//            Mono는 0 또는 1개의 결과를 발행하는 Publisher로, 비동기 작업의 결과를 처리하는데 사용됩니다.
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }


    //로그아웃기능 구현-세션종료 형식으로

//로그아웃 버튼 누르면 이 주소가 호출되고 세션을 종료하고 메인페이지로 리다이렉트
    @GetMapping("/logout")
    @ResponseBody // restful api에서는 redirect를 사용하지 않음
    public void sessionInvalidate(HttpServletResponse response) throws IOException
    {
        response.sendRedirect("http://localhost:8080");
    }



    //   개인정보 수정(관심사)
    // 로그인이 되어있는 사람의 세션값을 가지고 와서 그걸 할당하려면?
    @PutMapping("/updateInterests")
    @ResponseBody
    public ResponseEntity<?> updateInterests(@RequestBody Member member, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtService.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 제공되지 않았습니다.");
            }

            String userId = jwtService.validateTokenAndGetUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }

            Member _member = testMapper.findByUserId(userId);
            if (_member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            testMapper.updateMember(_member.getMemberNum(), member.getMainInterest(), member.getSubInterest());
            return ResponseEntity.ok("사용자 정보 업데이트 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 업데이트 실패: " + e.getMessage());
        }
    }

//    개인정보 수정
    @GetMapping("/updateInterests")
    @ResponseBody
    public ResponseEntity<?> updateInterestsGetMapping(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 제공되지 않았습니다.");
        }

        String userId = jwtService.validateTokenAndGetUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        Member member = testMapper.findByUserId(userId);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("name", member.getName());
        response.put("email", member.getEmail());
        response.put("age", String.valueOf(member.getAge()));
        response.put("gender", member.getGender());
        response.put("mainInterest", member.getMainInterest());
        response.put("subInterest", member.getSubInterest());

        return ResponseEntity.ok(response);
    }




//    회원탈퇴

//

    @DeleteMapping("/deleteMember")
    @ResponseBody
    public String deleteMember(@RequestBody Member member) {

        int count = testMapper.checkEmailAndPassword(member.getEmail(), member.getPassword());
        if (count > 0) {
            testMapper.deleteMember(member.getEmail(), member.getPassword());
            return "회원 정보 삭제 성공!";
        } else {
            return "이메일 또는 비밀번호가 잘못되었습니다.";
        }
    }
//회원탈퇴
    @GetMapping ("/deleteMember")
    @ResponseBody
    public ResponseEntity<?> deleteMemberGetMapping(HttpSession session) {
        String userId = (String) session.getAttribute("userId");  // 로그인 성공 시 Memberid을 세션에 저장
        String userEmail = (String) session.getAttribute("userEmail");

        if (userId == null || userEmail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("로그인해주세요");
        }

        Map<String, String> response = new HashMap<>();
        response.put("userId", userId);
        response.put("userEmail", userEmail);

        return ResponseEntity.ok(response);
    }

}
