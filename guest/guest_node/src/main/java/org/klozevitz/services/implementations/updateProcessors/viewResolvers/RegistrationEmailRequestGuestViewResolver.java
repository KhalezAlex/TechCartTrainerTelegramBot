package org.klozevitz.services.implementations.updateProcessors.viewResolvers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.ViewResolver;
import org.klozevitz.updateProcessor.filters.AbstractViewResolver;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.enities.users.enums.views.GuestView.REGISTRATION_EMAIL_REQUEST_VIEW;

@Log4j
@ViewResolver(
        views = REGISTRATION_EMAIL_REQUEST_VIEW,
        role = "guest",
        command = "/registrationEmailReceived"
)
@RequiredArgsConstructor
public class RegistrationEmailRequestGuestViewResolver extends AbstractViewResolver {

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {

        return telegramView.processUpdate(update);
    }
}
