package com.atstudio.onlinemafiabot.service.updateprocessors;

import com.atstudio.onlinemafiabot.telegram.TgApiExecutor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.onlinemafiabot.util.UpdateFieldExtractor.getMessageText;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Component
public class ShufflePlayersUpdateProcessor extends AbstractUpdateProcessor {

    private static final String SHUFFLE_TEXT = "/shuffle";

    private final TgApiExecutor executor;

    @Override
    protected void process(Update update) {
        String text = getMessageText(update).replace(SHUFFLE_TEXT, "");
        List<String> players = Stream.of(text.split(" "))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(toList());

        Collections.shuffle(players);

        List<String> numberedPlayers = getNumberedPlayers(players);

        String shuffledText = StringUtils.joinWith("\n", numberedPlayers);

        SendMessage message = new SendMessage(
                getChatId(update),
                shuffledText
        );
        executor.execute(message);
    }

    private List<String> getNumberedPlayers(List<String> players) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            result.add(i + " -> " + players.get(i));
        }
        return result;
    }

    @Override
    protected boolean applicableFor(Update update) {
        return StringUtils.contains(getMessageText(update), SHUFFLE_TEXT);
    }
}
