package com.atstudio.onlinemafiabot.service.updateprocessors.start;

import com.atstudio.onlinemafiabot.model.GameRole;
import com.atstudio.onlinemafiabot.model.Player;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.atstudio.onlinemafiabot.model.GameRole.*;

@Component
public class RolesAssigner {

    private static final List<GameRole> ROLES_PRIORITY_ORDER = Arrays.asList(
            DON_CORLEONE,
            KOMISSAR_REX,
            MAFIA_COMMON,
            MAFIA_COMMON,
            CITIZEN,
            CITIZEN,
            CITIZEN,
            CITIZEN,
            MAFIA_COMMON,
            CITIZEN,
            MAFIA_COMMON
    );

    public Map<Player, GameRole> assignRoles(List<Player> playersList) {
        List<GameRole> rolesToAssign = new ArrayList<>(ROLES_PRIORITY_ORDER.subList(0, playersList.size()));
        Collections.shuffle(rolesToAssign);

        Map<Player, GameRole> result = new HashMap<>();
        for (int index = 0; index < playersList.size(); index++) {
            result.put(playersList.get(index), rolesToAssign.get(index));
        }

        return result;
    }
}
