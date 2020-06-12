package com.atstudio.onlinemafiabot;

import com.atstudio.onlinemafiabot.service.MessageProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.Locale;

@SpringBootApplication
public class OnlineMafiaBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(OnlineMafiaBotApplication.class, args);
    }

    @Bean
    public MessageProvider messageProvider() {
        return (code, args) -> messageSource().getMessage(code, args, Locale.ROOT);
    }

    @Bean
    protected ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
