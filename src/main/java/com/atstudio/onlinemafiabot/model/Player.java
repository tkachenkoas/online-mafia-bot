package com.atstudio.onlinemafiabot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@Builder
public class Player {

    @Id
    private String login;
    private String name;
    private Long chatId;

}
