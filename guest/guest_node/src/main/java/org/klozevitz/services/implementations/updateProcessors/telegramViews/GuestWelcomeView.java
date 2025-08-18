package org.klozevitz.services.implementations.updateProcessors.telegramViews;

import org.klozevitz.annotations.TelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


/**
 * Первое приветственное сообщение для незарегистрированных пользователей
 * */

@TelegramView(
    role = "guest",
    command = "/start"
)
public class GuestWelcomeView extends AbstractTelegramView {
    private final String MESSAGE = "Вы находитесь на главной странице чат-бота.\n" +
            "Ваша компания еще не зарегистрирована.\n" +
            "Нажмите кнопку для продолжения регистрации.";

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var chatId = chatId(update);
        var guestWelcomeView = new SendMessage();
        var unregisteredInlineKeyboardMarkup = unregisteredInlineKeyboardMarkup();

        guestWelcomeView.setText(MESSAGE);
        guestWelcomeView.setReplyMarkup(unregisteredInlineKeyboardMarkup);
        guestWelcomeView.setChatId(chatId);

        return answerAsList(guestWelcomeView);
    }

    private InlineKeyboardMarkup unregisteredInlineKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var row = List.of(
                button("РЕГИСТРАЦИЯ", "/register"),
                button("ОТМЕНА", "/cancel") //TODO: не обработано!!!
        );
        var keyboardRows = List.of(row);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
