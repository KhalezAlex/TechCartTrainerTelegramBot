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
@TelegramView(role = "department", name = DepartmentView.EMPLOYEES_MANAGEMENT_VIEW)
@RequiredArgsConstructor
public class EmployeesManagementView extends AbstractTelegramView {
    private final DepartmentService departmentService;
    private final DepartmentTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        var telegramUserId = telegramUserId(update);
        var command = command(update);

        if ("/employee_tg_id_request_view".equals(command)) {
            return answerAsList(employeeTelegramUserIdRequestView(update));
        }
        if ("/start".equals(command)) {
            return answerAsList(welcomeView(update));
        }

        return null;
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }

    private SendMessage employeeTelegramUserIdRequestView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW);

        return telegramView.employeeTelegramUserIdRequestView(update);
    }
}
