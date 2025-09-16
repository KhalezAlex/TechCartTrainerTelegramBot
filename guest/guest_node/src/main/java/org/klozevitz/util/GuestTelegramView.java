package org.klozevitz.util;

import lombok.AllArgsConstructor;
import org.klozevitz.MessageUtil;
import org.klozevitz.enities.users.AppUser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GuestTelegramView {
    private final String WELCOME_VIEW_MESSAGE = "Вы находитесь на главной странице чат-бота.\n" +
            "Ваша компания еще не зарегистрирована.\n" +
            "Нажмите кнопку для продолжения регистрации.";
    private final String EMAIL_REQUEST_MESSAGE = "Введите адрес электронной почты для завершения регистрации";
    private final String EMAIL_CONFIRMATION_REQUEST_MESSAGE = "Ссылка на подтверждение регистрации выслана " +
            "на указанный Вами ящик электронной почты.\n" +
            "Для подтверждения регистрации пройдите по ссылке в письме.\n\n" +
            "Если требуется изменить адрес, нажмите кнопку внизу:";
    private final String EMAIL_CONFIRMATION_NOTIFICATION_MESSAGE = "Регистрация завершена!\n" +
            "Вам доступно полноценное пользование ботом.\n" +
            "Для того, чтобы попасть на главный экран, нажмите на кнопку внизу.";
    private final String EMAIL_CANNOT_BE_SENT_MESSAGE = "Письмо с инструкциями по валидации " +
            "не может быть направлено на данную электронную почту: %s\n" +
            "Повторите процедуру регистрации.\n";
    private final String WRONG_EMAIL_NOTIFICATION_MESSAGE = "Адрес, который Вы ввели, не является " +
            "валидным адресом электронной почты!";
    private final String ALREADY_REGISTERED_EMAIL_NOTIFICATION_MESSAGE = "Адрес, который Вы ввели, " +
            "уже зарегистрирован в системе";
    private final String PREVIOUS_VIEW_ERROR_MESSAGE = "<b>Вы совершили действие, которое привело к остановке " +
            "выполнения процесса. Вернемся к предыдущему экрану:</b>";
    private final String NULL_COMPANY_STATE_ERROR_NOTIFICATION_MESSAGE = "<b>Произошла непредвиденная ошибка. " +
            "Свяжитесь со специалистом технической поддержки для устранения ошибки.</b>";
    private final String DEPARTMENT_REGISTRATION_NOTIFICATION_MESSAGE = "<b>Телеграм-id \"%s\", " +
            "который будет отвечать за новое заведение, зарегистрирован. " +
            "Вы можете отредактировать/удалить его в меню менеджмента заведений.</b>";
    private final String ALREADY_REGISTERED_TELEGRAM_USER_ID_ERROR_MESSAGE = "<b>Введенный Телеграм-id уже " +
            "зарегистрирован в системе</b>";
    private final String WRONG_APP_USER_ROLE_ERROR_MESSAGE = "Вы зарегистрированы, как персонал или как человек, " +
            "отвечающий за отделение компании- функционал этого чата для Вас не доступен.\n" +
            "Если Вам необходимо зарегистрироваться, как другая компания, попросите организацию, зарегистрировавшую" +
            "Вас, удалить Вас или переназначить руководство отделом на другой аккаунт телеграм.";
    private MessageUtil messageUtil;

    /**
     * Первое приветственное сообщение для незарегистрированных пользователей
     * CompanyView.UNREGISTERED_WELCOME_VIEW
     * */
    public SendMessage welcomeView(Update update) {
        var chatId = chatId(update);
        var unregisteredWelcomeView = new SendMessage();
        var unregisteredInlineKeyboardMarkup = unregisteredInlineKeyboardMarkup();

        unregisteredWelcomeView.setText(WELCOME_VIEW_MESSAGE);
        unregisteredWelcomeView.setReplyMarkup(unregisteredInlineKeyboardMarkup);
        unregisteredWelcomeView.setChatId(chatId);

        return unregisteredWelcomeView;
    }

    private InlineKeyboardMarkup unregisteredInlineKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var row = List.of(
                button("РЕГИСТРАЦИЯ", "/register")
        );
        var keyboardRows = List.of(row);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }


    /**
     * Вьюхи, связанные с регистрацией
     * */


    /**
     * Вью запрашивает мейл у пользователя
     * CompanyView.EMAIL_REQUEST_VIEW
     * */
    public SendMessage registrationEmailRequestView(Update update) {
        var chatId = chatId(update);

        return textMessage(EMAIL_REQUEST_MESSAGE, chatId);
    }

    /**
     * Вью запрашивает подтверждение мейла пользователя
     * CompanyView.EMAIL_CONFIRMATION_REQUEST_VIEW
     * */
    public SendMessage emailConfirmationRequestView(Update update) {
        var answer = messageUtil.blankAnswer(update);
        var successNotificationViewAbortRegistrationKeyboardMarkup =
                successNotificationViewAbortRegistrationKeyboardMarkup();
        answer.setText(EMAIL_CONFIRMATION_REQUEST_MESSAGE);
        answer.setReplyMarkup(successNotificationViewAbortRegistrationKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup successNotificationViewAbortRegistrationKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var row = List.of(
                button("ЗАРЕГИСТРИРОВАТЬСЯ ЗАНОВО", "/registration_abort")
        );
        var keyboardRows = List.of(row);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    /**
     * Вью уведомляет пользователя о невозможности послать электронную почту
     * и отправляет на вью UNREGISTERED_WELCOME_VIEW
     * СВОЕГО ВЬЮ В CompanyView НЕТ
     * */
    public SendMessage emailCanNotBeSentNotificationView(Update update) {
        var email = update.getMessage().getText();
        var errorMessage = String.format(EMAIL_CANNOT_BE_SENT_MESSAGE, email);
        var answer = welcomeView(update);

        return messageUtil.addServiceMessage(answer, errorMessage);
    }

    /**
     * Вью уведомляет о том, что был введеден некорректный мейл,
     * и отправляет на вью EMAIL_REQUEST_VIEW
     * СВОЕГО ВЬЮ В CompanyView НЕТ
     * */
    public SendMessage invalidEmailNotificationView(Update update) {
        var answer = registrationEmailRequestView(update);

        return messageUtil.addServiceMessage(answer, WRONG_EMAIL_NOTIFICATION_MESSAGE);
    }

    /**
     * Вью уведомляет о том, что был введеден уже зарегистрированный мейл,
     * и отправляет на вью EMAIL_REQUEST_VIEW
     * СВОЕГО ВЬЮ В CompanyView НЕТ
     * */
    public SendMessage alreadyRegisteredEmailNotificationView(Update update) {
        var answer = registrationEmailRequestView(update);

        return messageUtil.addServiceMessage(answer, ALREADY_REGISTERED_EMAIL_NOTIFICATION_MESSAGE);
    }

    /**
     * Вью уведомляет пользователя об успешном завершении регистрации
     * CompanyView.EMAIL_CONFIRMATION_NOTIFICATION_VIEW
     * */
    public SendMessage emailConfirmationNotificationView(long chatId) {
        var answer = messageUtil.blankAnswer(chatId);
        var emailConfirmationNotificationViewKeyboardMarkup =
                emailConfirmationNotificationViewKeyboardMarkup();

        answer.setText(EMAIL_CONFIRMATION_NOTIFICATION_MESSAGE);
        answer.setReplyMarkup(emailConfirmationNotificationViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup emailConfirmationNotificationViewKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var row = List.of(
                button("ОСНОВНОЙ ЭКРАН", "/welcome")
        );
        var keyboardRows = List.of(row);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }


    /**
     * Вьюхи для зарегистрированных пользователей
     * */


    /**
     * Вью уведомляет об успешной регистрации департамента
     * СВОЕГО ВЬЮ в CompanyView нет
     * Возвращает меню управления департаментами
     * CompanyView.DEPARTMENTS_MANAGEMENT_VIEW
     * */
//    public SendMessage newDepartmentRegistrationNotificationView(Update update, AppUser currentAppUser) {
//        var answer = departmentsManagementView(update, currentAppUser);
//        var serviceMessage = String.format(
//                DEPARTMENT_REGISTRATION_NOTIFICATION_MESSAGE,
//                update.getMessage().getText()
//        );
//
//        return  messageUtil.addServiceMessage(answer, serviceMessage);
//    }

    /**
     * Вью уведомляет о том, что введенный при регистрации департамента telegramUserId уже есть в системе
     * СВОЕГО ВЬЮ в CompanyView нет
     * Возвращает меню управления департаментами
     * CompanyView.DEPARTMENTS_MANAGEMENT_VIEW
     * */
//    public SendMessage alreadyRegisteredTelegramUserIdErrorView(Update update, AppUser currentAppUser) {
//        var answer = departmentsManagementView(update, currentAppUser);
//
//        return messageUtil.addServiceMessage(answer, ALREADY_REGISTERED_TELEGRAM_USER_ID_ERROR_MESSAGE);
//    }

    /**
     * Вью предназначен для тех, кто имеет аккаунт в другом чате, и попробовал воспользоваться функционалом текущего.
     * СВОЕГО ВЬЮ в CompanyView нет
     * */
    public SendMessage wrongAppUserRoleErrorView(Update update) {
        var answer = messageUtil.blankAnswer(update);

        answer.setText(WRONG_APP_USER_ROLE_ERROR_MESSAGE);

        return answer;
    }

    /**
     * Базовое текстовое сообщение
     * */
    public SendMessage textMessage(String message, Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(message);
        return answer;
    }

    /**
     * UTILS
     * */

    private long chatId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }
}
