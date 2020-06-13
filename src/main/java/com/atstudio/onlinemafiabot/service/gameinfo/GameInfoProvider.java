package com.atstudio.onlinemafiabot.service.gameinfo;

import com.atstudio.onlinemafiabot.model.GameRole;
import com.atstudio.onlinemafiabot.model.MafiaGame;
import org.springframework.stereotype.Component;

@Component
public class GameInfoProvider {
    public long getMafiaCount(MafiaGame mafiaGame) {
        return mafiaGame.getGameRolesByPlayerNumber()
                .values().stream()
                .filter(GameRole::isMafia).count();
    }
}
