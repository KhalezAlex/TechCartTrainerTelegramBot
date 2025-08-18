package org.klozevitz.services.implementations.updateProcessors.command.byView;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.ViewUpdateProcessor;
import org.klozevitz.updateProcessor.filters.AbstractViewUpdateProcessor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.annotationParameters.UpdateType.COMMAND;
import static org.klozevitz.enities.users.enums.views.GuestView.NULLABLE_VIEW;

@Log4j
@ViewUpdateProcessor(
        view = NULLABLE_VIEW,
        updateType = COMMAND,
        role = "guest"
)
@RequiredArgsConstructor
public class NullableViewCommandGuestProcessor extends AbstractViewUpdateProcessor {

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var command = command(update);
        var viewResolver = viewResolver(command);

        return
                viewResolver == null ?
                        null :
                        viewResolver.processUpdate(update);
    }
}
