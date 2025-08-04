package org.klozevitz.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Service
@RequiredArgsConstructor
@RabbitListener(queues = "gateway_update")
public class GatewayUpdateDispatcher implements IGatewayUpdateDispatcher {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void dispatchByRole(Update update) {

    }
}
