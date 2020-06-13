package com.atstudio.onlinemafiabot.telegram

import com.atstudio.onlinemafiabot.model.GameRole
import com.atstudio.onlinemafiabot.model.MafiaGame
import com.atstudio.onlinemafiabot.model.Player
import com.atstudio.onlinemafiabot.repository.MafiaGameRepository
import com.atstudio.onlinemafiabot.repository.PlayerRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

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

        assert roles.findAll({ it.contains('Don') }).size() == 1
        assert roles.findAll({ it.contains('Komissar') }).size() == 1
        assert roles.findAll({ it.contains('Mafia') }).size() == 2
        assert roles.findAll({ it.contains('Citizen') }).size() == 6
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
        assert sendMessages.first().getText().contains("unknown")
    }

    @Test
    void onStartGameWillRemoveAllOther() {

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

        // start game
        def startMessage = "/start_game" + players.collect({ it.login }).join(" ")
        underTest.handle(getUpdateWithMessage(startMessage))

        // start game again
        underTest.handle(getUpdateWithMessage(startMessage))

        assert gameRepository.findAll().size() == 1
    }

    @Autowired
    private MafiaGameRepository gameRepository

    @Autowired
    private PlayerRepository playerRepository

    @Test
    void hugeStupidTest() {
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

        // start game
        def startMessage = "/start_game" + players.collect({ it.login }).join(" ")
        underTest.handle(getUpdateWithMessage(startMessage))

        // night time
        MafiaGame game = gameRepository.findOneByChatId(DEFAULT_CHAT).get()
        underTest.handle(getUpdateWithMessage("/first_night"))

        // mafia -> find all mafias
        Map<Integer, GameRole> mafiaNumbers = game.getGameRolesByPlayerNumber().findAll {
            it.value.isMafia()
        }

        Map<String, Integer> mafiaLoginToNumber = game.getPlayerNumbersByUser().findAll {
            mafiaNumbers.containsKey(it.value)
        }

        def mafiaPlayers = playerRepository.findAllById(mafiaLoginToNumber.keySet())

        // mafia -> make 3 votes
        def willKill = randomNotMafia(mafiaNumbers)
        mafiaPlayers.each {
            executeCommandFromPlayer(it, "/kill ${willKill}")
        }

        // don -> make the check
        Player don = mafiaPlayers.find({ mafiaNumbers.get(it.getNumberInCurrentGame()) == GameRole.DON_CORLEONE })
        executeCommandFromPlayer(don, "/doncheck ${randomNotMafia(mafiaNumbers)}")

        // komissar -> make the check
        def allPlayers = playerRepository.findAll()
        def komissar = allPlayers.find {
            def numberInGame = game.getPlayerNumbersByUser().get(it.login)
            return game.getGameRolesByPlayerNumber().get(numberInGame) == GameRole.KOMISSAR_REX
        }
        executeCommandFromPlayer(komissar, "/komcheck ${new Random().nextInt(10) + 1}")

        executedMethods.each { SendMessage message ->
            println(message.getText())
        }
    }

    private Object randomNotMafia(Map<Integer, GameRole> mafiaNumbers) {
        def willKill = new Random().nextInt(10) + 1
        while (mafiaNumbers.keySet().contains(willKill)) {
            willKill = new Random().nextInt(10) + 1
        }
        willKill
    }

    def registerPlayer(def player) {
        executeCommandFromPlayer(player as Player, '/register')
        executedMethods.clear()
    }

    def executeCommandFromPlayer(Player player, String command) {
        def update = getUpdateFromFile()
        update.message.text = command
        update.message.chat.id = player.chatId
        update.message.chat.type = "private"
        update.message.from.userName = player.login
        update.message.from.firstName = player.name
        underTest.handle(update)
    }
}