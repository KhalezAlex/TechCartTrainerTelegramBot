//package org.klozevitz.services.views;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j;
//import org.klozevitz.annotations.TelegramView;
//import org.klozevitz.enities.menu.util.CurrentTest;
//import org.klozevitz.enities.users.enums.views.EmployeeView;
//import org.klozevitz.entityService.menu.TestQuestionService;
//import org.klozevitz.entityService.users.EmployeeService;
//import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
//import org.klozevitz.util.EmployeeTelegramView;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Log4j
//@TelegramView(role = "employee", name = EmployeeView.CATEGORY_TEST_CHOICE_VIEW)
//@RequiredArgsConstructor
//public class CategoryTestChoiceView_LEGACY extends AbstractTelegramView {
//    private final EmployeeService employeeService;
//    private final TestQuestionService testQuestionService;
//    private final EmployeeTelegramView telegramView;
//
//    @Override
//    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
//        if (!update.hasCallbackQuery()) {
//            return null;
//        }
//
//        var command = command(update);
//        if (command.equals("/start")) {
//            return welcomeView(update);
//        }
//
//        if (command.startsWith("/category_test_")) {
//            return categoryTestView(update);
//        }
//
//        return null;
//    }
//
//    private ArrayList<BotApiMethod<?>> categoryTestView(Update update) {
//        var telegramUserId = telegramUserId(update);
//        var command = command(update);
//        var currentEmployee = employeeService.findByTelegramUserId(telegramUserId);
//        var categoryId = categoryIdFromUpdate(command);
//        var test = testQuestionService.findByCategoryId(categoryId);
//
//        currentEmployee.setCurrentTest(new CurrentTest(categoryId, test));
//
//        var answer = telegramView.categoryTestView(
//                update,
//                test.get(currentEmployee.getCurrentTest().getCurrentQuestionNumber())
//        );
//
//        var correctOptionId = correctOptionId(answer);
//        currentEmployee.getCurrentTest().setPreviousCorrectAnswerIndex(correctOptionId);
//        currentEmployee.setCurrentView(EmployeeView.TEST_VIEW);
//        employeeService.merge(currentEmployee);
//
//        return answer;
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
//
//    private ArrayList<BotApiMethod<?>> welcomeView(Update update) {
//        var telegramUserId = telegramUserId(update);
//        var answer = telegramView.welcomeView(update);
//
//        employeeService.setCurrentView(telegramUserId, EmployeeView.WELCOME_VIEW);
//
//        return answerAsList(answer);
//    }
//
//    private long categoryIdFromUpdate(String data) {
//        var splitCommand = List.of(data.split("_"));
//        var categoryIdInStringFromUpdate = splitCommand.get(splitCommand.size() - 1);
//
//        try {
//            return Long.parseLong(categoryIdInStringFromUpdate);
//        } catch (Exception e) {
//            return -1L;
//        }
//    }
//}
