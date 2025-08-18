package org.klozevitz.services.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IMainService {

    void processTextUpdate(Update update);

    void processCommandUpdate(Update update);

    void processCallbackUpdate(Update update);
}
