package com.atstudio.onlinemafiabot.telegram;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

public interface TgApiExecutor {

    <Result extends Serializable, Method extends BotApiMethod<Result>> Result execute(Method method);

}