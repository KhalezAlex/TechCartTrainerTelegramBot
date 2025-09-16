package org.klozevitz.util;

import lombok.RequiredArgsConstructor;
import org.klozevitz.MessageUtil;
import org.klozevitz.enities.users.Department;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyTelegramView {
    private final MessageUtil messageUtil;

    /**
     * Первое приветственное сообщение для незарегистрированных пользователей
     * CompanyView.REGISTERED_WELCOME_VIEW
     * */
    public SendMessage welcomeView(Update update) {
        var message = "Вы находитесь на главной странице чат-бота.";
        var answer = messageUtil.blankAnswer(update);
        var registeredWelcomeViewKeyboardMarkup =
                registeredWelcomeViewKeyboardMarkup();

        answer.setText(message);
        answer.setReplyMarkup(registeredWelcomeViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup registeredWelcomeViewKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var row = List.of(
                button("УПРАВЛЕНИЕ\nЗАВЕДЕНИЯМИ", "/departments_management"),
                button("УПРАВЛЕНИЕ\nПРОФИЛЕМ", "/profile_management")
        );
        var keyboardRows = List.of(row);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }


    /**
     * Вью управления департаментами
     * CompanyView.DEPARTMENTS_MANAGEMENT_VIEW
     * */
    public SendMessage departmentsManagementView(Update update, List<Department> departments) {
        var message = "На этой странице Вы можете управлять " +
                "Вашими заведениями. Как только Вы добавите первое заведение, оно появится в списке.";
        var answer = messageUtil.blankAnswer(update);
        var departmentsManagementViewKeyboardMarkUp =
                departmentsManagementViewKeyboardMarkUp(departments);

        answer.setText(message);
        answer.setReplyMarkup(departmentsManagementViewKeyboardMarkUp);

        return answer;
    }

    private InlineKeyboardMarkup departmentsManagementViewKeyboardMarkUp(List<Department> departments) {
        var departmentsManagementViewKeyboardMarkUp = new InlineKeyboardMarkup();
        var addDepartmentRow = List.of(
                button("ДОБАВИТЬ ЗАВЕДЕНИЕ", "/add_department"),
                button("Выход", "/welcome")
        );

        List<List<InlineKeyboardButton>> departmentManagementViewKeyboardMarkupDepartmentsManagementTable;

        if (!departments.isEmpty()) {
            departmentManagementViewKeyboardMarkupDepartmentsManagementTable =
                    departmentsManagementViewKeyboardMarkUpDepartmentsManagementTable(departments);
            departmentManagementViewKeyboardMarkupDepartmentsManagementTable.add(addDepartmentRow);
        } else {
            departmentManagementViewKeyboardMarkupDepartmentsManagementTable =
                    List.of(addDepartmentRow);
        }

        departmentsManagementViewKeyboardMarkUp.setKeyboard(departmentManagementViewKeyboardMarkupDepartmentsManagementTable);

        return departmentsManagementViewKeyboardMarkUp;
    }

    private List<List<InlineKeyboardButton>> departmentsManagementViewKeyboardMarkUpDepartmentsManagementTable(List<Department> departments) {
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        departments.forEach(d -> {
            var text = String.format("ЗАВЕДЕНИЕ %d", d.getId());
            List<InlineKeyboardButton> row = List.of(
                    button(text, "/asd"),
                    button("X", "/asdasd")
            );
            rows.add(row);
        });

        return rows;
    }


    /**
     * Вью запроса telegramUserId для добавления департамента
     * CompanyView.DEPARTMENT_TELEGRAM_USER_ID_REQUEST_VIEW
     * */
    public SendMessage departmentTelegramUserIdRequestView(Update update) {
        var message = "Введите id пользователя telegram \n" +
                "(требуется запросить у пользователя числовой идентификатор telegram user id - " +
                "он это может сделать в официальном боте @getmyid_bot) \n\n" +
                "В последствии, если будет введен неправильный id, пользователя можно будет легко удалить.";
        var answer = messageUtil.blankAnswer(update);
        answer.setText(message);

        return answer;
    }

    /**
     * Вью уведомляет о неверно введенном telegramUserId при регистрации департамента
     * СВОЕГО ВЬЮ в CompanyView нет
     * Возвращает вью запроса telegramUserId
     * CompanyView.DEPARTMENT_TELEGRAM_USER_ID_REQUEST_VIEW
     * */
    public SendMessage invalidDepartmentTelegramUserIdErrorView(Update update) {
        var message = "Введенная строка не является корректным telegramUserId";
        var answer = departmentTelegramUserIdRequestView(update);

        return  messageUtil.addServiceMessage(answer, message);
    }

    /**
     * Вью уведомляет о том, что введенный при регистрации департамента telegramUserId уже есть в системе
     * СВОЕГО ВЬЮ в CompanyView нет
     * Возвращает меню управления департаментами
     * CompanyView.DEPARTMENTS_MANAGEMENT_VIEW
     * */
    public SendMessage alreadyRegisteredTelegramUserIdErrorView(Update update, List<Department> departments) {
        var message = "<b>Введенный Телеграм-id уже " +
                "зарегистрирован в системе</b>";
        var answer = departmentsManagementView(update, departments);

        return messageUtil.addServiceMessage(answer, message);
    }

    /**
     * Вью уведомляет об успешной регистрации департамента
     * СВОЕГО ВЬЮ в CompanyView нет
     * Возвращает меню управления департаментами
     * CompanyView.DEPARTMENTS_MANAGEMENT_VIEW
     * */
    public SendMessage newDepartmentRegistrationNotificationView(Update update, List<Department> departments) {
        var message = "<b>Телеграм-id \"%s\", " +
                "который будет отвечать за новое заведение, зарегистрирован. " +
                "Вы можете отредактировать/удалить его в меню менеджмента заведений.</b>";
        var answer = departmentsManagementView(update, departments);
        var serviceMessage = String.format(
                message,
                update.getMessage().getText()
        );

        return  messageUtil.addServiceMessage(answer, serviceMessage);
    }



    private InlineKeyboardButton button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
