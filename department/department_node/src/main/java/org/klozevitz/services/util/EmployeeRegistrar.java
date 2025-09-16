package org.klozevitz.services.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.Employee;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.util.DepartmentTelegramView;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Pattern;

@Log4j
@Service
@RequiredArgsConstructor
public class EmployeeRegistrar {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final DepartmentTelegramView telegramView;

    public SendMessage register(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        var employeeTelegramUserId = update.getMessage().getText();
        var isTgIdValid = isTgIdValid(employeeTelegramUserId);
        SendMessage registrationResult;

        if (isTgIdValid) {
            var success = registerEmployee(telegramUserId, Long.parseLong(employeeTelegramUserId));
            registrationResult = success ?
                    employeeRegistrationNotificationView(update) :
                    alreadyRegisteredTelegramUserIdErrorView(update);
        } else {
            registrationResult = invalidEmployeeTelegramUserIdNotificationView(update);
        }

        return registrationResult;
    }

    private SendMessage invalidEmployeeTelegramUserIdNotificationView(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        departmentService.setCurrentView(telegramUserId, DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW);

        return telegramView.invalidEmployeeTelegramUserIdNotificationView(update);
    }

    private SendMessage alreadyRegisteredTelegramUserIdErrorView(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        departmentService.setCurrentView(telegramUserId, DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW);

        return telegramView.alreadyRegisteredEmployeeTelegramUserIdNotificationView(update);
    }

    private SendMessage employeeRegistrationNotificationView(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        departmentService.setCurrentView(telegramUserId, DepartmentView.EMPLOYEES_MANAGEMENT_VIEW);
        List<Employee> employees = employeeService.employeesByDepartmentTelegramUserId(telegramUserId);

        return telegramView.employeeRegistrationNotificationView(update, employees);
    }



    private boolean registerEmployee(long departmentTelegramUserId, long employeeTelegramUserId) {
        try {
            employeeService.save(departmentTelegramUserId, employeeTelegramUserId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTgIdValid(String tgId) {
        var regexp = "\\b\\d{8,13}\\b";
        var pattern = Pattern.compile(regexp);
        var matcher = pattern.matcher(tgId);

        return matcher.matches();
    }
}