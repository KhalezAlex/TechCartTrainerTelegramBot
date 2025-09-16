package org.klozevitz.components.io.answerConsumer;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface IDeleteMessageConsumer {
    void consumeDeleteMessage(DeleteMessage deleteMessage);
}
