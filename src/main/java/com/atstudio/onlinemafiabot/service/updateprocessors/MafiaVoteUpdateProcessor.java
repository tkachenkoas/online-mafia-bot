package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.model.GamePhase.DON_SEARCH_COMISSAR;
import static com.atstudio.onlinemafiabot.model.GamePhase.MAFIA_VOTE;
import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;

@AllArgsConstructor
@Component
@Slf4j
public class MafiaVoteUpdateProcessor extends AbstractUpdateProcessor {

    private final static String MAFIA_VOTE_COMMAND = "/vote";

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
        if (mafiaGame.getPhase() != MAFIA_VOTE) {
            sendMessage(chatId, messageProvider.getMessage("not_your_time"));
            return;
        }

        if (!isMafia(role)) {
            sendMessage(chatId, messageProvider.getMessage("not_applicable_for_your_role"));
            return;
        }

        String targetPlayerString = getMessageText(update).replaceAll(MAFIA_VOTE_COMMAND, "").trim();
        if (!NumberUtils.isParsable(targetPlayerString)) {
            sendMessage(chatId, messageProvider.getMessage("unknown_number", targetPlayerString));
            return;
        }

        if (alreadyVoted(mafiaGame, player)) {
            sendMessage(chatId, messageProvider.getMessage("already_voted"));
            return;
        }

        addMafiaVoteEvent(mafiaGame, player, targetPlayerString);

        checkIfAllMafiaVoted(mafiaGame);
    }

    private boolean isMafia(GameRole role) {
        return role == GameRole.MAFIA_COMMON
                || role == GameRole.DON_CORLEONE;
    }

    private void checkIfAllMafiaVoted(MafiaGame mafiaGame) {
        long mafiaCount = mafiaGame.getGameRolesByUser()
                .values().stream()
                .filter(this::isMafia).count();
        long mafiaVotes = mafiaGame.getNightEvents().stream()
                .filter(event -> event.getAction() == NightAction.MAFIA_VOTE)
                .count();
        if (mafiaCount == mafiaVotes) {
            mafiaGame.setPhase(DON_SEARCH_COMISSAR);
            mafiaGameRepository.save(mafiaGame);
            sendMessage(mafiaGame.getChatId(), messageProvider.getMessage("mafia_finished"));
        }
    }

    private boolean addMafiaVoteEvent(MafiaGame mafiaGame, Player player, String targetPlayerString) {
        return mafiaGame.getNightEvents().add(
            NightEvent.builder()
                .playerLogin(player.getLogin())
                .action(NightAction.MAFIA_VOTE)
                .targetPlayer(Integer.parseInt(targetPlayerString))
                .build()
        );
    }

    private boolean alreadyVoted(MafiaGame mafiaGame, Player player) {
        return mafiaGame.getNightEvents().stream()
                .anyMatch(event -> mafiaVote(event, player));
    }

    private boolean mafiaVote(NightEvent event, Player player) {
        return event.getPlayerLogin().equals(player.getLogin())
                && event.getAction() == NightAction.MAFIA_VOTE;
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, MAFIA_VOTE_COMMAND);
    }
}
