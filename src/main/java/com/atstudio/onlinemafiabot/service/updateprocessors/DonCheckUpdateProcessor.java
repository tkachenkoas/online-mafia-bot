package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.model.GamePhase.DON_SEARCH_COMISSAR;
import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;

@Component
@AllArgsConstructor
public class DonCheckUpdateProcessor extends AbstractUpdateProcessor {

    private final static String DON_CHECK_COMMAND = "/doncheck";

    private final MafiaGameRepository mafiaGameRepository;
    private final PlayerAndGameResolver playerAndGameResolver;

    @Override
    protected void process(Update update) {
        Chat chat = update.getMessage().getChat();
        Long chatId = chat.getId();
        if (!chat.isUserChat()) {
            sendMessage(chatId, messageProvider.getMessage("only_private_chat"));
            return;
        }
        PlayerAndGame playerAndGame = playerAndGameResolver.resolvePlayerAndGame(update);
        if (playerAndGame == null) {
            return;
        }
        MafiaGame mafiaGame = playerAndGame.getMafiaGame();
        Player player = playerAndGame.getPlayer();

        GameRole role = mafiaGame.getGameRolesByUser()
                .get(player.getLogin());
        if (mafiaGame.getPhase() != DON_SEARCH_COMISSAR) {
            sendMessage(chatId, messageProvider.getMessage("not_your_time"));
            return;
        }

        if (role != GameRole.DON_CORLEONE) {
            sendMessage(chatId, messageProvider.getMessage("not_applicable_for_your_role"));
            return;
        }

        String targetPlayerString = getMessageText(update).replaceAll(DON_CHECK_COMMAND, "").trim();
        if (!NumberUtils.isParsable(targetPlayerString)) {
            sendMessage(chatId, messageProvider.getMessage("unknown_number", targetPlayerString));
            return;
        }

        if (alreadyVoted(mafiaGame, player)) {
            sendMessage(chatId, messageProvider.getMessage("already_voted"));
            return;
        }

        addDonVoteEvent(mafiaGame, player, targetPlayerString);
    }

    private boolean addDonVoteEvent(MafiaGame mafiaGame, Player player, String targetPlayerString) {
        return mafiaGame.getNightEvents().add(
                NightEvent.builder()
                        .playerLogin(player.getLogin())
                        .action(NightAction.DON_CHECH)
                        .targetPlayer(Integer.parseInt(targetPlayerString))
                        .build()
        );
    }

    private boolean alreadyVoted(MafiaGame mafiaGame, Player player) {
        return mafiaGame.getNightEvents().stream()
                .anyMatch(event -> donVote(event, player));
    }

    private boolean donVote(NightEvent event, Player player) {
        return event.getPlayerLogin().equals(player.getLogin())
                && event.getAction() == NightAction.DON_CHECH;
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, DON_CHECK_COMMAND);
    }
}
