package org.klozevitz.services.implementations.util;

import lombok.RequiredArgsConstructor;
import org.klozevitz.CryptoTool;
import org.klozevitz.RabbitQueue;
import org.klozevitz.enities.users.enums.views.GuestView;
import org.klozevitz.entityService.users.AppUserService;
import org.klozevitz.entityService.users.CompanyService;
import org.klozevitz.entityService.users.GuestService;
import org.klozevitz.util.GuestTelegramView;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ActivationService {
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final GuestService guestService;
    private final CryptoTool cryptoTool;
    private final GuestTelegramView telegramView;
    private final RabbitTemplate rabbitTemplate;

    public boolean activate(String cryptoUserId) {
        var telegramUserId = cryptoTool.idOf(cryptoUserId);
        var optionalAppUser = appUserService.findByTelegramUserId(telegramUserId);

        if (optionalAppUser.isEmpty()) {
            return false;
        }

        appUserService.setCompanyRole(telegramUserId);
        guestService.deleteByTelegramUserId(telegramUserId);
        //TODO:
        // 3) ПОСЛЕ ОТПРАВКИ В ОЧЕРЕДЬ выставить компании вью nullable и менеджить ее уже в другом модуле
        companyService.setCurrentView(telegramUserId, GuestView.EMAIL_CONFIRMATION_NOTIFICATION_VIEW);

        produceNotificationMessageToCompanyAnswerQueue(telegramUserId);

        return true;
    }

    private void produceNotificationMessageToCompanyAnswerQueue(Long telegramUserId) {
        var answer = telegramView.emailConfirmationNotificationView(telegramUserId);
        var messagesToProcess = new ArrayList<>() {{
            add(answer);
            add(SendMessage.builder()
                    .text("")
                    .chatId(answer.getChatId())
                    .build());
        }};

        messagesToProcess.forEach(m ->
                rabbitTemplate.convertAndSend(RabbitQueue.ANSWER_QUEUE, m));

    }
}
