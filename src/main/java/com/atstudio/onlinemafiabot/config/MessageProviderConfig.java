package com.atstudio.onlinemafiabot.config;

import com.atstudio.onlinemafiabot.service.MessageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MessageProviderConfig {

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
