package org.klozevitz.components;

import lombok.extern.log4j.Log4j;
import org.klozevitz.components.io.historyManager.IHistoryManager;
import org.klozevitz.components.io.updateProducer.IUpdateProducer;
import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.klozevitz.RabbitQueue.GATEWAY_QUEUE;

@Log4j
@Component
public class UpdateController {
    private final IUpdateProducer updateProducer;
    private final IHistoryManager historyManager;
    private final MessageSentService messageSentService;
    private GatewayTgBotComponent bot;

    public UpdateController(IUpdateProducer updateProducer, IHistoryManager historyManager,
                            @Qualifier(value = "gatewayMessageSentService")MessageSentService messageSentService) {
        this.updateProducer = updateProducer;
        this.historyManager = historyManager;
        this.messageSentService = messageSentService;
    }

    public void registerBot(GatewayTgBotComponent bot) {
        this.bot = bot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Полученное сообщение - null - такого вообще не должно было произойти");
            return;
        }

        if (update.hasMessage() || update.hasCallbackQuery() || update.hasPollAnswer()) {
            updateProducer.produce(GATEWAY_QUEUE, update);
        } else {
            var errorMessage = String.format("неподдерживаемый формат сообщения от пользователя %s",
                    telegramUserId(update));
            log.error(errorMessage);
        }
    }

    public void setView(BotApiMethodMessage messageToSend) {
        if (messageToSend instanceof SendMessage &&
                ((SendMessage) messageToSend).getText().isEmpty()) {
            historyManager.saveBotMessageToDelete(-1, messageToSend);
            // здесь удалять пользовательское сообщение
            return;
        }

        var message = bot.sendAnswerMessage(messageToSend);

        var telegramUserId = message.getChatId();
        var messageIdsToDelete = messageSentService.getMessageIdsByTelegramUserId(telegramUserId);

        if (!messageIdsToDelete.isEmpty() && messageIdsToDelete.get(0) == -1) {
            historyManager.deleteBotMessages(messageIdsToDelete, telegramUserId);
            messageSentService.deleteByTelegramUserId(telegramUserId);
        }

        historyManager.saveBotMessageToDelete(message.getMessageId(), messageToSend);
    }

    public void deleteBuggedMessage(DeleteMessage deleteMessage) {
        bot.deleteBuggedMessage(deleteMessage);
    }


    // TODO: null выдавать не должен!!! разобраться с приходом ответа из викторины
    private Long telegramUserId(Update update) {
        return update.hasMessage() ? update.getMessage().getFrom().getId()
                : update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId()
                : null;
    }
}
