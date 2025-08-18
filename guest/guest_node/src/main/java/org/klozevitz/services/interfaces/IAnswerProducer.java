package org.klozevitz.services.interfaces;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface IAnswerProducer {
    void produceAnswer(SendMessage sendMessage);
}
