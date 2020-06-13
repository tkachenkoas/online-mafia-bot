package com.atstudio.onlinemafiabot.service.gameinfo;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class EventProcessor {

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

    public void changeGamePhase(MafiaGame mafiaGame, GamePhase phase) {
        mafiaGame.setPhase(phase);
        mafiaGameRepository.save(mafiaGame);
    }
}
