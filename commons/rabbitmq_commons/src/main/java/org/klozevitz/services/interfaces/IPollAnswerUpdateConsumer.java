package org.klozevitz.services.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IPollAnswerUpdateConsumer {

    void consumePollAnswerUpdate(Update update);
}
