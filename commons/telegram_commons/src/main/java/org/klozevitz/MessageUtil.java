package org.klozevitz;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageUtil {
    public SendMessage addServiceMessage(SendMessage answer, String error) {
        var message = String.format("%s\n\n%s", error, answer.getText());
        answer.enableHtml(true);
        answer.setText(message);
        return answer;
    }

    public SendMessage blankAnswer(Update update) {
        var chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
        return blankAnswer(chatId);
    }

    public SendMessage blankAnswer(long chatId) {
        var answer = new SendMessage();
        answer.setChatId(chatId);
        return answer;
    }
}
