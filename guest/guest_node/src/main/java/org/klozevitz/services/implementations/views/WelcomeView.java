package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.klozevitz.entityService.users.GuestService;
import org.klozevitz.util.GuestTelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@TelegramView(role = "guest", name = GuestView.WELCOME_VIEW)
@RequiredArgsConstructor
public class WelcomeView extends AbstractTelegramView {
    private final GuestService guestService;
    private final GuestTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        return
                isRegisterCommand(update) ?
                        answerAsList(registrationEmailRequestView(update)) :
                        null;
    }

    private boolean isRegisterCommand(Update update) {
        return
                update.hasCallbackQuery() &&
                        update.getCallbackQuery().getData().equals("/register");
    }

    private SendMessage registrationEmailRequestView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, GuestView.REGISTRATION_EMAIL_REQUEST_VIEW);

        return telegramView.registrationEmailRequestView(update);
    }
}
