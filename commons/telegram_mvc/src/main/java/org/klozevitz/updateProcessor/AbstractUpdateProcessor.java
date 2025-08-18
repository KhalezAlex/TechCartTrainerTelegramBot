package org.klozevitz.updateProcessor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractUpdateProcessor implements UpdateProcessor {
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

    protected String command(Update update) {
        return update.hasMessage() ?
                update.getMessage().getText() :
                update.getCallbackQuery().getData();
    }

    protected ArrayList<SendMessage> answerAsList(SendMessage answer) {
        return new ArrayList<>() {{
            add(answer);
            add(SendMessage.builder()
                    .text("")
                    .chatId(answer.getChatId())
                    .build());
        }};
    }
}
