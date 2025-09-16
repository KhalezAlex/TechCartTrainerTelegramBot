package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.entityService.users.GuestService;
import org.klozevitz.util.GuestTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@TelegramView(role = "guest", name = GuestView.CONFIRMATION_EMAIL_SENT_VIEW)
@RequiredArgsConstructor
public class ConfirmationEmailSentView extends AbstractTelegramView {
    private final GuestService guestService;
    private final CompanyService companyService;
    private final GuestTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        var command = command(update);

        if ("/registration_abort".equals(command)) {
            return answerAsList(welcomeView(update));
        }
        return null;
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, GuestView.WELCOME_VIEW);
        companyService.deleteByTelegramUserId(telegramUserId);

        return telegramView.welcomeView(update);
    }
}
