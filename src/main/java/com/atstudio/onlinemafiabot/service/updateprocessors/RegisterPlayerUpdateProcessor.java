package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.Player;
import com.atstudio.onlinemafiabot.repository.PlayerRepository;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;

@AllArgsConstructor
@Component
public class RegisterPlayerUpdateProcessor extends AbstractUpdateProcessor {

    private static final String REGISTER_MESSAGE = "/register";

    private final PlayerRepository repository;
    private final TgApiExecutor executor;
    private final MessageProvider messageProvider;

    @Override
    protected void process(Update update) {
        Chat chat = update.getMessage().getChat();
        Long chatId = chat.getId();
        if (!chat.isUserChat()) {
            sendCantRegisterInPublicChat(chatId);
            return;
        }

        User user = update.getMessage().getFrom();
        Player player = Player.builder()
                .chatId(chatId)
                .login(user.getUserName())
                .name(StringUtils.trim(user.getFirstName() + " " + user.getLastName()))
                .build();

        repository.save(player);
        sendPlayerRegisteredMessage(player);
    }

    private void sendPlayerRegisteredMessage(Player player) {
        executor.execute(
                new SendMessage(
                        player.getChatId(),
                        messageProvider.getMessage("register_success", "@" + player.getLogin(), player.getName())
                )
        );
    }

    private void sendCantRegisterInPublicChat(Long chatId) {
        executor.execute(
                new SendMessage(chatId, messageProvider.getMessage("register_in_no_private_chat"))
        );
    }

    @Override
    protected boolean applicableFor(Update update) {
        return StringUtils.contains(getMessageText(update), REGISTER_MESSAGE);
    }
}
