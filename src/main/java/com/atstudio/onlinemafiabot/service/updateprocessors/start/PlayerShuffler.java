package com.atstudio.onlinemafiabot.service.updateprocessors.start;

import com.atstudio.onlinemafiabot.model.Player;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PlayerShuffler {

    private final TgApiExecutor executor;

    public Map<Player, Integer> shuffleAndNotifyChat(Long chatId, List<Player> playersList) {
        List<Player> shuffledPlayers = new ArrayList<>(playersList);
        Collections.shuffle(shuffledPlayers);

        notifyPlayers(chatId, shuffledPlayers);

        Map<Player, Integer> result = new HashMap<>();
        for (int i = 0; i < shuffledPlayers.size(); i++) {
            result.put(shuffledPlayers.get(i), i + 1);
        }
        return result;
    }

    private void notifyPlayers(Long chatId, List<Player> players) {
        List<String> numberedPlayers = getNumberedPlayers(players);

        String shuffledText = numberedPlayers.stream().collect(Collectors.joining("\n"));

        SendMessage message = new SendMessage(
                chatId,
                shuffledText
        );
        executor.execute(message);
    }

    private List<String> getNumberedPlayers(List<Player> players) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            result.add((i + 1) + " -> " + players.get(i).getName());
        }
        return result;
    }
}
