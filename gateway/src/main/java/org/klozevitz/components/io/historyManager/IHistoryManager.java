package org.klozevitz.components.io.historyManager;

import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public interface IHistoryManager {
    void registerBot(GatewayTgBotComponent bot);

    void deleteMessage(long telegramUserId, int messageId);

    void deleteBotMessages(ArrayList<Integer> messagesToDelete, long telegramUserId);

    void saveBotMessageToDelete(Integer messageId, BotApiMethodMessage sendMessage);

    void saveUserMessageToDelete(Update update);
}
