package com.atstudio.onlinemafiabot.service.gameinfo;

import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.model.NightEvent;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class NightResultAnnouncer {

    private final MessageProvider messageProvider;
    private final TgApiExecutor executor;
    private final GameInfoProvider gameInfoProvider;

    public void announceNightResult(MafiaGame mafiaGame) {
        long mafiaCount = gameInfoProvider.getMafiaCount(mafiaGame);
        Map<Integer, Integer> voteCountByPlayerNumber = getVotesByPlayer(mafiaGame);

        Long chatId = mafiaGame.getChatId();

        Set<Map.Entry<Integer, Integer>> entries = voteCountByPlayerNumber.entrySet();
        for (Map.Entry<Integer, Integer> entry: entries) {
            if (entry.getValue() == mafiaCount) {
                executor.execute(
                        new SendMessage(chatId, messageProvider.getMessage("mafia_killed", entry.getKey()))
                );
                sayGoodBye(chatId);
                return;
            }
        }

        executor.execute(
                new SendMessage(chatId, messageProvider.getMessage("mafia_missed"))
        );
        sayGoodBye(chatId);
    }

    private void sayGoodBye(Long chatId) {
        executor.execute(
                new SendMessage(chatId, messageProvider.getMessage("goodbye"))
        );
    }

    private Map<Integer, Integer> getVotesByPlayer(MafiaGame mafiaGame) {
        Map<Integer, Integer> result = new HashMap<>();
        for (NightEvent event : mafiaGame.getNightEvents()) {
            result.compute(event.getTargetPlayer(), (key, value) -> value == null ? 1 : value + 1);
        }
        return result;
    }
}
