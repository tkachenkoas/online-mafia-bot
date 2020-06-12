package com.atstudio.onlinemafiabot.telegram

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

import static com.atstudio.onlinemafiabot.telegram.UpdateUtils.getUpdateFromFile
import static com.atstudio.onlinemafiabot.telegram.UpdateUtils.getUpdateWithMessage

@RunWith(SpringRunner)
@ContextConfiguration(classes = IntegrationTestConfig)
@DirtiesContext
class BotIntegrationTest {

    private static final Long DEFAULT_CHAT = 163655430;

    @Autowired
    private UpdateHandler underTest

    @Autowired
    List<BotApiMethod> executedMethods

    @Test
    void testWillShufflePlayers() {
        Update shuffleUpdate = getUpdateWithMessage("/shuffle Anton Artem Ivan Petrov Nikolaev")

        underTest.handle(shuffleUpdate)

        assert executedMethods.size() == 1
        def sendMessage = executedMethods.first() as SendMessage

        def text = sendMessage.getText()
        ['Anton', 'Artem', 'Ivan', 'Petrov', 'Nikolaev'].each {
            assert text.contains(it)
        }

        1..5.each {
            assert text.contains(it.toString())
        }
    }

    @Test
    void willGiveRoles() {

        // prepare players

        def players = (1..10).collect({
            [
                    'chatId': it,
                    'name'  : 'Player' + it,
                    'login' : '@login' + it
            ]
        })

        players.each {
            registerPlayer(it)
        }

        // when -> start game

        def startMessage = "/start_game" + players.collect({ it.login }).join(" ")

        underTest.handle(getUpdateWithMessage(startMessage))

        // then -> verify that each user received a message

        def sendMessages = executedMethods.collect({ it as SendMessage })

        // 10 x player messages + 1 x chat
        assert sendMessages.size() == 11

        assert sendMessages.find({ it.getChatId() == DEFAULT_CHAT }) != null

        List<String> roles = []

        players.collect({ it.chatId }).each {
            def userMessage = sendMessages.find({ it.getChatId() == it })
            assert userMessage != null
            roles.add(userMessage.getText())
        }

        assert roles.findAll({ it.contains('Don')}).size() == 1
        assert roles.findAll({ it.contains('Komissar')}).size() == 1
        assert roles.findAll({ it.contains('Mafia')}).size() == 2
        assert roles.findAll({ it.contains('Citizen')}).size() == 6
    }

    @Test
    void wontGiveRolesIfUserNotRegistered() {

        // prepare players

        def players = (1..10).collect({
            [
                    'chatId': it,
                    'name'  : 'Player' + it,
                    'login' : '@login' + it
            ]
        })

        players.each {
            registerPlayer(it)
        }

        // when -> start game

        def startMessage = "/start_game" + players.collect({ it.login }).join(" ") + " @unknown"

        underTest.handle(getUpdateWithMessage(startMessage))

        // then -> verify that each user received a message

        def sendMessages = executedMethods.collect({ it as SendMessage })

        // Can't start game with unknown users
        assert sendMessages.size() == 1
        assert sendMessages.first().getText().contains("Unknown")
    }

    def registerPlayer(def player) {
        def update = getUpdateFromFile()
        update.message.chat.id = player.chatId
        update.message.from.userName = player.login
        update.message.from.firstName = player.name
        underTest.handle(update)
        executedMethods.clear()
    }
}