package com.atstudio.onlinemafiabot.telegram;

import com.atstudio.onlinemafiabot.service.updateprocessors.UpdateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Slf4j
public class UpdateHandler {

    private final List<UpdateProcessor> processors;

    @Autowired
    public UpdateHandler(List<UpdateProcessor> processors) {
        this.processors = processors;
    }

    public void handle(Update update) {
        log.info("Received update from telegram: {}", update);
        for (UpdateProcessor processor: processors) {
            if (processor.willTakeCareOf(update)) {
                return;
            }
        }
    }

}
