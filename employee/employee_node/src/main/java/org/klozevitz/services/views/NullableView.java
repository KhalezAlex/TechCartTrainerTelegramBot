package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static org.klozevitz.enities.users.enums.views.EmployeeView.NULLABLE_VIEW;

@Log4j
@TelegramView(role = "employee", name = NULLABLE_VIEW)
@RequiredArgsConstructor
public class NullableView extends AbstractTelegramView {

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {

        return null;
    }
}
