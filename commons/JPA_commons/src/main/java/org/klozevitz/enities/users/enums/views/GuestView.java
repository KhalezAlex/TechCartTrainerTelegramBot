package org.klozevitz.enities.users.enums.views;

public class GuestView {
    public static final String NULLABLE_VIEW = "nullable_view";
    public static final String WELCOME_VIEW = "welcome_view";
    public static final String REGISTRATION_EMAIL_REQUEST_VIEW = "registration_email_request_view";
    public static final String CONFIRMATION_EMAIL_SENT_VIEW = "confirmation_email_sent_view";
    // Внимательно: стр. 6 и 9: вью разные, но обработчик у них будет один- RegistrationEmailRequestView.java,
    // тк, при попытке зарегистрироваться с плохим мейлом, нужно будет ввести мейл заново, а логика уже есть!!!
    public static final String INVALID_EMAIL_NOTIFICATION_VIEW = "registration_email_request_view";
    public static final String EMAIL_CONFIRMATION_NOTIFICATION_VIEW = "email_confirmation_notification_view";
}
