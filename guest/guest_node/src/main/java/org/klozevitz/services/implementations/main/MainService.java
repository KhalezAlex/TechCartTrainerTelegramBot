package org.klozevitz.services.implementations.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.services.interfaces.IAnswerProducer;
import org.klozevitz.services.interfaces.IMainService;
import org.klozevitz.updateProcessor.UpdateProcessor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@RequiredArgsConstructor
public class MainService implements IMainService {
    private final UpdateProcessor textUpdateProcessor;
    private final UpdateProcessor commandUpdateProcessor;
    private final UpdateProcessor callbackUpdateProcessor;
    private final IAnswerProducer answerProducer;

    @Override
    public void processTextUpdate(Update update) {
        processUpdate(update, textUpdateProcessor);
    }

    @Override
    public void processCommandUpdate(Update update) {
        processUpdate(update, commandUpdateProcessor);
    }

    @Override
    public void processCallbackUpdate(Update update) {
        processUpdate(update, callbackUpdateProcessor);
    }

    private void processUpdate(Update update, UpdateProcessor callbackUpdateProcessor) {
        try {
            var answer = callbackUpdateProcessor.processUpdate(update);
            if (answer != null) {
                sendAnswer(answer);
            }
        } catch (Exception e) {
            log.debug("*******************");
            log.error(e);
            log.debug("*******************");
        }
    }

    private void sendAnswer(ArrayList<SendMessage> answer) {
        for (SendMessage sendMessage : answer) {
            answerProducer.produceAnswer(sendMessage);
        }
    }
}
