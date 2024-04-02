package com.example.chatbot.consumer.service;

import com.example.chatbot.consumer.tool.SlidingWindowRateLimiter;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class ConsumeService {

    @Resource
    GptService gptService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Gson gson = new Gson();
    private final AtomicBoolean flag = new AtomicBoolean(false);
    private final SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(60, 80000);

    @PostConstruct
    public void init() {
        executorService.submit(this::execute);
        executorService.submit(this::execute);
    }

    public void execute() {
        while (true) {
            if (rateLimiter.isAllowed()) {
                String message;
                if (!flag.getAndSet(true)) {
                    log.info("消费者尝试拉取vip消息");
                    message = redisTemplate.opsForList().leftPop("VipMessage", 20, TimeUnit.SECONDS);
                    flag.set(false);
                    if (message == null) {
                        continue;
                    }
                } else {
                    log.info("消费者尝试拉取消息");
                    message = redisTemplate.opsForList().leftPop("CommonMessage", 20, TimeUnit.SECONDS);
                }
                // 处理
                if (message != null) {
                    // 询问gpt
                    log.info("拉取成功");
                    Update update = gson.fromJson(message, Update.class);
                    log.info("询问gpt " + update.getMessage());
                    gptService.callGpt(update);
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
