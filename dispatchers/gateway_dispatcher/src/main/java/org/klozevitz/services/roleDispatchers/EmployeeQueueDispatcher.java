package org.klozevitz.services.roleDispatchers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.services.interfaces.IQueueDispatcher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.klozevitz.RabbitQueue.*;

@Log4j
@Service
@RequiredArgsConstructor
public class EmployeeQueueDispatcher implements IQueueDispatcher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = "employee_update_queue")
    public void dispatch(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                rabbitTemplate.convertAndSend(EMPLOYEE_COMMAND_UPDATE_QUEUE, update);
            } else {
                rabbitTemplate.convertAndSend(EMPLOYEE_TEXT_UPDATE_QUEUE, update);
            }
        } else if (update.hasCallbackQuery()) {
            rabbitTemplate.convertAndSend(EMPLOYEE_CALLBACK_UPDATE_QUEUE, update);
        }
    }
}
