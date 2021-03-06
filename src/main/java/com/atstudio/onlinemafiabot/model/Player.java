package com.atstudio.onlinemafiabot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {

    @Id
    private String login;
    private String name;
    private Long chatId;

    // current game state
    private String currentGameId;
    private Integer numberInCurrentGame;

}
