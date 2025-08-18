package org.klozevitz.services.implementations.updateProcessors.viewResolvers;

import lombok.RequiredArgsConstructor;
import org.klozevitz.annotations.ViewResolver;
import org.klozevitz.entityService.IGuestService;
import org.klozevitz.updateProcessor.filters.AbstractViewResolver;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.enities.users.enums.views.GuestView.REGISTRATION_EMAIL_REQUEST_VIEW;
import static org.klozevitz.enities.users.enums.views.GuestView.WELCOME_VIEW;

@ViewResolver(
        views = WELCOME_VIEW,
        role = "guest",
        command = "/register"
)
@RequiredArgsConstructor
public class RegistrationGuestViewResolver extends AbstractViewResolver {
    private final IGuestService guestService;

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var telegramUserId = telegramUserId(update);
        guestService.setCurrentView(telegramUserId, REGISTRATION_EMAIL_REQUEST_VIEW);

        return telegramView.processUpdate(update);
    }
}
