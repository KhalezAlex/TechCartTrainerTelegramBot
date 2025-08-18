package org.klozevitz.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.users.enums.Role;
import org.klozevitz.entityService.IUserService;
import org.klozevitz.services.interfaces.IQueueDispatcher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static org.klozevitz.RabbitQueue.*;
import static org.klozevitz.enities.users.enums.Role.*;

@Log4j
@Service
@RequiredArgsConstructor
public class GatewayQueueDispatcher implements IQueueDispatcher {
    private final RabbitTemplate rabbitTemplate;
    private final IUserService userService;

    @Override
    @RabbitListener(queues = "gateway_queue")
    public void dispatch(Update update) {
        long telegramUserId = telegramUserId(update);
        var currentAppUser = userService.findByTelegramUserId(telegramUserId);

        if (currentAppUser == null) {
            userService.newAppUser(telegramUserId);

            rabbitTemplate.convertAndSend(GUEST_UPDATE_QUEUE, update);
        } else {
            var role = currentAppUser.getRole();
            var queue = queueByRole(role);
            assert queue != null;
            rabbitTemplate.convertAndSend(queue, update);
        }
    }

    private long telegramUserId(Update update) {
        return Optional.ofNullable(update)
                .map(u ->
                        u.hasMessage() ? u.getMessage().getFrom().getId() :
                                u.hasCallbackQuery() ? u.getCallbackQuery().getFrom().getId() :
                                        u.hasPollAnswer() ? u.getPollAnswer().getUser().getId() :
                                                null
                )
                .orElseThrow(() -> new IllegalArgumentException("Update пустой"));
    }

    private String queueByRole(Role role) {
        if (role.equals(GUEST)) {
            return GUEST_UPDATE_QUEUE;
        }
        if (role.equals(COMPANY)) {
            return COMPANY_UPDATE_QUEUE;
        }
        if (role.equals(DEPARTMENT)) {
            return DEPARTMENT_UPDATE_QUEUE;
        }
        if (role.equals(EMPLOYEE)) {
            return EMPLOYEE_UPDATE_QUEUE;
        }
        return null;
    }
}
