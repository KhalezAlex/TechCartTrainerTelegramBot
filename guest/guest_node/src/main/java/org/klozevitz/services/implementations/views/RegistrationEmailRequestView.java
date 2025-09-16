package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.klozevitz.entityService.users.GuestService;
import org.klozevitz.services.implementations.util.CompanyRegistrar;
import org.klozevitz.util.GuestTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "guest", name = GuestView.REGISTRATION_EMAIL_REQUEST_VIEW)
@RequiredArgsConstructor
public class RegistrationEmailRequestView extends AbstractTelegramView {
    private final GuestService guestService;
    private final GuestTelegramView telegramView;
    private final CompanyRegistrar registrar;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        var confirmationEmailSent = registrar.register(update);

        switch (confirmationEmailSent) {
            case "invalid":
                return answerAsList(invalidEmailNotificationView(update));
            case "already registered":
                return answerAsList(alreadyRegisteredEmailNotificationView(update));
            case "sent":
                return answerAsList(confirmationEmailSentNotificationView(update));
            default:
                return null;
        }
    }

    private SendMessage confirmationEmailSentNotificationView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, GuestView.CONFIRMATION_EMAIL_SENT_VIEW);

        return telegramView.emailConfirmationRequestView(update);
    }

    private SendMessage alreadyRegisteredEmailNotificationView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, GuestView.REGISTRATION_EMAIL_REQUEST_VIEW);

        return telegramView.alreadyRegisteredEmailNotificationView(update);
    }

    private SendMessage invalidEmailNotificationView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, GuestView.INVALID_EMAIL_NOTIFICATION_VIEW);

        return telegramView.invalidEmailNotificationView(update);
    }
}
