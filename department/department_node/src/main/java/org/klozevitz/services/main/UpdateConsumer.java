package org.klozevitz.services.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.RabbitQueue;
import org.klozevitz.services.interfaces.IUpdateConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Service
@RequiredArgsConstructor
public class UpdateConsumer implements IUpdateConsumer {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = RabbitQueue.DEPARTMENT_UPDATE_QUEUE)
    public void consumeUpdate(Update update) {
        mainService.processUpdate(update);
    }
}
