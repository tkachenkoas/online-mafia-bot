package com.atstudio.onlinemafiabot.telegram

import com.atstudio.onlinemafiabot.OnlineMafiaBotApplication
import com.atstudio.onlinemafiabot.service.MessageProvider
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Configuration
@ComponentScan(basePackages = "com.atstudio.onlinemafiabot", excludeFilters = [
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = OnlineMafiaBotApplication.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TgApiExecutorImpl.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebhookController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MafiaTelegramBot.class)
])
@DataMongoTest
@EnableMongoRepositories(basePackages = "com.atstudio.onlinemafiabot")
@EnableAutoConfiguration
class IntegrationTestConfig {

    @Bean
    TgApiExecutor executor(List<BotApiMethod> lastExecuted) {
        TgApiExecutor result = mock(TgApiExecutor)
        when(result.execute(any())).thenAnswer({ inv ->
                lastExecuted.add(inv.getArgument(0))
        return null;
        })
        return result
    }

    @Bean
    List<BotApiMethod> executedMethods() {
        return [] as LinkedList
    }

    //@Bean
    MessageProvider messageProvider() {
        return mock(MessageProvider)
    }

}
