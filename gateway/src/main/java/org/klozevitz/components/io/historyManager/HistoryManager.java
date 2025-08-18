package org.klozevitz.components.io.historyManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.components.MessageSentService;
import org.klozevitz.enities.users.MessageSent;
import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.util.List;
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
    public void deleteUserMessage(Update update) {
        var messageId = messageId(update);
        var telegramUserId = telegramUserId(update);
        var deleteMessage = deleteMessage(telegramUserId, messageId);

        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            var errorMessage = String
                    .format(
                            "Сообщение с id \"%d\" в чате \"%d\" не удалось удалить",
                            messageId,
                            telegramUserId
                    );
            log.error(errorMessage);
        }
    }

    @Override
    public void deleteBotMessages(List<MessageSent> messagesSent) {
        messagesSent.forEach(this::deleteBotMessage);
    }

    private void deleteBotMessage(MessageSent message) {
        var messageId = message.getMessageId();
        var sendMessage = message.getSendMessage();
        if (messageId != null && sendMessage != null) {
            var deleteMessage = deleteMessage(
                    Long.parseLong(sendMessage.getChatId()),
                    messageId);
            try {
                bot.execute(deleteMessage);
            } catch (TelegramApiException ignored) {
                log.error("Уже удалено!!!");
            }
        }
    }

    @Override
    @Transactional
    public void saveMessageToDelete(Integer messageId, SendMessage sendMessage) {
        if (sendMessage.getText().isEmpty()) {
            messageSentService.saveTerminate(Long.parseLong(sendMessage.getChatId()));
        } else {
            messageSentService.save(messageId, sendMessage, Long.parseLong(sendMessage.getChatId()));
        }
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

    private DeleteMessage deleteMessage(long telegramUserId, int messageId) {
        return DeleteMessage.builder()
                .chatId(telegramUserId)
                .messageId(messageId)
                .build();
    }
}
