package com.example.chatbot.consumer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContextDto {
    private String role;
    private String content;
    public ContextDto (String r, String c){
        role = r;
        content = c;
    }
}
