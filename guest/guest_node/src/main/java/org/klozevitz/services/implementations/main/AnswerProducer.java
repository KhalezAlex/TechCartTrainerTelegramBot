package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import org.klozevitz.IAnswerProducer;
import org.klozevitz.RabbitQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Service
@RequiredArgsConstructor
public class AnswerProducer implements IAnswerProducer {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void produceAnswer(BotApiMethod<?> sendMessage) {
        rabbitTemplate.convertAndSend(RabbitQueue.ANSWER_QUEUE, sendMessage);
    }

    @Override
    public void produceBuggedMessage(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(RabbitQueue.DELETION_QUEUE, deleteMessage);
    }
}
