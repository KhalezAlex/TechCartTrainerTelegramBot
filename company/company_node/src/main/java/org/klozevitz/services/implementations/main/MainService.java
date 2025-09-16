package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.IAnswerProducer;
import org.klozevitz.annotations.ViewDispatcher;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.updateProcessor.filters.AbstractViewDispatcher;
import org.klozevitz.util.CompanyTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@ViewDispatcher(role = "company")
@RequiredArgsConstructor
public class MainService extends AbstractViewDispatcher {
    private final IAnswerProducer answerProducer;
    private final CompanyService companyService;
    private final CompanyTelegramView telegramView;

    public void processUpdate(Update update) {
        ArrayList<BotApiMethod<?>> answer = null;
        var telegramUserId = telegramUserId(update);
        var optionalCurrentCompany = companyService.findByTelegramUserId(telegramUserId);
        if (optionalCurrentCompany.isEmpty()) {
            return;
        }
        var currentCompany = optionalCurrentCompany.get();
        var viewName = currentCompany.getCurrentView();
        var view = getView(viewName);

        try {
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                var message = update.getMessage().getText();
                if (message.equals("/start")) {
                    companyService.setCurrentView(telegramUserId, CompanyView.WELCOME_VIEW);
                    answer = answerAsList(update, telegramUserId);
                }
            } else {
                answer = view.processUpdate(update);
            }

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
