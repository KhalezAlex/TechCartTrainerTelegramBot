package org.klozevitz.services.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IUpdateConsumer {

    void consumeUpdate(Update update);
}
