package org.klozevitz.components.io.historyManager;

import org.klozevitz.enities.users.AppUser;
import org.klozevitz.enities.users.MessageSent;
import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface IHistoryManager {
    void registerBot(GatewayTgBotComponent bot);
    void deleteUserMessage(Update update);
    void deleteBotMessages(List<MessageSent> messagesSent);
    void saveMessageToDelete(Integer messageId, SendMessage sendMessage);
}
