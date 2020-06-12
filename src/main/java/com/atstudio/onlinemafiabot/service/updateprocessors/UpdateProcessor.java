package com.atstudio.onlinemafiabot.service.updateprocessors;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessor {

    boolean willTakeCareOf(Update update);

}
