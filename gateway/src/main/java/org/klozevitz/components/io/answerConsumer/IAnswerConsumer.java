package org.klozevitz.components.io.answerConsumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface IAnswerConsumer {
    void consumeAnswer(SendMessage answer);
}
