package com.atstudio.onlinemafiabot.service.updateprocessors.firstnight;

import com.atstudio.onlinemafiabot.model.GameRole;
import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.model.PlayerAndGame;
import com.atstudio.onlinemafiabot.service.gameinfo.EventProcessor;
import com.atstudio.onlinemafiabot.service.gameinfo.NightResultAnnouncer;
import com.atstudio.onlinemafiabot.service.gameinfo.StateValidator;
import com.atstudio.onlinemafiabot.service.updateprocessors.AbstractUpdateProcessor;
import com.atstudio.onlinemafiabot.service.updateprocessors.PlayerAndGameResolver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.onlinemafiabot.model.GamePhase.COMISSAR_SEARCH_MAFIA;
import static com.atstudio.onlinemafiabot.model.GameRole.KOMISSAR_REX;
import static com.atstudio.onlinemafiabot.model.NightAction.KOMISSAR_CHECK;

@Component
@AllArgsConstructor
public class KomissarCheckUpdateProccessor extends AbstractUpdateProcessor {

    private final static String KOMISSAR_CHECK_COMMAND = "/komcheck";

    private final PlayerAndGameResolver playerAndGameResolver;
    private final StateValidator stateValidator;
    private final EventProcessor eventProcessor;
    private final NightResultAnnouncer resultAnnouncer;

    @Override
    protected void process(Update update) {
        stateValidator.verifyThatChatIsPrivate(update);
        PlayerAndGame playerAndGame = playerAndGameResolver.resolvePlayerAndGame(update);
        stateValidator.assertThatGamePhaseIs(playerAndGame.getMafiaGame(), COMISSAR_SEARCH_MAFIA);
        stateValidator.assertThatUserRoleIsOneOf(playerAndGame, KOMISSAR_REX);
        stateValidator.assertThatUserDidNotPerformAction(playerAndGame, KOMISSAR_CHECK);

        Integer targetPlayerNumber = stateValidator.extractTargetPlayerFromCommand(update, KOMISSAR_CHECK_COMMAND);
        eventProcessor.addEventToGame(playerAndGame, targetPlayerNumber, KOMISSAR_CHECK);

        notifyKomissarOnCheckResult(playerAndGame, targetPlayerNumber);

        notifyOnKomissarFinishedCheck(playerAndGame.getMafiaGame());
    }

    private void notifyKomissarOnCheckResult(PlayerAndGame playerAndGame, Integer targetPlayerNumber) {
        GameRole chechedPlayerRole = playerAndGame.getMafiaGame().getGameRolesByPlayerNumber().get(targetPlayerNumber);
        String messageString = chechedPlayerRole.isMafia()
                ? "found_mafia"
                : "found_citizen";
        sendMessage(playerAndGame.getPlayer().getChatId(), messageProvider.getMessage(messageString));
    }

    private void notifyOnKomissarFinishedCheck(MafiaGame mafiaGame) {
        sendMessage(mafiaGame.getChatId(), messageProvider.getMessage("komissar_finished"));
        resultAnnouncer.announceNightResult(mafiaGame);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, KOMISSAR_CHECK_COMMAND);
    }
}
