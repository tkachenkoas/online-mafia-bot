package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;

public abstract class AbstractUpdateProcessor implements UpdateProcessor {

    @Autowired
    protected TgApiExecutor executor;

    @Autowired
    protected MessageProvider messageProvider;

    @Override
    public boolean willTakeCareOf(Update update) {
        if (applicableFor(update)) {
            process(update);
            return true;
        }
        return false;
    }

    protected abstract void process(Update update);

    protected abstract boolean applicableFor(Update update);

    protected boolean messageContains(Update update, String searchString) {
        return StringUtils.contains(getMessageText(update), searchString);
    }

    protected void sendMessage(Long chatId, String message) {
        executor.execute(
                new SendMessage(
                        chatId,
                        message
                )
        );
    }
}
