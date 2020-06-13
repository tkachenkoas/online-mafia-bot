package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.model.Player;
import com.atstudio.onlinemafiabot.model.PlayerAndGame;
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository;
import com.atstudio.onlinemafiabot.repository.PlayerRepository;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.BotMisUseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class PlayerAndGameResolver {

    private final MessageProvider messageProvider;
    private final MafiaGameRepository mafiaGameRepository;
    private final PlayerRepository playerRepository;

    public PlayerAndGame resolvePlayerAndGame(Update update) {
        String userName = update.getMessage().getFrom().getUserName();
        Optional<Player> optionalPlayer = playerRepository.findById(userName);
        if (!optionalPlayer.map(Player::getCurrentGameId).isPresent()) {
            throw new BotMisUseException(messageProvider.getMessage("player_not_in_game", userName));
        }

        Player currentPlayer = optionalPlayer.get();
        Optional<MafiaGame> optionalGame = mafiaGameRepository.findById(currentPlayer.getCurrentGameId());
        if (!optionalGame.isPresent()) {
            log.warn("No game found for player: {} and game id {}", currentPlayer.getLogin(), currentPlayer.getCurrentGameId());
            throw new BotMisUseException(messageProvider.getMessage("player_not_in_game", userName));
        }
        return new PlayerAndGame(currentPlayer, optionalGame.get());
    }
}
