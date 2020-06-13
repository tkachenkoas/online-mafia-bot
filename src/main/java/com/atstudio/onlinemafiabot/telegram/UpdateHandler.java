package com.atstudio.onlinemafiabot.telegram;

import com.atstudio.onlinemafiabot.service.updateprocessors.UpdateProcessor;
import com.atstudio.onlinemafiabot.util.UpdateFieldExtractor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getChatId;

@AllArgsConstructor
@Component
@Slf4j
public class UpdateHandler {

    private final List<UpdateProcessor> processors;
    private final TgApiExecutor executor;

    public void handle(Update update) {
        log.info("Received update from telegram: {}", update);
        for (UpdateProcessor processor : processors) {
            try {
                if (processor.willTakeCareOf(update)) {
                    return;
                }
            } catch (BotMisUseException exc) {
                executor.execute(
                        new SendMessage(getChatId(update), exc.getMessage())
                );
                return;
            } catch (Exception e) {
                log.warn("Exception when handling update: " + e.getMessage(), e);
            }
        }
    }

}
