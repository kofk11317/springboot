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

//    db 연결할때 사용
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

    @Autowired
    private TestMapper testMapper2;
    @GetMapping("/create_news")
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


















}
