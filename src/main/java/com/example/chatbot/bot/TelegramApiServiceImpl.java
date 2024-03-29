package com.example.chatbot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramApiServiceImpl implements TelegramApiService{

    @Override
    public Message execute(SendMessage sendMessage) throws TelegramApiException {
        // 这里是与Telegram API的实际交互代码
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }
}
