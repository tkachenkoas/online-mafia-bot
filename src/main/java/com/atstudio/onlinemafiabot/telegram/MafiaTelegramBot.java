package com.atstudio.onlinemafiabot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class MafiaTelegramBot extends TelegramLongPollingBot {

    private @Value("${bot.token}") String botToken;
    private @Value("${bot.username}") String botUserName;

    @Autowired
    private UpdateHandler updateHandler;

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received update from telegram: {}", update);
        updateHandler.handle(update);
    }

}
