package org.klozevitz.services.implementations.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.CompanyView;
import org.klozevitz.services.implementations.util.DepartmentRegistrar;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "company", name = CompanyView.NEW_DEPARTMENT_TELEGRAM_USER_ID_REQUEST_VIEW)
@RequiredArgsConstructor
public class NewDepartmentTelegramUserIdRequestView extends AbstractTelegramView {
    private final DepartmentRegistrar registrar;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return null;
        }

        var registrationResult = registrar.register(update);

        return answerAsList(registrationResult);
    }

}
