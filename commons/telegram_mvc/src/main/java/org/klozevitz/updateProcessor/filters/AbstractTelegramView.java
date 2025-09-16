package org.klozevitz.updateProcessor.filters;

import org.klozevitz.updateProcessor.AbstractUpdateProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.awt.*;

public abstract class AbstractTelegramView extends AbstractUpdateProcessor {

    public String getViewName() {
        return this.getClass().getSimpleName();
    }

    protected long chatId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }

    protected InlineKeyboardButton button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    protected String command(Update update) {
        return update.hasMessage() ?
                update.getMessage().getText() :
                update.getCallbackQuery().getData();
    }
}