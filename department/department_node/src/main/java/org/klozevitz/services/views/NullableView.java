package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.DepartmentTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "department", name = DepartmentView.NULLABLE_VIEW)
@RequiredArgsConstructor
public class NullableView extends AbstractTelegramView {
    private final DepartmentService departmentService;
    private final DepartmentTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        var command = command(update);
        if (command.equals("/start")) {
            return answerAsList(welcomeView(update));
        }

        return null;
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }
}
