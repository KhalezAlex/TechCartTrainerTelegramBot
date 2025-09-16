package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.CompanyTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "company", name = CompanyView.EMAIL_CONFIRMATION_NOTIFICATION_VIEW)
@RequiredArgsConstructor
public class EmailConfirmationNotificationView extends AbstractTelegramView {
    private final CompanyService companyService;
    private final CompanyTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        return
                isWelcomeCommand(update) ?
                        answerAsList(welcomeView(update)) :
                        null;
    }

    private boolean isWelcomeCommand(Update update) {
        return
                update.hasCallbackQuery() &&
                        update.getCallbackQuery().getData().equals("/welcome");
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        companyService.setCurrentView(telegramUserId, CompanyView.WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }
}
