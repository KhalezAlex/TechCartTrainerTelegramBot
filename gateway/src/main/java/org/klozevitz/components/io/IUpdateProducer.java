package org.klozevitz.components.io;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IUpdateProducer {
    void produce(String queue, Update update);
}
