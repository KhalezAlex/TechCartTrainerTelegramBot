//package org.klozevitz.services.views;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j;
//import org.klozevitz.annotations.TelegramView;
//import org.klozevitz.enities.users.enums.views.EmployeeView;
//import org.klozevitz.entityService.users.EmployeeService;
//import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
//import org.klozevitz.util.EmployeeTelegramView;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Log4j
//@TelegramView(role = "employee", name = EmployeeView.CATEGORY_TEST_VIEW)
//@RequiredArgsConstructor
//public class CategoryTestView_LEGACY extends AbstractTelegramView {
//    public final EmployeeService employeeService;
//    public final EmployeeTelegramView telegramView;
//
//
//    @Override
//    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
//        if (!update.hasPollAnswer()) {
//            return null;
//        }
//
//        var telegramUserId = telegramUserId(update);
//        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);
//        var currentTest = currentEmployee.getCurrentTest();
//
//        if (currentTest.getCurrentQuestionNumber() + 1 != currentTest.getMenuTest().size()) {
//            return nextCategoryTestView(update);
//        } else {
//            var answer = testResultView(update);
//            return answerAsList(answer);
//        }
//    }
//
//    private ArrayList<BotApiMethod<?>> nextCategoryTestView(Update update) {
//        var telegramUserId = telegramUserId(update);
//        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);
//        var currentTest = currentEmployee.getCurrentTest();
//        var chosenOption = update
//                .getPollAnswer()
//                .getOptionIds()
//                .get(0);
//        var previousAnswerCorrect = chosenOption.equals(currentTest.getPreviousCorrectAnswerIndex());
//        currentTest.newQuestion(previousAnswerCorrect);
//
//        employeeService.merge(currentEmployee);
//
//        var testQuestion = currentTest.getMenuTest().get(currentTest.getCurrentQuestionNumber());
//        var answer = telegramView.categoryTestView(update, testQuestion);
//
//        var correctOptionId = correctOptionId(answer);
//        currentEmployee.getCurrentTest().setPreviousCorrectAnswerIndex(correctOptionId);
//        employeeService.merge(currentEmployee);
//
//        return answer;
//    }
//
//    private SendMessage testResultView(Update update) {
//        return null;
//    }
//
//    private Integer correctOptionId(List<BotApiMethod<?>> answer) {
//        AtomicInteger result = new AtomicInteger();
//        answer.forEach(message -> {
//            if (message instanceof SendPoll) {
//                result.set(((SendPoll) message).getCorrectOptionId());
//            }
//        });
//        return result.get();
//    }
//}
