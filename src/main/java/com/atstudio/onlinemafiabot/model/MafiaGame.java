package com.atstudio.onlinemafiabot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class MafiaGame {
    @Id
    private String gameId;
    private Long chatId;
    private Map<Integer, GameRole> gameRolesByPlayerNumber;
    private Map<String, Integer> playerNumbersByUser;
    private GamePhase phase;

    private List<NightEvent> nightEvents;

    public List<NightEvent> getNightEvents() {
        if (nightEvents == null) {
            nightEvents = new ArrayList<>();
        }
        return nightEvents;
    }
}

