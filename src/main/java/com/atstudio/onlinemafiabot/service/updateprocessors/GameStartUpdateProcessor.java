package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.GameRole;
import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.model.Player;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import com.atstudio.onlinemafiabot.repository.PlayerRepository;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.service.updateprocessors.start.PlayerShuffler;
import com.atstudio.onlinemafiabot.service.updateprocessors.start.RolesAssigner;
import com.atstudio.onlinemafiabot.service.updateprocessors.start.RolesInformer;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.atstudio.onlinemafiabot.model.GamePhase.FIRST_DAY;
import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Component
public class GameStartUpdateProcessor extends AbstractUpdateProcessor {

    private static final String GAME_START_COMMAND = "/start_game";

    private final PlayerRepository playerRepository;
    private final MafiaGameRepository mafiaGameRepository;
    private final MessageProvider messageProvider;
    private final TgApiExecutor executor;
    private final RolesAssigner rolesAssigner;
    private final RolesInformer rolesInformer;
    private final PlayerShuffler playerShuffler;

    @Override
    protected void process(Update update) {
        String text = getMessageText(update).replace(GAME_START_COMMAND, "");
        List<String> userLogins = extractUserLogins(text);

        List<Player> playersList = getPlayerListByLogins(userLogins);
        Long chatId = getChatId(update);
        if (playersList.size() != userLogins.size()) {
            reportUnregisteredUser(userLogins, playersList, chatId);
            return;
        }

        Map<Player, Integer> numberedPlayers = playerShuffler.shuffleAndNotifyChat(chatId, playersList);

        Map<Player, GameRole> gameRoles = rolesAssigner.assignRoles(playersList);

        rolesInformer.informUsersOfTheirRoles(gameRoles);

        playersList.forEach(player -> {
            Integer numberInGame = numberedPlayers.get(player);
            Assert.notNull(numberInGame, "Player number resolving error");
            player.setNumberInCurrentGame(numberInGame);
        });

        MafiaGame mafiaGame = mafiaGameRepository.save(buildGame(chatId, gameRoles));

        playersList.forEach(player -> {
            player.setCurrentGameId(mafiaGame.getGameId());
            playerRepository.save(player);
        });

        notifyChatOnGameStart(chatId);
    }

    private List<Player> getPlayerListByLogins(List<String> userLogins) {
        return StreamSupport
                .stream(playerRepository.findAllById(userLogins).spliterator(), false)
                .collect(toList());
    }

    private List<String> extractUserLogins(String text) {
        return Stream.of(text.split(" "))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(toList());
    }

    private MafiaGame buildGame(Long chatId, Map<Player, GameRole> gameRoles) {
        return MafiaGame.builder()
                .chatId(chatId)
                .phase(FIRST_DAY)
                .playerNumbersByUser(playerNumberByUser(gameRoles))
                .gameRolesByPlayerNumber(gameRolesByPlayerNum(gameRoles))
                .build();
    }

    private void notifyChatOnGameStart(Long chatId) {
        sendMessage(chatId, messageProvider.getMessage("game_start"));
    }

    private Map<Integer, GameRole> gameRolesByPlayerNum(Map<Player, GameRole> gameRoles) {
        Map<Integer, GameRole> result = new HashMap<>();
        gameRoles.forEach((key, value) -> result.put(key.getNumberInCurrentGame(), value));
        return result;
    }

    private Map<String, Integer> playerNumberByUser(Map<Player, GameRole> gameRoles) {
        Map<String, Integer> result = new HashMap<>();
        gameRoles.forEach((key, value) -> result.put(key.getLogin(), key.getNumberInCurrentGame()));
        return result;
    }

    private void reportUnregisteredUser(List<String> userLogins, List<Player> playersList, Long chatId) {
        Set<String> existing = playersList.stream().map(Player::getLogin).collect(Collectors.toSet());
        Set<String> missing = userLogins.stream().filter(existing::contains).collect(Collectors.toSet());
        String missingMessage = messageProvider.getMessage(
                "unregistered_players", String.join(";", missing)
        );
        executor.execute(new SendMessage(chatId, missingMessage));
    }

    @Override
    protected boolean applicableFor(Update update) {
        return messageContains(update, GAME_START_COMMAND);
    }
}
