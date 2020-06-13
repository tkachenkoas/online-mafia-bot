package com.atstudio.onlinemafiabot.service.updateprocessors.firstnight;

import com.atstudio.onlinemafiabot.model.*;
import com.atstudio.onlinemafiabot.service.gameinfo.EventProcessor;
import com.atstudio.onlinemafiabot.service.gameinfo.StateValidator;
import com.atstudio.onlinemafiabot.service.updateprocessors.AbstractUpdateProcessor;
import com.atstudio.onlinemafiabot.service.updateprocessors.PlayerAndGameResolver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.model.GamePhase.DON_SEARCH_COMISSAR;
import static com.atstudio.onlinemafiabot.model.GameRole.DON_CORLEONE;
import static com.atstudio.onlinemafiabot.model.GameRole.KOMISSAR_REX;

@Component
@AllArgsConstructor
public class DonCheckUpdateProcessor extends AbstractUpdateProcessor {

    private final static String DON_CHECK_COMMAND = "/doncheck";

    private final PlayerAndGameResolver playerAndGameResolver;
    private final StateValidator stateValidator;
    private final EventProcessor eventProcessor;

    @Override
    protected void process(Update update) {
        stateValidator.verifyThatChatIsPrivate(update);
        PlayerAndGame playerAndGame = playerAndGameResolver.resolvePlayerAndGame(update);
        stateValidator.assertThatGamePhaseIs(playerAndGame.getMafiaGame(), DON_SEARCH_COMISSAR);
        stateValidator.assertThatUserRoleIsOneOf(playerAndGame, DON_CORLEONE);
        stateValidator.assertThatUserDidNotPerformAction(playerAndGame, NightAction.DON_CHECH);

        Integer targetPlayerNumber = stateValidator.extractTargetPlayerFromCommand(update, DON_CHECK_COMMAND);
        eventProcessor.addEventToGame(playerAndGame, targetPlayerNumber, NightAction.DON_CHECH);

        notifyDonOnCheckResult(playerAndGame, targetPlayerNumber);

        eventProcessor.changeGamePhase(playerAndGame.getMafiaGame(), GamePhase.COMISSAR_SEARCH_MAFIA);
        notifyOnDonFinishedCheck(playerAndGame.getMafiaGame());
    }

    private void notifyDonOnCheckResult(PlayerAndGame playerAndGame, Integer targetPlayerNumber) {
        GameRole chechedPlayerRole = playerAndGame.getMafiaGame().getGameRolesByPlayerNumber().get(targetPlayerNumber);
        String messageString = chechedPlayerRole == KOMISSAR_REX
                ? "found_comissar"
                : "keep_searching";
        sendMessage(playerAndGame.getPlayer().getChatId(), messageProvider.getMessage(messageString));
    }

    private void notifyOnDonFinishedCheck(MafiaGame mafiaGame) {
        sendMessage(mafiaGame.getChatId(), messageProvider.getMessage("don_finished"));
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, DON_CHECK_COMMAND);
    }
}
