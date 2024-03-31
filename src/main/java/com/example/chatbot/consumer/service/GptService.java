package com.example.chatbot.consumer.service;

import com.example.chatbot.consumer.dto.ChatgptDto;
import com.example.chatbot.consumer.dto.ContextDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
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
        List<ContextDto> list = getAndUpdateContext(update.getMessage().getFrom().getId(), "user", update.getMessage().getText());
        // 以前的方式
//        Object[] messages = new Object[1];
//        messages[0] = ImmutableMap.of("role", "user", "content", update.getMessage().getText());
//        log.info(gson.toJson(messages));

        // 设置媒体类型。此处为json格式的媒体类型
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, Object> requestMap = ImmutableMap.of("messages", list);
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
                log.info(gson.toJson(chatgptDto.getChoices().get(0).getMessage()));
                getAndUpdateContext(update.getMessage().getFrom().getId(), "assistant", chatgptDto.getChoices().get(0).getMessage().getContent());
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(res);
        redisTemplate.opsForList().rightPush("ReturnMessage", gson.toJson(message));
    }

    public List<ContextDto> getAndUpdateContext(Long userId, String role, String content) {
        List<ContextDto> list = new ArrayList<>();
        try {
            // 获取上下文
            String context = redisTemplate.opsForValue().get(String.valueOf(userId));
            if (context == null) {
                // 初始化
                list.add(new ContextDto("system", systemContent));
            } else {
                list = gson.fromJson(context, new TypeToken<List<ContextDto>>() {
                }.getType());
            }
            list.add(new ContextDto(role, content));
            log.info(String.valueOf(userId), gson.toJson(list));
            redisTemplate.opsForValue().set(String.valueOf(userId), gson.toJson(list));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return list;
    }
}
