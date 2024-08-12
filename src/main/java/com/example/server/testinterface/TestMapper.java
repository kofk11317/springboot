package com.example.server.testinterface;

import com.example.server.dto.CreateNews;
import com.example.server.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TestMapper
{
   int testConnection();//db 연결 테스트
   List<String> getDescriptions();//뉴스 기사 가져오기
   int  checkEmailExists(String email);
   void SignUp(String id, String password, String name, String email, int age, String gender, String mainInterest, String subInterest);
   List<CreateNews> selectCreateNewsList();
   List<CreateNews> selectCreateNewsListPaginated(int offset, int limit);
   long countCreateNews();
   Member SignIn(String id, String password);
}


