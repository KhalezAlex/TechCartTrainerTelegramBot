package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.menu.util.CurrentTest;
import org.klozevitz.enities.users.enums.views.EmployeeView;
import org.klozevitz.entityService.menu.CategoryService;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.EmployeeTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Log4j
@TelegramView(role = "employee", name = EmployeeView.CATEGORY_TEST_CHOICE_VIEW)
@RequiredArgsConstructor
public class CategoryTestChoiceView extends AbstractTelegramView {
    private final EmployeeService employeeService;
    private final CategoryService categoryService;
    private final EmployeeTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        var command = command(update);
        if (command.equals("/start")) {
            return welcomeView(update);
        }

        if (command.startsWith("/category_test_")) {
            return categoryTestView(update);
        }

        return null;
    }


    private ArrayList<BotApiMethod<?>> welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        var answer = telegramView.welcomeView(update);

        employeeService.setCurrentView(telegramUserId, EmployeeView.WELCOME_VIEW);

        return answerAsList(answer);
    }


    private ArrayList<BotApiMethod<?>> categoryTestView(Update update) {
        var data = command(update);
        var categoryId = categoryIdFromUpdate(data);
        var category = categoryService.findById(categoryId);
        var currentTest = new CurrentTest(category);

        var telegramUserId = telegramUserId(update);
        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);

        currentEmployee.setCurrentTest(currentTest);
        currentEmployee.setCurrentView(EmployeeView.TEST_VIEW);
        employeeService.merge(currentEmployee);

        var answer = telegramView.itemTestMessage(update, currentTest, true);

        return answerAsList(answer);
    }


    private long categoryIdFromUpdate(String data) {
        var splitCommand = List.of(data.split("_"));
        var categoryIdInStringFromUpdate = splitCommand.get(splitCommand.size() - 1);

        try {
            return Long.parseLong(categoryIdInStringFromUpdate);
        } catch (Exception e) {
            return -1L;
        }
    }
}
