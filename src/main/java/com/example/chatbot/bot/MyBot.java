package com.example.chatbot.bot;

import com.example.chatbot.chatgpt.ChatgptDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
public class MyBot extends TelegramLongPollingBot implements TelegramApiService {

    @Value("${telegrambot.botUserName}")
    private String botUsername;

    @Value("${telegrambot.botToken}")
    private String token;

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            CompletableFuture.runAsync(() -> {
                try {
                    this.onUpdateReceived(update);
                } catch (Exception e) {
                    log.error("An error occurred while processing message: ", e);
                }
            });
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String content = update.getMessage().getText();
            // todo 指令响应
            if (content.startsWith("/")) {

            } else {
                // todo chatgpt 打算发送给消息队列
                // 设置媒体类型。此处为json格式的媒体类型
                MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                // 请求体
                Object[] messages = new Object[1];
                messages[0] = ImmutableMap.of("role", "user", "content", content);
                Map<String, Object> requestMap = ImmutableMap.of("messages", messages);

                // 构建请求
                Gson gson = new Gson();
                RequestBody r = RequestBody.create(gson.toJson(requestMap), MEDIA_TYPE_JSON);
                Request request = new Request.Builder()
                        .url("https://chatgpt.hkbu.edu.hk/general/rest" + "/deployments/" + "gpt-35-turbo" +
                                "/chat/completions/?api-version=" + "2023-12-01-preview")
                        .addHeader("api-key", "ca33fab4-8f9f-458c-beda-16923167bb61")
                        .post(r)
                        .build();
                log.info("requestMap" + gson.toJson(requestMap));

                try (Response response = client.newCall(request).execute()) {
                    if (response.body() != null) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        ChatgptDto chatgptDto = objectMapper.readValue(response.body().string(), ChatgptDto.class);
                        content = chatgptDto.getChoices().get(0).getMessage().getContent();
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(content);

            try {
                execute(message); // Call method to send the message
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    @Override
    public Message execute(SendMessage sendMessage) throws TelegramApiException {
        log.info(sendMessage.getText());
        return null;
    }
}
