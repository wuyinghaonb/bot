package com.example.chatbot.consumer.dto;

import lombok.Data;

@Data
public class ContextDto {
    private String role;
    private String content;
    public ContextDto (String r, String c){
        role = r;
        content = c;
    }
}
