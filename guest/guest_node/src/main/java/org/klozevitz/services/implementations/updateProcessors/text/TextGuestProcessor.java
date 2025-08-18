package org.klozevitz.services.implementations.updateProcessors.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.klozevitz.entityService.IGuestService;
import org.klozevitz.updateProcessor.filters.AbstractBasicUpdateProcessor;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.annotationParameters.UpdateType.TEXT;

@Log4j
@BasicUpdateProcessor(
        updateType = TEXT,
        role = "guest"
)
@RequiredArgsConstructor
public class TextGuestProcessor extends AbstractBasicUpdateProcessor {
    private final IGuestService guestService;

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var telegramUserId = telegramUserId(update);
        var guest = guestService.findByTelegramUserId(telegramUserId);

        assert guest != null;
        var viewProcessor = viewDispatcher.get(guest.getCurrentView());

        return
                viewProcessor == null ?
                        null :
                        viewProcessor.processUpdate(update);
    }
}
