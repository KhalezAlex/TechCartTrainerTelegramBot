package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.menu.Category;
import org.klozevitz.enities.users.Employee;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.menu.CategoryService;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.DepartmentTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Log4j
@TelegramView(role = "department", name = DepartmentView.WELCOME_VIEW)
@RequiredArgsConstructor
public class WelcomeView extends AbstractTelegramView {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final CategoryService categoryService;
    private final DepartmentTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!isCorrectCommand(update)) {
            return null;
        }

        var command = command(update);

        if ("/employee_management".equals(command)) {
            return employeeManagementView(update);
        }
        if ("/resources_management".equals(command)) {
            return resourcesManagementView(update);
        }
        if ("/profile_management".equals(command)) {
            return profileManagementView(update);
        }

        return null;
    }


    private ArrayList<BotApiMethod<?>> employeeManagementView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.EMPLOYEES_MANAGEMENT_VIEW);
        List<Employee> employees = employeeService.employeesByDepartmentTelegramUserId(telegramUserId);
        var answer = telegramView.employeesManagementView(update, employees);

        return answerAsList(answer);
    }

    private ArrayList<BotApiMethod<?>> resourcesManagementView(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.RESOURCES_MANAGEMENT_VIEW);
        List<Category> categories = categoryService.findByDepartmentTelegramUserId(telegramUserId);
        var answer = telegramView.resourcesManagementView(update, categories);

        return answerAsList(answer);
    }

    private boolean isCorrectCommand(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        var command = command(update);
        return command.equals("/employee_management") ||
                command.equals("/resources_management") ||
                command.equals("/profile_management");
    }

    private ArrayList<BotApiMethod<?>> profileManagementView(Update update) {

        return null;
    }
}
