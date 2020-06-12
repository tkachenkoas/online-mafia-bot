package com.atstudio.onlinemafiabot.repository;

import com.atstudio.onlinemafiabot.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

}
