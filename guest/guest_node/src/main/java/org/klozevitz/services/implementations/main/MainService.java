package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.ViewDispatcher;
import org.klozevitz.entityService.users.GuestService;
import org.klozevitz.IAnswerProducer;
import org.klozevitz.updateProcessor.filters.AbstractViewDispatcher;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@ViewDispatcher(role = "guest")
@RequiredArgsConstructor
public class MainService extends AbstractViewDispatcher {
    private final IAnswerProducer answerProducer;
    private final GuestService guestService;

    public void processUpdate(Update update) {
        var telegramUserId = telegramUserId(update);
        var currentGuest = guestService.findByTelegramUserId(telegramUserId);
        var viewName = currentGuest.getCurrentView();
        var view = getView(viewName);
//        var messageId = messageId(update);
//        messageSentService.saveUserMessage(messageId, telegramUserId);

        try {
            var answer = view.processUpdate(update);
            if (answer != null) {
                sendAnswer(answer);
            } else {
                var messageId = messageId(update);
                assert messageId != -1;
                var deleteMessage = deleteMessage(telegramUserId, messageId);
                answerProducer.produceBuggedMessage(deleteMessage);
            }
        } catch (Exception e) {
            log.debug("*******************");
            log.error(e);
            log.debug("*******************");
        }
    }

    private DeleteMessage deleteMessage(long telegramUserId, Integer messageId) {
        return DeleteMessage.builder()
                .messageId(messageId)
                .chatId(telegramUserId)
                .build();
    }

    private void sendAnswer(ArrayList<BotApiMethod<?>> answer) {
        for (BotApiMethod<?> botApiMethod : answer) {
            answerProducer.produceAnswer(botApiMethod);
        }
    }

    private Integer messageId(Update update) {
        return update.hasMessage()
                ? update.getMessage().getMessageId()
                : update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getMessageId()
                : -1;
    }
}
