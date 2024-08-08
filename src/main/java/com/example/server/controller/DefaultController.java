package com.example.server.controller;

import com.example.server.dto.Member;
import com.example.server.testinterface.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
//192.168.0.81

@Controller
public class DefaultController
{


    @GetMapping("/")
    @ResponseBody // 리턴값이 view가 아닌 데이터 자체임을 나타냄
    public String home()
    {
        return "hello wdsdforld!";
    }

//    db 연결할때 확인용
@Autowired
private TestMapper testMapper;
    @GetMapping("/db")
    @ResponseBody // 리턴값이 view가 아닌 데이터 자체임을 나타냄
    public String testDbConnection()
    {
        try {
            int result = testMapper.testConnection();
            return "데이터베이스 연결 성공! 결과: " + result;
        } catch (Exception e) {
            return "데이터베이스 연결 실패: " + e.getMessage();
        }
    }
//   뉴스 기사 가져오기
    @Autowired
    private TestMapper testMapper2;
    @GetMapping("/getnews")
    @ResponseBody // 리턴값이 view가 아닌 데이터 자체임을 나타냄
    public List<String> getNews()
    {
        try {
            List<String> result = testMapper2.getDescriptions();
            return Collections.singletonList("데이터베이스 연결 성공! 결과: " + result);
        } catch (Exception e) {
            return Collections.singletonList("데이터베이스 연결 실패: " + e.getMessage());
        }
    }




    // 프론트 없이 테스트해보는 코드를 작성하려면?
//
//    회원가입 기능

    @Autowired

    private TestMapper testMapper3;
    @PostMapping("/signup")
    @ResponseBody
    public String signup(@RequestBody Member member)

    {
        if (testMapper3.checkEmailExists(member.getEmail()) > 0)
        {
            return "이미 존재하는 이메일입니다.";
        }
        else
        {
        testMapper.SignUp(member.getId(), member.getPassword(), member.getName(), member.getEmail(), member.getAge(), member.getGender())    ;
        return "회원가입 성공!";
        }
    }

    @Autowired
    private TestMapper testMapper4;

    @PostMapping("/signin")
    @ResponseBody
    public String login(@RequestBody Member member) {
//       멤버 객체에서 필요한 것만 뽑아다 사용가능
//        아이디가 같고 비밀번호가 다른 경우?
        if (testMapper4.SignIn(member.getId(), member.getPassword()) > 0) {
            return "로그인 성공";
        } else {
            return "아이디 또는 비밀번호가 잘못되었습니다.";
        }
    }
//로그아웃기능 구현-세션종료 형식으로


//   개인정보 수정(이메일,관심사)















































































}
