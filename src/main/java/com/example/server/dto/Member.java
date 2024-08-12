package com.example.server.dto;
import java.sql.Date;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member
{
// DTO(Data Transfer Object) 클래스는 데이터베이스 테이블의 데이터를 전달하는 목적으로 사용
    private int memberNum;
    private String id;
    private String password;
    private String name;
    private String email;
    private int age;
    private String gender;
    private String mainInterest;
    private String subInterest;


}
