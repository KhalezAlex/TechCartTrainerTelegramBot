package org.klozevitz;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IRegistrar {
    String register(Update update);
}
