package com.atstudio.onlinemafiabot.service.updateprocessors.firstnight;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import com.atstudio.onlinemafiabot.service.gameinfo.EventProcessor;
import com.atstudio.onlinemafiabot.service.gameinfo.GameInfoProvider;
import com.atstudio.onlinemafiabot.service.gameinfo.StateValidator;
import com.atstudio.onlinemafiabot.service.updateprocessors.AbstractUpdateProcessor;
import com.atstudio.onlinemafiabot.service.updateprocessors.PlayerAndGameResolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.model.GamePhase.DON_SEARCH_COMISSAR;
import static com.atstudio.onlinemafiabot.model.GamePhase.MAFIA_HUNT_TIME;
import static com.atstudio.onlinemafiabot.model.GameRole.DON_CORLEONE;
import static com.atstudio.onlinemafiabot.model.GameRole.MAFIA_COMMON;
import static com.atstudio.onlinemafiabot.model.NightAction.MAFIA_VOTE;
import static java.util.Arrays.asList;

@AllArgsConstructor
@Component
@Slf4j
public class MafiaVoteUpdateProcessor extends AbstractUpdateProcessor {

    private final static String MAFIA_KILL_COMMAND = "/kill";

    private final MafiaGameRepository mafiaGameRepository;
    private final PlayerAndGameResolver playerAndGameResolver;
    private final StateValidator stateValidator;
    private final EventProcessor eventProcessor;
    private final GameInfoProvider gameInfoProvider;

    @Override
    protected void process(Update update) {
        stateValidator.verifyThatChatIsPrivate(update);
        PlayerAndGame playerAndGame = playerAndGameResolver.resolvePlayerAndGame(update);
        MafiaGame mafiaGame = playerAndGame.getMafiaGame();

        stateValidator.assertThatGamePhaseIs(playerAndGame.getMafiaGame(), MAFIA_HUNT_TIME);
        stateValidator.assertThatUserRoleIsOneOf(playerAndGame, asList(MAFIA_COMMON, DON_CORLEONE));
        Integer targetPlayerNumber = stateValidator.extractTargetPlayerFromCommand(update, MAFIA_KILL_COMMAND);

        stateValidator.assertThatUserDidNotPerformAction(playerAndGame, MAFIA_VOTE);
        eventProcessor.addEventToGame(playerAndGame, targetPlayerNumber, MAFIA_VOTE);

        checkIfAllMafiaVoted(mafiaGame);
    }

    private void checkIfAllMafiaVoted(MafiaGame mafiaGame) {
        long mafiaCount = gameInfoProvider.getMafiaCount(mafiaGame);
        long mafiaVotes = mafiaGame.getNightEvents().stream()
                .filter(event -> event.getAction() == MAFIA_VOTE)
                .count();
        if (mafiaCount == mafiaVotes) {
            eventProcessor.changeGamePhase(mafiaGame, DON_SEARCH_COMISSAR);
            sendMessage(mafiaGame.getChatId(), messageProvider.getMessage("mafia_finished"));
        }
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, MAFIA_KILL_COMMAND);
    }
}
