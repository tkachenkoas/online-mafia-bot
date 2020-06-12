package com.atstudio.onlinemafiabot.service.updateprocessors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;

@Component
public class GameStartUpdateProcessor extends AbstractUpdateProcessor {

    private static final String GAME_START_COMMAND = "/start_game";

    @Override
    protected void process(Update update) {

    }

    @Override
    protected boolean applicableFor(Update update) {
        return StringUtils.contains(getMessageText(update), GAME_START_COMMAND);
    }
}
