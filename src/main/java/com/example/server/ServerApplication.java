package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;


//db 연결 할때랑 안할 때 구분
@MapperScan("com.example.server.testinterface")//메인 애플리케이션 클래스에 @MapperScan 어노테이션을 추가하여 MyBatis 매퍼를 스캔하도록 설정

//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication

public class ServerApplication
{

    public static void main(String[] args) {

        SpringApplication.run(ServerApplication.class, args);
    }

}
