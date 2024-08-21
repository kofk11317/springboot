package com.example.server.testinterface;

import com.example.server.dto.CreateNews;
import com.example.server.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TestMapper
{
   List<String> getDescriptions();//뉴스 기사 가져오기
   int  checkEmailExists(String email);
   void SignUp(String id, String password, String name, String email, int age, String gender, String mainInterest, String subInterest);
   List<CreateNews> selectCreateNewsList(int id);
   List<CreateNews> selectCreateNewsListPaginated(int offset, int limit);
   List<CreateNews> selectCreateNewsListByCategoryPaginated(int offset, int limit, String category);
   List<CreateNews> selectCreateNewsListBySearchPaginated(int offset, int limit, String query);
   CreateNews selectCreateNewsDetail(int id);
   long countCreateNews();
   Member SignIn(String id, String password);
   void updateMember(int memberNum, String mainInterest, String subInterest);
   void deleteMember(String email, String password);
   int checkEmailAndPassword(String email, String password);



   Member findByUserId(String id);
   int checkIDExists(String id);
   List<CreateNews> selectTrendNewsList();
   void updateCreateNewsJoind(int id);

   void insertLike(int memberNum, int newsId, int like);
   Integer selectLike(int memberNum, int newsId);
   void updateLike(int memberNum, int newsId, int like);
   int selectAllLike(int newsId);
   int selectAllDislike(int newsId);
   List<CreateNews> selectRelatedNews(String query);
}