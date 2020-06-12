package com.atstudio.onlinemafiabot.telegram;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Controller("/callback")
@AllArgsConstructor
public class WebhookController {

    private static final String BOT_PATH = "send-updates-here";

    private final TelegramWebhookBot bot;
    private final UpdateHandler updateHandler;

    @SneakyThrows
    @PostConstruct
    public void init() {
        bot.setWebhook("https://online-mafia-bot.herokuapp.com/callback/" + BOT_PATH, "");
    }

    @PostMapping("/callback/" + BOT_PATH)
    public void handleWebhook(@RequestBody Update update) {
        updateHandler.handle(update);
    }

}
