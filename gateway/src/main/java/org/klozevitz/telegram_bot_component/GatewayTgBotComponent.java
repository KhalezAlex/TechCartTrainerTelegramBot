package org.klozevitz.telegram_bot_component;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.klozevitz.components.UpdateController;
import org.klozevitz.components.io.historyManager.IHistoryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Log4j
@Setter
@RequiredArgsConstructor
public class GatewayTgBotComponent extends TelegramLongPollingBot {
    @Value(value = "${bot.username}")
    private String username;
    @Value(value = "${bot.token}")
    private String token;
    private final UpdateController updateController;
    private final IHistoryManager historyManager;

    @PostConstruct
    private void init() {
        updateController.registerBot(this);
        historyManager.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("получено сообщение!!!" + "telegramUserId");
        updateController.processUpdate(update);
        historyManager.deleteUserMessage(update);
    }

    public Message sendAnswerMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try {
                return execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
        return null;
    }
}
