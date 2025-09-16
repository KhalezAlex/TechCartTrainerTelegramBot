package org.klozevitz.components.io.answerConsumer;

import lombok.RequiredArgsConstructor;
import org.klozevitz.RabbitQueue;
import org.klozevitz.components.UpdateController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Service
@RequiredArgsConstructor
public class DeleteMessageConsumer implements IDeleteMessageConsumer {
    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = RabbitQueue.DELETION_QUEUE)
    public void consumeDeleteMessage(DeleteMessage deleteMessage) {
        updateController.deleteBuggedMessage(deleteMessage);
    }
}
