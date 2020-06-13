package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@AllArgsConstructor
@Component
public class NightStartUpdateProcessor extends AbstractUpdateProcessor {

    private static final String NIGHT_START_MESSAGE = "/first_night";

    private final MafiaGameRepository gameRepository;

    @Override
    protected void process(Update update) {
        Chat chat = update.getMessage().getChat();
        Long chatId = chat.getId();
        if (chat.isUserChat()) {
            sendMessage(chatId, messageProvider.getMessage("only_group_chat"));
            return;
        }

        Optional<MafiaGame> game = gameRepository.findOneByChatId(chatId);
        if (!game.isPresent()) {
            sendMessage(chatId, messageProvider.getMessage("game_not_found"));
            return;
        }

        sendMessage(chatId, messageProvider.getMessage("action_instructions"));
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, NIGHT_START_MESSAGE);
    }
}
