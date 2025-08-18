package org.klozevitz.components.io.updateProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.RabbitQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.klozevitz.RabbitQueue.GATEWAY_QUEUE;

@Service
@Log4j
@RequiredArgsConstructor
public class UpdateProducer implements IUpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    // TODO: когда будет понятно, какие типы сообщений будет обрабатывать бот, написать метод получения id и сообщения
    // строка 21
    @Override
    public void produce(String queue, Update update) {
        var message = String.format("gateway||UpdateProducer: сообщение от пользователя %s: ",
                "СЧИТАТЬ ИМЯ ПОЛЬЗОВАТЕЛЯ и сообщение");
        log.debug(message);
        rabbitTemplate.convertAndSend(GATEWAY_QUEUE, update);
    }
}
