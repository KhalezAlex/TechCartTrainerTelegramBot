package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.users.enums.views.EmployeeView;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.EmployeeTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Log4j
@TelegramView(role = "employee", name = EmployeeView.TEST_VIEW)
@RequiredArgsConstructor
public class TestView extends AbstractTelegramView {
    private final EmployeeService employeeService;
    private final EmployeeTelegramView telegramView;


    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (updateValid(update)) {
            return null;
        }

        SendMessage answer;

        if (update.hasCallbackQuery()) {
            answer = processCompleteItemTestAnswer(update);
        } else {
            answer = processIngredientAnswer(update);
        }

        return answer == null ?
                null :
                answerAsList(answer);
    }

    private boolean updateValid(Update update) {
        return !update.hasCallbackQuery() &&
                !update.hasMessage() &&
                !update.getMessage().hasText();
    }

    private SendMessage processCompleteItemTestAnswer(Update update) {
        var telegramUserId = telegramUserId(update);
        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);
        var currentTest = currentEmployee.getCurrentTest();

        // очистить список, удалить из основной мапы блюдо по имени, изменить текущее блюдо
        currentTest.completeItemTest();

        // выяснить, закончен ли тест - если да, то вывести результат, если нет, то послать сообщение со следующим блюдом
        if (currentTest.getTest().isEmpty()) {
            var testResultMessage = currentTest.testResultMessage();

            currentEmployee.setCurrentView(EmployeeView.TEST_RESULT_VIEW);
            currentEmployee.setCurrentTest(null);
            employeeService.merge(currentEmployee);

            // TODO: сохранить результат тестирования!!!

            return telegramView.testResultView(update, testResultMessage);
        } else {
            employeeService.merge(currentEmployee);

            return telegramView.itemTestMessage(update,currentTest, true);
        }
    }


    // TODO: возможно, придется изменить currentTest.validateTestAnswer() - на тип void
    private SendMessage processIngredientAnswer(Update update) {
        var telegramUserId = telegramUserId(update);
        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);
        var currentTest = currentEmployee.getCurrentTest();

        var testIngredientVariant = update.getMessage().getText();
        var correct = currentTest.validateTestAnswer(testIngredientVariant);

        employeeService.merge(currentEmployee);

        return telegramView.itemTestMessage(update, currentTest, false);
    }
}
