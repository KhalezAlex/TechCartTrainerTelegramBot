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

import static org.klozevitz.enities.users.enums.views.GuestView.WELCOME_VIEW;

@TelegramView(role = "guest", name = GuestView.NULLABLE_VIEW)
@RequiredArgsConstructor
public class NullableView extends AbstractTelegramView {
    private final GuestService guestService;
    private final GuestTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        return isStartCommand(update) ?
                answerAsList(welcomeView(update)) :
                null;
    }

    private boolean isStartCommand(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equals("/start");
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }
}
