package org.klozevitz.updateProcessor.filters;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractViewDispatcher {
    private final Map<String, AbstractTelegramView> dispatcher;

    public AbstractViewDispatcher() {
        this.dispatcher = new HashMap<>();
    }

    protected AbstractTelegramView getView(String view) {
        return dispatcher.get(view);
    }

    protected long telegramUserId(Update update) {
        return Optional.ofNullable(update)
                .map(u ->
                        u.hasMessage() ? u.getMessage().getFrom().getId() :
                                u.hasCallbackQuery() ? u.getCallbackQuery().getFrom().getId() :
                                        u.hasPollAnswer() ? u.getPollAnswer().getUser().getId() :
                                                null
                )
                .orElseThrow(() -> new IllegalArgumentException("Update пустой"));
    }

    public void registerView(String name, AbstractTelegramView view) {
        dispatcher.put(name, view);
    }
}
