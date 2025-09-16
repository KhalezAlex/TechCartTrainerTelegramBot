package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.menu.Item;
import org.klozevitz.enities.users.enums.views.EmployeeView;
import org.klozevitz.entityService.menu.ItemService;
import org.klozevitz.entityService.users.EmployeeService;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.EmployeeTelegramView;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j
@TelegramView(role = "employee", name = EmployeeView.CATEGORY_INFO_CHOICE_VIEW)
@RequiredArgsConstructor
public class CategoryInfoChoiceView extends AbstractTelegramView {
    private final EmployeeService employeeService;
    private final ItemService itemService;
    private final EmployeeTelegramView telegramView;

    @Override
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasCallbackQuery()) {
            return null;
        }

        var command = command(update);
        if (command.startsWith("/category_info_")) {
            return categoryInfoView(update);
        }

        if (command.equals("/start")) {
            return welcomeView(update);
        }

        return null;
    }

    private ArrayList<BotApiMethod<?>> welcomeView(Update update) {
        var telegramUserId = telegramUserId(update);
        var answer = telegramView.welcomeView(update);

        employeeService.setCurrentView(telegramUserId, EmployeeView.WELCOME_VIEW);

        return answerAsList(answer);
    }

    private ArrayList<BotApiMethod<?>> categoryInfoView(Update update) {
        String recipe;
        ArrayList<BotApiMethod<?>> answer = new ArrayList<>();
        var telegramUserId = telegramUserId(update);
        var data = update.getCallbackQuery().getData();
        var categoryId = categoryIdFromUpdate(data);

        if (categoryId == -1) {
            return null;
        }

        var items = itemService.findByCategoryId(categoryId);

        while (items.size() > 0) {
            var isLastMessage = items.size() == 1;
            recipe = recipe(items.get(0));
            var message = telegramView.categoryInfoView(update, recipe, isLastMessage);
            items.remove(0);
            answer.add(message);
        }

        answer.add(SendMessage.builder() //пустое сообщение - такое же, как в answerAsList
                .text("")
                .chatId(telegramUserId)
                .build());

        employeeService.setCurrentView(telegramUserId, EmployeeView.CATEGORY_INFO_VIEW);

        return answer;
    }

    private String recipe(Item item) {
        AtomicInteger atomicIndex = new AtomicInteger(1);
        StringBuilder sb = new StringBuilder();

        sb
                .append("<b>")
                .append(item.getName())
                .append("</b>");

        item.getIngredients().forEach(
                ingredient -> sb
                        .append("\n  ")
                        .append(atomicIndex.getAndIncrement())
                        .append(".   ")
                        .append(ingredient.getName())
                        .append(" - ")
                        .append(ingredient.getWeight().intValue())
                        .append(ingredient.getUnits()));

        return sb.toString();
    }

    private long categoryIdFromUpdate(String data) {
        var splitCommand = List.of(data.split("_"));
        var categoryIdInStringFromUpdate = splitCommand.get(splitCommand.size() - 1);

        try {
            return Long.parseLong(categoryIdInStringFromUpdate);
        } catch (Exception e) {
            return -1L;
        }
    }
}
