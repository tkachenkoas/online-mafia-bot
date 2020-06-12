package com.atstudio.onlinemafiabot.service.updateprocessors.start;

import com.atstudio.onlinemafiabot.model.GameRole;
import com.atstudio.onlinemafiabot.model.Player;
import com.atstudio.onlinemafiabot.service.MessageProvider;
import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Component
public class RolesInformer {

    private final MessageProvider messageProvider;
    private final TgApiExecutor executor;

    private final static Map<GameRole, String> ROLE_TO_STRING = ImmutableMap.of(
            GameRole.CITIZEN, "citizen",
            GameRole.DON_CORLEONE, "don_corleone",
            GameRole.KOMISSAR_REX, "komissar",
            GameRole.MAFIA_COMMON, "mafia_common"
    );

    public void informUsersOfTheirRoles(Map<Player, GameRole> gameRoles) {
        Set<Map.Entry<Player, GameRole>> entries = gameRoles.entrySet();
        for (Map.Entry<Player, GameRole> entry : entries) {
            String roleString = messageProvider.getMessage(
                    ROLE_TO_STRING.get(entry.getValue())
            );
            Player player = entry.getKey();
            executor.execute(
                    new SendMessage(
                            player.getChatId(),
                            messageProvider.getMessage("your_role_is", roleString)
                    )
            );
        }
    }
}
