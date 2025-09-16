package org.klozevitz.services.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.IAnswerProducer;
import org.klozevitz.annotations.ViewDispatcher;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.util.DepartmentTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractViewDispatcher;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@ViewDispatcher(role = "department")
@RequiredArgsConstructor
public class MainService extends AbstractViewDispatcher {
    private final IAnswerProducer answerProducer;
    private final DepartmentService departmentService;
    private final DepartmentTelegramView telegramView;

    public void processUpdate(Update update) {
        ArrayList<BotApiMethod<?>> answer = null;
        var telegramUserId = telegramUserId(update);
        var currentDepartment = departmentService.findByTelegramUserId(telegramUserId);
        var viewName = currentDepartment.getCurrentView();
        var view = getView(viewName);

        try {
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                var message = update.getMessage().getText();
                if (message.equals("/start")) {
                    departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);
                    answer = answerAsList(update, telegramUserId);
                }
            } else {
                answer = view.processUpdate(update);
            }

            if (answer != null) {
                sendAnswer(answer);
            } else {
                produceBuggedMessage(update, telegramUserId);
            }
        } catch (Exception e) {
            log.debug("*******************");
            log.error(e);
            log.debug("*******************");
            produceBuggedMessage(update, telegramUserId);
        }
    }

    private void produceBuggedMessage(Update update, long telegramUserId) {
        var messageId = messageId(update);
        assert messageId != -1;
        var deleteMessage = deleteMessage(telegramUserId, messageId);
        answerProducer.produceBuggedMessage(deleteMessage);
    }

    private ArrayList<BotApiMethod<?>> answerAsList(Update update, long telegramUserId) {
        return new ArrayList<>() {{
            add(telegramView.welcomeView(update));
            add(SendMessage.builder()
                    .text("")
                    .chatId(telegramUserId)
                    .build());
        }};
    }

    private DeleteMessage deleteMessage(long telegramUserId, Integer messageId) {
        return DeleteMessage.builder()
                .messageId(messageId)
                .chatId(telegramUserId)
                .build();
    }

    private void sendAnswer(ArrayList<BotApiMethod<?>> answer) {
        for (BotApiMethod<?> currentAnswer : answer) {
            answerProducer.produceAnswer(currentAnswer);
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
