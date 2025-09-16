package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.EmployeeView;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.EmployeeTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "employee", name = EmployeeView.TEST_RESULT_VIEW)
@RequiredArgsConstructor
public class TestResultView extends AbstractTelegramView {
    private final EmployeeService employeeService;
    private final EmployeeTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        var telegramUserId = telegramUserId(update);
        employeeService.setCurrentView(telegramUserId, EmployeeView.WELCOME_VIEW);
        var answer = telegramView.welcomeView(update);

        return answerAsList(answer);
    }
}
