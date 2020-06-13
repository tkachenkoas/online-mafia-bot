package com.atstudio.onlinemafiabot.service.gameinfo;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.BotMisUseException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;
import static java.util.Arrays.asList;

@Component
@AllArgsConstructor
public class StateValidator {

    private final MessageProvider messageProvider;

    public void verifyThatChatIsPrivate(Update update) {
        Chat chat = update.getMessage().getChat();
        if (!chat.isUserChat()) {
            throw new BotMisUseException(messageProvider.getMessage("only_private_chat"));
        }
    }

    public void assertThatGamePhaseIs(MafiaGame mafiaGame, GamePhase phase) {
        if (mafiaGame.getPhase() != phase) {
            throw new BotMisUseException(messageProvider.getMessage("not_your_time"));
        }
    }

    public Integer extractTargetPlayerFromCommand(Update update, String commandPrefix) {
        String targetPlayerString = getMessageText(update).replaceAll(commandPrefix, "").trim();
        if (!NumberUtils.isParsable(targetPlayerString)) {
            throw new BotMisUseException(messageProvider.getMessage("unknown_command", targetPlayerString));
        }
        return Integer.parseInt(targetPlayerString);
    }

    public void assertThatUserRoleIsOneOf(PlayerAndGame playerAndGame, List<GameRole> roles) {
        String login = playerAndGame.getPlayer().getLogin();
        MafiaGame mafiaGame = playerAndGame.getMafiaGame();

        Integer playerNumber = mafiaGame.getPlayerNumbersByUser().get(login);

        GameRole playerRole = mafiaGame
                .getGameRolesByPlayerNumber()
                .get(playerNumber);
        if (!roles.contains(playerRole)) {
            throw new BotMisUseException(messageProvider.getMessage("not_applicable_for_your_role"));
        }
    }

    public void assertThatUserRoleIsOneOf(PlayerAndGame playerAndGame, GameRole role) {
        assertThatUserRoleIsOneOf(playerAndGame, asList(role));
    }

    public void assertThatUserDidNotPerformAction(PlayerAndGame playerAndGame, NightAction action) {
        Player player = playerAndGame.getPlayer();
        boolean alreadyVoted = playerAndGame.getMafiaGame().getNightEvents().stream()
                .filter(event -> event.getPlayerLogin().equals(player.getLogin()))
                .anyMatch(event -> event.getAction() == action);
        if (alreadyVoted) {
            throw new BotMisUseException(messageProvider.getMessage("already_voted"));
        }
    }


}
