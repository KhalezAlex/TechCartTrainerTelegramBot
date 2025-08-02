package org.klozevitz.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.components.io.IUpdateProducer;
import org.klozevitz.telegram_bot_component.GatewayTgBotComponent;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.klozevitz.RabbitQueue.GATEWAY_QUEUE;

@Log4j
@Component
@RequiredArgsConstructor
public class UpdateController {
    private final IUpdateProducer updateProducer;
    private GatewayTgBotComponent bot;

    public void registerBot(GatewayTgBotComponent bot) {
        this.bot = bot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Полученное сообщение - null - такого вообще не должно было произойти");
            return;
        }

        if (update.hasMessage() || update.hasCallbackQuery() || update.hasPoll()) {
            updateProducer.produce(GATEWAY_QUEUE.queue(), update);
        } else {
            var errorMessage = String.format("неподдерживаемый формат сообщения от пользователя %s",
                    telegramUserId(update));
            log.error(errorMessage);
        }
    }

//    private List<Long> chatHistory(Update update) {
//
//    }

    // TODO: null выдавать не должен!!! разобраться с приходом ответа из викторины
    private Long telegramUserId(Update update) {
        return update.hasMessage() ? update.getMessage().getFrom().getId()
                : update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId()
                : null;
    }
}
