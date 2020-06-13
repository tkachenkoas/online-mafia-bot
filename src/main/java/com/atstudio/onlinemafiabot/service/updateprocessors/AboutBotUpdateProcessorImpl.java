package com.atstudio.onlinemafiabot.service.updateprocessors;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getChatId;

@Component
public class AboutBotUpdateProcessorImpl extends AbstractUpdateProcessor {
    @Override
    protected void process(Update update) {
        sendMessage(getChatId(update), messageProvider.getMessage("about_bot"));
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, "/about");
    }
}
