package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.services.interfaces.IBasicUpdateConsumer;
import org.klozevitz.services.interfaces.IMainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.klozevitz.RabbitQueue.*;

@Log4j
@Service
@RequiredArgsConstructor
public class UpdateConsumer implements IBasicUpdateConsumer {
    private final IMainService mainService;

    @Override
    @RabbitListener(queues = GUEST_TEXT_UPDATE_QUEUE)
    public void consumeTextUpdate(Update update) {
        mainService.processTextUpdate(update);
    }

    @Override
    @RabbitListener(queues = GUEST_COMMAND_UPDATE_QUEUE)
    public void consumeCommandUpdate(Update update) {
        mainService.processCommandUpdate(update);
    }

    @Override
    @RabbitListener(queues = GUEST_CALLBACK_UPDATE_QUEUE)
    public void consumeCallbackUpdate(Update update) {
        mainService.processCallbackUpdate(update);
    }
}
