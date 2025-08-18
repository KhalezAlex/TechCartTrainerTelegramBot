package org.klozevitz.services.implementations.updateProcessors.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.BasicUpdateProcessor;
import org.klozevitz.entityService.IGuestService;
import org.klozevitz.updateProcessor.filters.AbstractBasicUpdateProcessor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.annotationParameters.UpdateType.COMMAND;

@Log4j
@BasicUpdateProcessor(
        updateType = COMMAND,
        role = "guest"
)
@RequiredArgsConstructor
public class CommandGuestProcessor extends AbstractBasicUpdateProcessor {
    private final IGuestService guestService;

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var telegramUserId = telegramUserId(update);
        var guest = guestService.findByTelegramUserId(telegramUserId);

        assert guest != null;
        var viewProcessor = viewProcessor(guest.getCurrentView());

        return
                viewProcessor == null ?
                        null :
                        viewProcessor.processUpdate(update);
    }
}
