package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.CompanyTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "company", name = CompanyView.WELCOME_VIEW)
@RequiredArgsConstructor
public class WelcomeView extends AbstractTelegramView {
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    private final CompanyTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!isCorrectCommand(update)) {
            return null;
        }

        var command = command(update);

        if (command.equals("/departments_management")) {
            return answerAsList(departmentsManagementView(update));
        } else {
            return answerAsList(profileManagementView(update));
        }
    }

    private boolean isCorrectCommand(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String command = command(update);
        return command.equals("/departments_management") ||
                command.equals("/profile");
    }

    private SendMessage departmentsManagementView(Update update) {
        var telegramUserId = telegramUserId(update);
        companyService.setCurrentView(telegramUserId, CompanyView.DEPARTMENTS_MANAGEMENT_VIEW);
        var departments = departmentService.departmentsByCompanyTelegramUserId(telegramUserId);

        return telegramView.departmentsManagementView(update, departments);
    }

    private SendMessage profileManagementView(Update update) {
        var telegramUserId = telegramUserId(update);

        return null;
    }
}
