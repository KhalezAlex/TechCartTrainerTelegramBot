package org.klozevitz.components.io.answerConsumer;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

public interface IAnswerConsumer {
    void consumeAnswer(BotApiMethodMessage answer);
}
