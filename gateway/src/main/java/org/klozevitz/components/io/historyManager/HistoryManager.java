package org.klozevitz.components.io.historyManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.components.MessageSentService;
import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Log4j
@RequiredArgsConstructor
public class HistoryManager implements IHistoryManager {
    private final MessageSentService messageSentService;
    private GatewayTgBotComponent bot;

    @Override
    public void registerBot(GatewayTgBotComponent bot) {
        this.bot = bot;
    }

    @Override
    public void deleteBotMessages(ArrayList<Integer> messageIdsToDelete, long telegramUserId) {
        deleteMessage(telegramUserId, messageIdsToDelete.get(1) - 1);
        messageIdsToDelete.forEach(id -> deleteMessage(telegramUserId, id));
    }

    @Override
    public void deleteMessage(long telegramUserId, int messageId) {
        var deleteMessage = DeleteMessage.builder()
                .chatId(telegramUserId)
                .messageId(messageId)
                .build();
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException ignored) {
            log.error("Уже удалено!!!");
        }

    }

    @Override
    @Transactional
    public void saveBotMessageToDelete(Integer messageId, BotApiMethodMessage messageToDelete) {
        var chatId = chatId(messageToDelete);
        if (messageToDelete instanceof SendMessage &&
                ((SendMessage)messageToDelete).getText().isEmpty()) {
            messageSentService.saveTerminate(chatId);
        } else {
            messageSentService.saveBotMessage(messageId, messageToDelete, chatId);
        }
    }

    @Override
    public void saveUserMessageToDelete(Update update) {
        var messageId = messageId(update);
        var telegramUserId = telegramUserId(update);

        messageSentService.saveUserMessage(messageId, telegramUserId);
    }

    private long chatId(BotApiMethodMessage message) {
        if (message instanceof SendMessage) {
            return Long.parseLong(((SendMessage) message).getChatId());
        }
        if (message instanceof SendPoll) {
            return Long.parseLong(((SendPoll) message).getChatId());
        }

        return -1;
    }

    private Integer messageId(Update update) {
        return update.hasMessage()
                ? update.getMessage().getMessageId()
                : update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getMessageId()
                : -1;
    }

    private long telegramUserId(Update update) {
        return Optional.ofNullable(update)
                .map(u ->
                        u.hasMessage() ? u.getMessage().getFrom().getId() :
                                u.hasCallbackQuery() ? u.getCallbackQuery().getFrom().getId() :
                                        u.hasPollAnswer() ? u.getPollAnswer().getUser().getId() :
                                                null
                )
                .orElseThrow(() -> new IllegalArgumentException("Update пустой"));
    }
}
