package com.atstudio.onlinemafiabot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NightEvent {
    private String playerLogin;
    private NightAction action;
    private Integer targetPlayer;
}
