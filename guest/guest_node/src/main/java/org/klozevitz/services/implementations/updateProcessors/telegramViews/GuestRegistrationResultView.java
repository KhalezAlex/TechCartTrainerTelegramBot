package org.klozevitz.services.implementations.updateProcessors.telegramViews;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(
        role = "guest",
        command = "/registrationEmailReceived"
)
@RequiredArgsConstructor
public class GuestRegistrationResultView extends AbstractTelegramView {

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var email = command(update);
        log.debug("ЩАС БУДЕМ РЕГИТЬСЯ!!!!!!!! " + email);
        return null;
    }
}
