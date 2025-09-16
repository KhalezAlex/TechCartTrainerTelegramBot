package org.klozevitz.util;

import lombok.RequiredArgsConstructor;
import org.klozevitz.MessageUtil;
import org.klozevitz.enities.menu.Category;
import org.klozevitz.enities.users.Employee;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentTelegramView {
    private final MessageUtil messageUtil;


    /**
     * Первое приветственное сообщение
     * DepartmentView.WELCOME_VIEW
     * */
    public SendMessage welcomeView(Update update) {
        var message = "Вы находитесь на главной странице чат-бота.\n" +
                "Вам доступны следующие действия:";
        var answer = messageUtil.blankAnswer(update);
        var welcomeViewKeyboardMarkup = welcomeViewKeyboardMarkup();

        answer.setText(message);
        answer.setReplyMarkup(welcomeViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup welcomeViewKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var keyboardRows = List.of(
                List.of(button("УПРАВЛЕНИЕ ПЕРСОНАЛОМ", "/employee_management")),
                List.of(button("УПРАВЛЕНИЕ МАТЕРИАЛАМИ", "/resources_management")),
                List.of(button("УПРАВЛЕНИЕ ПРОФИЛЕМ", "/profile_management"))
        );

        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     * Вью управления сотрудниками
     * DepartmentView.EMPLOYEES_MANAGEMENT_VIEW
     * */
    public SendMessage employeesManagementView(Update update, List<Employee> employees) {
        var message = "На этой странице Вы можете управлять " +
                "Вашим персоналом. Как только Вы добавите первого сотрудника, он появится в списке.";
        var answer = messageUtil.blankAnswer(update);
        var employeesManagementViewKeyboardMarkUp =
                employeesManagementViewKeyboardMarkUp(employees);

        answer.setText(message);
        answer.setReplyMarkup(employeesManagementViewKeyboardMarkUp);

        return answer;
    }

    private InlineKeyboardMarkup employeesManagementViewKeyboardMarkUp(List<Employee> employees) {
        var keyboardMarkUp = new InlineKeyboardMarkup();
        var addEmployeeRow = List.of(
                button("ДОБАВИТЬ СОТРУДНИКА", "/employee_tg_id_request_view"),
                button("Выход", "/start")
        );

        List<List<InlineKeyboardButton>> departmentsManagementTable;

        if (!employees.isEmpty()) {
            departmentsManagementTable =
                    employeesManagementViewKeyboardMarkUpEmployeeManagementTable(employees);
            departmentsManagementTable.add(addEmployeeRow);
        } else {
            departmentsManagementTable =
                    List.of(addEmployeeRow);
        }

        keyboardMarkUp
                .setKeyboard(
                        departmentsManagementTable
                );

        return keyboardMarkUp;
    }

    //TODO: сделать нормальную вьюху с кнопками и ссылкой на вью управления каждым конкретным работником
    private List<List<InlineKeyboardButton>> employeesManagementViewKeyboardMarkUpEmployeeManagementTable(List<Employee> employees) {
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        employees.forEach(employee -> {
            var text = String.format("telegram id: %d", employee.getId());
            List<InlineKeyboardButton> row = List.of(
                    button(text, "/asd"),
                    button("X", "/asdasd")
            );
            rows.add(row);
        });

        return rows;
    }


    /**
     * Вью запроса телеграм-id для регистрации нового работника
     * DepartmentView.EMPLOYEE_TG_USER_ID_REQUEST_VIEW
     * */
    public SendMessage employeeTelegramUserIdRequestView(Update update) {
        var message = "Введите id пользователя telegram \n" +
                "(требуется запросить у пользователя числовой идентификатор telegram user id - " +
                "он это может сделать в официальном боте @getmyid_bot) \n\n" +
                "В последствии, если будет введен неправильный id, пользователя можно будет легко удалить.";
        var answer = textMessage(message, update);
        var keyboard = new InlineKeyboardMarkup();
        var homePageRow = homepageKeyboardRow();

        keyboard.setKeyboard(
                List.of(homePageRow)
        );
        answer.setReplyMarkup(keyboard);

        return answer;
    }


    /**
     * Вью отображения результата регистрации при невалидном telegramUserId работника
     * DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW
     * с ошибкой
     * */
    public SendMessage invalidEmployeeTelegramUserIdNotificationView(Update update) {
        var errorMessage = "<b>Введенная строка не является корректным telegramUserId</b>";;
        var answer = employeeTelegramUserIdRequestView(update);

        answer = messageUtil.addServiceMessage(answer, errorMessage);
        answer.enableHtml(true);

        return answer;
    }


    /**
     * Вью отображения результата регистрации при уже зарегистрированном telegramUserId работника
     * DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW
     * с ошибкой
     * */
    public SendMessage alreadyRegisteredEmployeeTelegramUserIdNotificationView(Update update) {
        var errorMessage = "<b>Введенный Телеграм-id уже зарегистрирован в системе</b>";
        var answer = employeeTelegramUserIdRequestView(update);

        answer = messageUtil.addServiceMessage(answer, errorMessage);
        answer.enableHtml(true);

        return answer;
    }


    // TODO: не закончено!!!
    /**
     * Вью отображения успешной регистрации
     * возвращает DepartmentView.EMPLOYEE_TELEGRAM_USER_ID_REQUEST_VIEW с уведомлением об успешной регистрации
     * сотрудника
     * */
    public SendMessage employeeRegistrationNotificationView(Update update, List<Employee> employees) {
        var message = "<b>Телеграм-id \"%s\", " +
                "нового сотрудника зарегистрирован. " +
                "Вы можете отредактировать/удалить его в меню менеджмента персонала.</b>";
        var answer = employeesManagementView(update, employees);

        answer = messageUtil.addServiceMessage(answer, message);
        answer.enableHtml(true);

        return answer;
    }


    /**
     * Вью управления меню
     * DepartmentView.RESOURCES_MANAGEMENT_VIEW
     * */
    public SendMessage resourcesManagementView(Update update, List<Category> resources) {
        var message = "На этой странице Вы можете управлять " +
                "Вашими методическими материалами. Как только Вы добавите первый, он появится в списке.";
        var answer = messageUtil.blankAnswer(update);
        var resourcesManagementViewKeyboardMarkUp =
                resourcesManagementViewKeyboardMarkUp(resources);

        answer.setText(message);
        answer.setReplyMarkup(resourcesManagementViewKeyboardMarkUp);

        return answer;
    }

    private InlineKeyboardMarkup resourcesManagementViewKeyboardMarkUp(List<Category> resources) {
        var keyboardMarkUp = new InlineKeyboardMarkup();
        var addResourceRow = List.of(
                button("ДОБАВИТЬ КАТЕГОРИЮ МЕНЮ", "/add_resource"),
                button("Выход", "/start")
        );

        List<List<InlineKeyboardButton>> resourcesManagementTable;

        if (!resources.isEmpty()) {
            resourcesManagementTable =
                    resourcesManagementViewKeyboardMarkUpResourcesManagementTable(resources);
            resourcesManagementTable.add(addResourceRow);
        } else {
            resourcesManagementTable =
                    List.of(addResourceRow);
        }

        keyboardMarkUp
                .setKeyboard(
                        resourcesManagementTable
                );

        return keyboardMarkUp;
    }

    private List<List<InlineKeyboardButton>> resourcesManagementViewKeyboardMarkUpResourcesManagementTable(List<Category> resources) {
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        resources.forEach(e -> {
            var text = String.format("Меню %d", e.getId());
            List<InlineKeyboardButton> row = List.of(
                    button(text, "/asd"),
                    button("X", "/asdasd")
            );
            rows.add(row);
        });

        return rows;
    }


    /**
     * Вью запроса документа
     * DepartmentView.RESOURCE_REQUEST_VIEW
     * */
    public SendMessage resourceRequestView(Update update) {
        var message = "Ожидание excel-документа формата, представленного ниже \n\n" +
                "В последствии, его можно будет легко удалить.";
        var answer = textMessage(message, update);
        var keyboard = new InlineKeyboardMarkup();
        var homePageRow = homepageKeyboardRow();

        keyboard.setKeyboard(
                List.of(homePageRow)
        );
        answer.setReplyMarkup(keyboard);

        return answer;
    }


    /**
     * Вью просмотра результатов загрузки ресурсов для изучения
     * DepartmentView.RESOURCES_MANAGEMENT_VIEW
     * с сообщением о загрузке
     * */
    public SendMessage parsedFileNotification(Update update, List<Category> resources) {
        var serviceMessagePattern = "<b>Вы загрузили excel-документ с категорией (-ями) меню:\n%s\n" +
                "Просмотреть информацию и работать с ним Вы можете в разделе \"УПРАВЛЕНИЕ МАТЕРИАЛАМИ\"</b>";
        var categoriesList = parsedFileNotificationCategoriesList(resources);
        var serviceMessage = String.format(serviceMessagePattern, categoriesList);
        var answer = resourcesManagementView(update, resources);

        messageUtil.addServiceMessage(answer, serviceMessage);

        return answer;
    }

    private String parsedFileNotificationCategoriesList(List<Category> resources) {
        final StringBuilder str = new StringBuilder();
        resources.forEach(category -> str.append(category.getName()).append("\n"));
        return str.toString();
    }


    /**
     * Вью оповещения о невозможности спарсить файл
     * DepartmentView.WELCOME_VIEW
     * с сообщением о невозможности спарсить файл
     * */
    public SendMessage parseErrorNotification(Update update) {
        var serviceMessage = "<b>Вы Загрузили документ, который " +
                "невозможно обработать. Пожалуйста, попробуйте снова!</b>";
        var answer = welcomeView(update);

        messageUtil.addServiceMessage(answer, serviceMessage);

        return answer;
    }


    /**
     * Вью оповещения о невозможности загрузить файл
     * DepartmentView.WELCOME_VIEW
     * с сообщением о невозможности загрузить файл
     * */
    public SendMessage uploadFailNotification(Update update) {
        var serviceMessage = "Бот не смог загрузить файл из-за технической ошибки. Пожалуйста, попробуйте снова.";
        var answer = welcomeView(update);

        messageUtil.addServiceMessage(answer, serviceMessage);

        return null;
    }



    /**
     * UTILS
     * */

    /**
     * Базовая инлайн-кнопка
     * */
    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    /**
     * Базовое текстовое сообщение
     * */
    public SendMessage textMessage(String message, Update update) {
        return SendMessage.builder()
                .chatId(chatId(update))
                .text(message)
                .build();
    }

    private long chatId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }

    private List<InlineKeyboardButton> homepageKeyboardRow() {
        return List.of(
                button("Главная страница", "/start")
        );
    }
}
