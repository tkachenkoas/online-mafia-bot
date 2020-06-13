package com.atstudio.onlinemafiabot.service.gameinfo;

import com.atstudio.onlinemafiabot.model.NightAction;
import com.atstudio.onlinemafiabot.model.NightEvent;
import com.atstudio.onlinemafiabot.model.PlayerAndGame;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EventAdder {

    private MafiaGameRepository mafiaGameRepository;

    public void addEventToGame(PlayerAndGame playerAndGame, Integer targetPlayerNumber, NightAction action) {
        playerAndGame.getMafiaGame().getNightEvents().add(
                NightEvent.builder()
                        .playerLogin(playerAndGame.getPlayer().getLogin())
                        .action(action)
                        .targetPlayer(targetPlayerNumber)
                        .build()
        );
        mafiaGameRepository.save(playerAndGame.getMafiaGame());

    }
}
