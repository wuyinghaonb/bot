package com.example.chatbot.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatgptDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private long created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<Choice> choices;

    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    // Getters and Setters omitted for brevity

    @Data
    public static class Choice {

        @JsonProperty("index")
        private int index;

        @JsonProperty("message")
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;

        // Getters and Setters omitted for brevity
    }

    @Data
    public static class Message {

        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;

        // Getters and Setters omitted for brevity
    }

    @Data
    public static class Usage {

        @JsonProperty("prompt_tokens")
        private int promptTokens;

        @JsonProperty("completion_tokens")
        private int completionTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;

        // Getters and Setters omitted for brevity
    }
}
