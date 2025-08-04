package org.klozevitz.services;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IGatewayUpdateDispatcher {
    void dispatchByRole(Update update);
}
