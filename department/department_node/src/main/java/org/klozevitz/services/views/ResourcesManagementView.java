package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.DepartmentTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "department", name = DepartmentView.RESOURCES_MANAGEMENT_VIEW)
@RequiredArgsConstructor
public class ResourcesManagementView extends AbstractTelegramView {
    private final DepartmentService departmentService;
    private final DepartmentTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        var command = update.getCallbackQuery().getData();
        if (command.equals("/add_resource")) {
            return resourceRequestView(update);
        }
        if (command.equals("/start")) {
            return welcomeView(update);
        }

        return null;
    }

    private ArrayList<BotApiMethod<?>> welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);
        var answer = telegramView.welcomeView(update);

        return answerAsList(answer);
    }

    private ArrayList<BotApiMethod<?>> resourceRequestView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.RESOURCE_REQUEST_VIEW);
        var answer = telegramView.resourceRequestView(update);

        return answerAsList(answer);
    }
}
