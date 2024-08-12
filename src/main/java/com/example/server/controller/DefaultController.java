package com.example.server.controller;

import com.example.server.dto.Member;
import com.example.server.testinterface.TestMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
//192.168.0.81

@Controller
public class DefaultController
{



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





//로그인이 되어야지만 사용가능한 기능들
    @PostMapping("/signin")
    @ResponseBody
    public String login(@RequestBody Member member, HttpSession session, RedirectAttributes redirectAttributes , HttpServletRequest request)
    {
        Member loggInMember = testMapper.SignIn(member.getId(), member.getPassword());

//       멤버 객체에서 필요한 것만 뽑아다 사용가능
        if (loggInMember != null)
        {
            session.setAttribute("userId", loggInMember.getId());  // 로그인 성공 시 Memberid을 세션에 저장

            session.setAttribute("userseqId", loggInMember.getMemberNum());  // 로그인 성공 시 MemberNum을 세션에 저장 (회원정보 수정시 사용)
            return "로그인 성공";
        } else {
            return "아이디 또는 비밀번호가 잘못되었습니다.";
        }
    }




    //로그아웃기능 구현-세션종료 형식으로

//로그아웃 버튼 누르면 이 주소가 호출되고 세션을 종료하고 메인페이지로 리다이렉트
    @GetMapping("/logout")
    @ResponseBody // restful api에서는 redirect를 사용하지 않음
    public void sessionInvalidate(HttpSession session, HttpServletResponse response) throws IOException
    {
        session.invalidate();
        response.sendRedirect("http://localhost:8080");
    }



    //   개인정보 수정(관심사)
    // 로그인이 되어있는 사람의 세션값을 가지고 와서 그걸 할당하려면?
//    @GetMapping("/updateInterests")
    @PutMapping("/updateInterests")
    @ResponseBody
    public String updateInterests(@RequestBody Member member, HttpSession session)
    {
        try {
//            로그인 되엇을때 세션에 저장된 userseqId를 가져와서 사용
            testMapper.updateMember((Integer) session.getAttribute("userseqId"), member.getMainInterest(), member.getSubInterest());
            return "관심사 업데이트 성공!";
        } catch (Exception e) {
            return "관심사 업데이트 실패: " + e.getMessage();
        }
    }
//    회원탈퇴

//    일단 회원id가 나왔으면 좋을것 같음 session.getAttribute("userId")
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
















































































}
