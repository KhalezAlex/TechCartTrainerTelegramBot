package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.EmployeeView;
import org.klozevitz.entityService.menu.CategoryService;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.EmployeeTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "employee", name = EmployeeView.CATEGORY_INFO_VIEW)
@RequiredArgsConstructor
public class CategoryInfoView extends AbstractTelegramView {
    private final EmployeeService employeeService;
    private final CategoryService categoryService;
    private final EmployeeTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        var command = command(update);

        if (command.equals("/category_info_choice_view")) {
            return categoryInfoChoiceView(update);
        }

        if (command.equals("/start")) {
            return welcomeView(update);
        }

        return null;
    }

    private ArrayList<BotApiMethod<?>> welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        var answer = telegramView.welcomeView(update);

        employeeService.setCurrentView(telegramUserId, EmployeeView.WELCOME_VIEW);

        return answerAsList(answer);
    }

    private ArrayList<BotApiMethod<?>> categoryInfoChoiceView(Update update) {
        var telegramUserId = telegramUserId(update);
        var resourceInfo = categoryService
                .getResourceNamesMappedByIdsByEmployeeTelegramUserId(telegramUserId);
        var answer = telegramView.categoryInfoChoiceView(telegramUserId, resourceInfo);

        employeeService.setCurrentView(telegramUserId, EmployeeView.CATEGORY_INFO_CHOICE_VIEW);

        return answerAsList(answer);
    }
}
