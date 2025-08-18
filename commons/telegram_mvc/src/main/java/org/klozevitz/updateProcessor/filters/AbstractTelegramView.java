package org.klozevitz.updateProcessor.filters;

import org.klozevitz.updateProcessor.AbstractUpdateProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractTelegramView extends AbstractUpdateProcessor {
    protected long chatId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }
}
