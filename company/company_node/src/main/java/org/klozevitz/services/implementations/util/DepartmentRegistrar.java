package org.klozevitz.services.implementations.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.entityService.users.AppUserService;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.util.CompanyTelegramView;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Pattern;

@Log4j
@Service
@RequiredArgsConstructor
public class DepartmentRegistrar {
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    private final CompanyTelegramView telegramView;

    public SendMessage register(Update update) {
        var telegramUserId = update.getMessage().getFrom().getId();

        if (!isTgIdValid(String.valueOf(telegramUserId))) {
            return invalidDepartmentTgIdErrorView(update);
        }

        // сначала проверяем на то, есть ли вообще в сети пользователь с таким tgId
        var departmentTelegramUserId = Long.parseLong(update.getMessage().getText());
        // если есть, возвращаем на прошлый вью с ошибкой
        if (appUserService.findByTelegramUserId(departmentTelegramUserId).isPresent()) {
            return alreadyRegisteredTelegramUserIdErrorView(update);
        }
        // иначе регистрируется департамент:
        var registered = registerDepartment(update);

        return registered ?
                departmentRegistrationNotificationView(update) :
                invalidDepartmentTgIdErrorView(update);
    }

    private SendMessage departmentRegistrationNotificationView(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        companyService.setCurrentView(telegramUserId, CompanyView.DEPARTMENTS_MANAGEMENT_VIEW);
        var departments = departmentService.departmentsByCompanyTelegramUserId(telegramUserId);

        return telegramView.newDepartmentRegistrationNotificationView(update, departments);
    }

    private SendMessage invalidDepartmentTgIdErrorView(Update update) {
        var telegramUserId = update.getMessage().getFrom().getId();
        companyService.setCurrentView(telegramUserId, CompanyView.NEW_DEPARTMENT_TELEGRAM_USER_ID_REQUEST_VIEW);

        return telegramView.invalidDepartmentTelegramUserIdErrorView(update);
    }

    private SendMessage alreadyRegisteredTelegramUserIdErrorView(Update update) {
        long telegramUserId = update.getMessage().getFrom().getId();
        companyService.setCurrentView(telegramUserId, CompanyView.DEPARTMENTS_MANAGEMENT_VIEW);
        var departments = departmentService.departmentsByCompanyTelegramUserId(telegramUserId);

        return telegramView.alreadyRegisteredTelegramUserIdErrorView(update, departments);
    }

    private boolean registerDepartment(Update update) {
        var companyTelegramUserId = update.getMessage().getFrom().getId();
        var departmentTelegramUserId = Long.parseLong(update.getMessage().getText());

        try {
            departmentService.save(companyTelegramUserId, departmentTelegramUserId);
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
