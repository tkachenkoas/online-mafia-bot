package com.atstudio.onlinemafiabot.model;

public enum GameRole {

    CITIZEN,
    KOMISSAR_REX,
    MAFIA_COMMON,
    DON_CORLEONE;

    public boolean isMafia() {
        return this == MAFIA_COMMON || this == DON_CORLEONE;
    }

}
