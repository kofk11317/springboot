package com.example.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
package com.example.server.controller;

import com.example.server.dto.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DefaultController {
    @Autowired
    private TestMapper testMapper;

    // Other methods...

    @PutMapping("/updateInterest")
    @ResponseBody
    public String updateInterest(@RequestBody Member member) {
        try {
            testMapper.updateMemberInterest(member.getMemberNum(), member.getMainInterest(), member.getSubInterest());
            return "관심사 업데이트 성공!";
        } catch (Exception e) {
            return "관심사 업데이트 실패: " + e.getMessage();
        }
    }
}package com.example.server.testinterface;

import com.example.server.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TestMapper {
    // Other methods...

    void updateMemberInterest(@Param("memberNum") int memberNum, @Param("mainInterest") String mainInterest, @Param("subInterest") String subInterest);
}