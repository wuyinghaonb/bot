package com.example.chatbot.consumer;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class RedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Gson gson = new Gson();
    private final SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(60, 60000);

    @PostConstruct
    public void init() {
        for (int i = 0; i < 4; i++) {
            executorService.submit(this::execute);
        }
    }

    public void execute() {
        while (true) {
            if (rateLimiter.isAllowed()) {
                String message = redisTemplate.opsForList().leftPop("VipMessage");
                if (message == null) {
                    message = redisTemplate.opsForList().leftPop("CommonMessage");
                }
                if (message != null) {
                    Update update = gson.fromJson(message, Update.class);
                    log.info("询问gpt " + update.getMessage().getFrom());
                    SendMessage send = AskGpt.callGpt(update);
                    redisTemplate.opsForList().rightPush("ReturnMessage", gson.toJson(send));
                }
            } else {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("{}线程任务关闭", this.getClass().getSimpleName());
        executorService.shutdown();
    }
}
