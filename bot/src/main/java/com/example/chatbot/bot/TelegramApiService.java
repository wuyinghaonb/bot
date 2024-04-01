package com.example.chatbot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramApiService {
    Message execute(SendMessage sendMessage) throws TelegramApiException;
}
