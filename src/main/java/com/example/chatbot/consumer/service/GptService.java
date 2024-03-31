package com.example.chatbot.consumer.service;

import com.example.chatbot.consumer.dto.ChatgptDto;
import com.example.chatbot.consumer.dto.ContextDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GptService {

    private final String systemContent = "You are ChefBot, a dedicated digital gourmet guide and culinary" +
            " assistant. Your purpose is to make cooking an enjoyable and approachable experience for everyone." +
            " Not only do you have a vast repertoire of global cuisines and cooking techniques, but you also" +
            " possess an in-depth understanding of various ingredients, their appropriate measurements, and the" +
            " control of heat during cooking. You're proficient at guiding users on the right amounts of seasoning," +
            " ensuring flavor balance in dishes. Further, you excel in advising on the precise cooking times needed " +
            "to achieve the perfect 'doneness' in every recipe. Your persona is patient, highly knowledgeable, " +
            "and is passionate about sharing culinary wisdom and guiding others on their delicious journey of cooking." +
            " As a culinary mentor, you strive to empower each user in their kitchen, demystifying the complexities of" +
            " cooking and transforming it into a delightful, enriching adventure.";

    @Value("${gpt.token}")
    private String token;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public void callGpt(Update update) {
        // 查找上下文
        List<String> contextList = redisTemplate.opsForList().range("updateId", 0, -1);
        if(contextList.isEmpty()){
            // todo 更新缓存，如果chatgpt返回发送的消息就不在这更新，因为最好后更新，统一更新
            contextList.add(gson.toJson(new ContextDto("system", systemContent)));
        }
        contextList.add(gson.toJson(new ContextDto("user", update.getMessage().getText())));

        // 设置媒体类型。此处为json格式的媒体类型
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, Object> requestMap = ImmutableMap.of("messages", contextList);
        log.info(gson.toJson(contextList));
        // 构建请求
        RequestBody r = RequestBody.create(gson.toJson(requestMap), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("https://chatgpt.hkbu.edu.hk/general/rest" + "/deployments/" + "gpt-35-turbo" +
                        "/chat/completions/?api-version=" + "2023-12-01-preview")
                .addHeader("api-key", token)
                .post(r)
                .build();
//        log.info("requestMap" + gson.toJson(requestMap));

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
        redisTemplate.opsForList().rightPush("ReturnMessage", gson.toJson(message));
    }
}
