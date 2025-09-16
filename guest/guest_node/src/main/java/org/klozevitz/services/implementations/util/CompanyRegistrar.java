package org.klozevitz.services.implementations.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.CryptoTool;
import org.klozevitz.IRegistrar;
import org.klozevitz.entityService.users.CompanyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Log4j
@Service
@RequiredArgsConstructor
public class CompanyRegistrar implements IRegistrar {
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${mailService.activationUrl}")
    private String activationUrl;

    private final CryptoTool cryptoTool;
    private final CompanyService companyService;
    private final JavaMailSender javaMailSender;

    @Override
    public String register(Update update) {
        var email = update.getMessage().getText();

        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
        } catch (AddressException e) {
            log.error("Прислан невалидный email: " + email);
            return "invalid";
        }

        var optionalUserByEmail = companyService.findByEmail(email);
        // если пользователь ввел email, который уже зарегистрирован, вернуть false
        if (optionalUserByEmail.isPresent()) {
            log.error("Пользователь с такой почтой уже зарегистрирован: " + email);
            return "already registered";
        }

        var telegramUserId = telegramUserId(update);
        var cryptoUserId = cryptoTool.hashOf(telegramUserId);
        companyService.createNewCompany(telegramUserId, email);

        send(email, cryptoUserId);
        return "sent";
    }

    public void send(String emailTo, String cryptoId) {
        var subject = "Активация аккаунта";
        var messageBody = activationMessageBody(cryptoId);

        var message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(messageBody);

        javaMailSender.send(message);
    }

    private String activationMessageBody(String cryptoId) {
        var message = String.format("Пройдите по ссылке ниже для завершения регистрации:\n%s",
                activationUrl);
        return message.replace("{id}", cryptoId);
    }

    protected long telegramUserId(Update update) {
        return Optional.ofNullable(update)
                .map(u ->
                        u.hasMessage() ? u.getMessage().getFrom().getId() :
                                u.hasCallbackQuery() ? u.getCallbackQuery().getFrom().getId() :
                                        u.hasPollAnswer() ? u.getPollAnswer().getUser().getId() :
                                                null
                )
                .orElseThrow(() -> new IllegalArgumentException("Update пустой"));
    }
}
