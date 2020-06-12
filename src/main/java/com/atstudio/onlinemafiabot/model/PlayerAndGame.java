package com.atstudio.onlinemafiabot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerAndGame {
    private Player player;
    private MafiaGame mafiaGame;
}
