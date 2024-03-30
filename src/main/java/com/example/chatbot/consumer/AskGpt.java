package com.example.chatbot.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Slf4j
public class AskGpt {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static SendMessage callGpt(Update update){
        String content = update.getMessage().getText();
        // 设置媒体类型。此处为json格式的媒体类型
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        // 请求体
        Object[] messages = new Object[1];
        messages[0] = ImmutableMap.of("role", "user", "content", content);
        Map<String, Object> requestMap = ImmutableMap.of("messages", messages);

        // 构建请求
        RequestBody r = RequestBody.create(gson.toJson(requestMap), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("https://chatgpt.hkbu.edu.hk/general/rest" + "/deployments/" + "gpt-35-turbo" +
                        "/chat/completions/?api-version=" + "2023-12-01-preview")
                .addHeader("api-key", "ca33fab4-8f9f-458c-beda-16923167bb61")
                .post(r)
                .build();
        log.info("requestMap" + gson.toJson(requestMap));

        String res = null;
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ChatgptDto chatgptDto = objectMapper.readValue(response.body().string(), ChatgptDto.class);
                res = chatgptDto.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        log.info(res);
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(res);
        return message;
    }
}
