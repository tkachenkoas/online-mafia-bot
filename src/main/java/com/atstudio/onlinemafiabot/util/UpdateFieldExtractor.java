package com.atstudio.onlinemafiabot.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class UpdateFieldExtractor {

    public static String getMessageText(Update update) {
        return getMessage(update).map(Message::getText).orElse(null);
    }

    public static Long getChatId(Update update) {
        return getMessage(update).map(Message::getChatId).orElse(null);
    }

    private static Optional<Message> getMessage(Update update) {
        Optional<Message> updateMessage = ofNullable(update).map(Update::getMessage);
        return updateMessage.isPresent() ? updateMessage : messageFromCallback(update);
    }

    private static Optional<Message> messageFromCallback(Update update) {
        return ofNullable(update)
                .map(Update::getCallbackQuery)
                .map(CallbackQuery::getMessage);
    }

}
