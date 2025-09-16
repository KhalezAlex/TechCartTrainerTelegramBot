package org.klozevitz;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface IAnswerProducer {
    void produceAnswer(BotApiMethod<?> sendMessage);
    void produceBuggedMessage(DeleteMessage deleteMessage);
}
