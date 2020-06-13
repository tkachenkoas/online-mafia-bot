package com.atstudio.onlinemafiabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class OnlineMafiaBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(OnlineMafiaBotApplication.class, args);
    }
}
