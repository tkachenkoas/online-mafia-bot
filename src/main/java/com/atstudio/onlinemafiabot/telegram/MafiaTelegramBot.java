package com.atstudio.onlinemafiabot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@Component
public class MafiaTelegramBot extends TelegramWebhookBot {

    private final static String BOT_PATH = "botwebhook";

    private @Value("${bot.token}")
    String botToken;
    private @Value("${bot.username}")
    String botUserName;

    @Autowired
    private UpdateHandler updateHandler;

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        updateHandler.handle(update);
        return null;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return BOT_PATH;
    }

}
