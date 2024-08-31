package com.anuragnepal.Telegrambot.Config;

import com.anuragnepal.Telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    private final TelegramBot telegramBot;

    public BotConfig(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Bean
    public String registerBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to register bot", e);
        }
        return "Registered Successfully";
    }
}
