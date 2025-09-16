package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.services.util.EmployeeRegistrar;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.DepartmentTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "department", name = DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW)
@RequiredArgsConstructor
public class EmployeeTelegramUserIdRequestView extends AbstractTelegramView {
    private final DepartmentService departmentService;
    private final EmployeeRegistrar employeeRegistrar;
    private final DepartmentTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return answerAsList(registerEmployee(update));
        } else if (update.hasCallbackQuery() && !update.getCallbackQuery().getData().isEmpty()) {
            var command = command(update);
            if ("/start".equals(command)) {
                return answerAsList(welcomeView(update));
            }
        }
        return null;
    }

    private SendMessage registerEmployee(Update update) {
        return employeeRegistrar.register(update);
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }
}
