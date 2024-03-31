package com.example.chatbot.bot;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
public class MyBot extends TelegramLongPollingBot{

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

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            executorService.submit(this::send);
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        log.info("获取到update");
        for (Update update : updates) {
            redisTemplate.opsForList().rightPush("CommonMessage", gson.toJson(update));
            log.info("已推送update到redis");
        }
    }

    public void send() {
        while (true) {
            String toSend = redisTemplate.opsForList().leftPop("ReturnMessage");
            if (toSend != null && !toSend.isEmpty()) {
                SendMessage message = gson.fromJson(toSend, SendMessage.class);
                try {
                    execute(message); // Call method to send the message
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
    }

//    @Override
//    public Message execute(SendMessage sendMessage) throws TelegramApiException {
//        log.info(sendMessage.getText());
//        return null;
//    }
}
