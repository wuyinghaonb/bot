package com.example.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatBotApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ChatBotApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // 禁用Web容器
        app.run( args);
    }

}
