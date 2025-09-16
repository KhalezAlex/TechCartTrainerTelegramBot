package org.klozevitz.services.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.IAnswerProducer;
import org.klozevitz.RabbitQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Log4j
@Service
@RequiredArgsConstructor
public class AnswerProducer implements IAnswerProducer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswer(BotApiMethod<?> botApiMethod) {
        rabbitTemplate.convertAndSend(RabbitQueue.ANSWER_QUEUE, botApiMethod);
    }

    @Override
    public void produceBuggedMessage(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(RabbitQueue.DELETION_QUEUE, deleteMessage);
    }
}
