package com.example.chatbot;

import static org.mockito.Mockito.*;

import com.example.chatbot.bot.MyBot;
import com.example.chatbot.bot.TelegramApiService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ChatBotApplicationTests {

    @Mock
    private TelegramApiService telegramApiService;

    @Autowired
    private MyBot bot;

    @Test
    public void testBotLoad() throws TelegramApiException {
        long startTime = System.currentTimeMillis(); // 获取程序开始时间

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("mock message");
        sendMessage.setChatId("mock id");

        // 模拟TelegramApiService的行为
        lenient().when(telegramApiService.execute(any(SendMessage.class))).thenAnswer(invocation -> {
            SendMessage argument = invocation.getArgument(0);
            System.out.println("Mock execute method called with: " + argument.getText());
            return new Message();
        });

        // 模拟多次调用以测试负载
        List<Update> updates = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            updates.add(createMockUpdate(i));
            // 假设这是触发bot响应的方法
        }
        bot.onUpdatesReceived(updates);


        long endTime = System.currentTimeMillis(); // 获取程序结束时间
        long elapsedTime = endTime - startTime; // 计算程序运行时间
        System.out.println("程序运行时间：" + elapsedTime + " 毫秒");
        while(true){

        }

        // 验证模拟对象的方法被调用了预期次数
    //        verify(telegramApiService, times(10)).execute(any(SendMessage.class));
    }

    public static Update createMockUpdate(Integer i) {
        User mockUser = new User();
        mockUser.setId((long) i);
        mockUser.setFirstName("User" + i);
        mockUser.setLastName("LastName" + i);
        Chat mockChat = new Chat();
        mockChat.setId((long) i);
        mockChat.setTitle("Chat" + i);
        Message mockMessage = new Message();
        mockMessage.setMessageId(i);
        mockMessage.setFrom(mockUser);
        mockMessage.setChat(mockChat);
        mockMessage.setText("你好 " + i);
        Update mockUpdate = new Update();
        mockUpdate.setMessage(mockMessage);
        return mockUpdate;
    }
}
