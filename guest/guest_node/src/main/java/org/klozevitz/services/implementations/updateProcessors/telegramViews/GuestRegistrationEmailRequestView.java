package org.klozevitz.services.implementations.updateProcessors.telegramViews;

import org.klozevitz.annotations.TelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

/**
 * Вью запрашивает мейл у пользователя
 * */

@TelegramView(
        role = "guest",
        command = "/register"
)
public class GuestRegistrationEmailRequestView extends AbstractTelegramView {
    private final String MESSAGE = "Введите адрес электронной почты для завершения регистрации";

    @Override
    public ArrayList<SendMessage> processUpdate(Update update) {
        var chatId = telegramUserId(update);

        return answerAsList(textMessage(MESSAGE, chatId));
    }

    public SendMessage textMessage(String message, Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(message);
        return answer;
    }
}
