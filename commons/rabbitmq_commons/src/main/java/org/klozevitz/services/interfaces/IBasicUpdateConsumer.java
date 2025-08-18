package org.klozevitz.services.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IBasicUpdateConsumer {

    void consumeTextUpdate(Update update);

    void consumeCommandUpdate(Update update);

    void consumeCallbackUpdate(Update update);
}
