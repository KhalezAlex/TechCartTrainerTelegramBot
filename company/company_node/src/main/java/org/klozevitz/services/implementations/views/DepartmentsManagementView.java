package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.CompanyTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "company", name = CompanyView.DEPARTMENTS_MANAGEMENT_VIEW)
@RequiredArgsConstructor
public class DepartmentsManagementView extends AbstractTelegramView {
    private final CompanyService companyService;
    private final CompanyTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {

        if(!isCorrectCommand(update)) {
            return null;
        }

        var command = command(update);

        if (command.equals("/add_department")) {
            return answerAsList(newDepartmentTelegramUserIdRequestView(update));
        } else {
            return answerAsList(welcomeView(update));
        }
    }

    private boolean isCorrectCommand(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String command = command(update);
        return command.equals("/add_department") ||
                command.equals("/welcome");
    }

    private SendMessage newDepartmentTelegramUserIdRequestView(Update update) {
        var telegramUserId = telegramUserId(update);
        companyService.setCurrentView(telegramUserId, CompanyView.NEW_DEPARTMENT_TELEGRAM_USER_ID_REQUEST_VIEW);

        return telegramView.departmentTelegramUserIdRequestView(update);
    }

    private SendMessage welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        companyService.setCurrentView(telegramUserId, CompanyView.WELCOME_VIEW);

        return telegramView.welcomeView(update);
    }

}
