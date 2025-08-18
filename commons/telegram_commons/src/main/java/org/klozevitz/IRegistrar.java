package org.klozevitz;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface IRegistrar {
    SendMessage register(Update update);
}
