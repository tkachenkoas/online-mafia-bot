package com.atstudio.onlinemafiabot.repository;

import com.atstudio.onlinemafiabot.model.MafiaGame;
import com.atstudio.onlinemafiabot.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MafiaGameRepository extends MongoRepository<MafiaGame, String> {
    Optional<MafiaGame> findOneByChatId(Long chatId);
    void deleteAllByChatId(Long chatId);
}
