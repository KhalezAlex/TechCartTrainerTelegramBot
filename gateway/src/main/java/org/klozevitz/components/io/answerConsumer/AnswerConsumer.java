package org.klozevitz.components.io.answerConsumer;

import lombok.RequiredArgsConstructor;
import org.klozevitz.components.UpdateController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.klozevitz.RabbitQueue.ANSWER_QUEUE;

@Service
@RequiredArgsConstructor
public class AnswerConsumer implements IAnswerConsumer {
    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = ANSWER_QUEUE)
    public void consumeAnswer(SendMessage answer) {
        updateController.setView(answer);
    }
}
