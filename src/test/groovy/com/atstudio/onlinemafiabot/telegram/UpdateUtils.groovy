package com.atstudio.onlinemafiabot.telegram

import groovy.json.JsonSlurper
import org.telegram.telegrambots.meta.api.objects.Update

class UpdateUtils {

    static Update getUpdateWithMessage(String message) {
        def update = getUpdateFromFile()

        update.message.text = message

        return update;
    }

    static Update getUpdateFromFile() {
        return new JsonSlurper().parse(
                this.getResourceAsStream("/plain-message-update.json") as InputStream
        ) as Update
    }

}
