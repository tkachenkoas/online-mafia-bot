package com.atstudio.onlinemafiabot.service.updateprocessors;

import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractUpdateProcessor implements UpdateProcessor {

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
}
