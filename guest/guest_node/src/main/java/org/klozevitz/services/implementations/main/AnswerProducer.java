package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import org.klozevitz.RabbitQueue;
import org.klozevitz.services.interfaces.IAnswerProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class AnswerProducer implements IAnswerProducer {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(RabbitQueue.ANSWER_QUEUE, sendMessage);
    }
}
