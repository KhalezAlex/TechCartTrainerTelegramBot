package org.klozevitz.services.implementations.updateProcessors.text.byView;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.ViewUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractViewUpdateProcessor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.annotationParameters.UpdateType.TEXT;
import static org.klozevitz.enities.users.enums.views.GuestView.REGISTRATION_EMAIL_REQUEST_VIEW;

@Log4j
@ViewUpdateProcessor(
        view = REGISTRATION_EMAIL_REQUEST_VIEW,
        updateType = TEXT,
        role = "guest"
)
@RequiredArgsConstructor
public class GuestRegistrationEmailRequestViewProcessor extends AbstractViewUpdateProcessor {
    public final String COMMAND = "/registrationEmailReceived";

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var viewResolver = viewResolver(COMMAND);

        return
                viewResolver == null ?
                        null :
                        viewResolver.processUpdate(update);
    }
}
