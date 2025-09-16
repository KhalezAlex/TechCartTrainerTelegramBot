package org.klozevitz.util;

import lombok.RequiredArgsConstructor;
import org.klozevitz.MessageUtil;
import org.klozevitz.enities.menu.util.CurrentTest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeTelegramView {
    private final MessageUtil messageUtil;


    /**
     * Первое приветственное сообщение
     * EmployeeView.WELCOME_VIEW
     */
    public SendMessage welcomeView(Update update) {
        var message = "Вы находитесь на главной странице чат-бота.\n" +
                "Вам доступны следующие действия:";
        var answer = messageUtil.blankAnswer(update);
        var welcomeViewKeyboardMarkup = welcomeViewKeyboardMarkup();

        answer.setText(message);
        answer.setReplyMarkup(welcomeViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup welcomeViewKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var keyboardRows = List.of(
                List.of(
                        button("ИЗУЧЕНИЕ МАТЕРИАЛА", "/category_info_choice_view"),
                        button("ТЕСТИРОВАНИЕ", "/category_test_choice_view")
                )
        );

        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     * Вью выбора ресурса для изучения
     * EmployeeView.CATEGORY_INFO_CHOICE_VIEW
     */
    public SendMessage categoryInfoChoiceView(long telegramUserId, Map<Long, String> resources) {
        var message = "Меню выбора категории для изучения";
        var answer = messageUtil.blankAnswer(telegramUserId);
        var categoryChoiceViewKeyboardMarkup = resourcesChoiceViewKeyboardMarkup(resources);

        answer.setText(message);
        answer.setReplyMarkup(categoryChoiceViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup resourcesChoiceViewKeyboardMarkup(Map<Long, String> resources) {
        var keyboard = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        resources.forEach((key, value) -> keyboardRows.add(List.of(
                button(value, String.format("/category_info_%d", key))
        )));
        keyboardRows.add(homepageKeyboardRow());
        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     * Вью просмотра информации по блюдам из категории
     * EmployeeView.CATEGORY_INFO_VIEW
     * отсылается список сообщений и только к последнему прикручиваются управляющие кнопки
     */
    public SendMessage categoryInfoView(Update update, String message, boolean isLastMessage) {
        var answer = messageUtil.blankAnswer(update);

        answer.setText(message);
        answer.enableHtml(true);

        if (isLastMessage) {
            var categoryInfoViewKeyboardMarkup = categoryInfoViewKeyboardMarkup();
            answer.setReplyMarkup(categoryInfoViewKeyboardMarkup);
        }

        return answer;
    }

    private InlineKeyboardMarkup categoryInfoViewKeyboardMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        var keyboardRows = new ArrayList<List<InlineKeyboardButton>>();

        keyboardRows.add(new ArrayList<>() {{
            add(
                    button(
                            "НАЗАД", "/category_info_choice_view"
                    )
            );
        }});
        keyboardRows.add(homepageKeyboardRow());
        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     * Вью выбора ресурса для тестирования
     * EmployeeView.CATEGORY_TEST_CHOICE_VIEW
     */
    public SendMessage categoryTestChoiceView(long telegramUserId, Map<Long, String> resources) {
        var message = "Меню выбора категории для тестирования";
        var answer = messageUtil.blankAnswer(telegramUserId);
        var categoryTestChoiceViewKeyboardMarkup = categoryTestChoiceViewKeyboardMarkup(resources);

        answer.setText(message);
        answer.setReplyMarkup(categoryTestChoiceViewKeyboardMarkup);

        return answer;
    }

    private InlineKeyboardMarkup categoryTestChoiceViewKeyboardMarkup(Map<Long, String> resources) {
        var keyboard = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        resources.forEach((key, value) -> keyboardRows.add(List.of(
                button(value, String.format("/category_test_%d", key))
        )));
        keyboardRows.add(homepageKeyboardRow());
        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     * Вью вопроса для тестирования
     * EmployeeView.CATEGORY_TEST_VIEW
     */
    public SendMessage itemTestMessage(Update update, CurrentTest currentTest, boolean isFirstMessage) {
        var message = String.format(
                "Текущий тест: <b>%s</b>\nТекущее блюдо: <b>%s</b>\n" +
                        "%s" +
                        "Вам надо вводить по очереди строки формата\n" +
                        "<b>&lt;ингредиент&gt; - &lt;вес&gt;</b>\n" +
                        "Пример строки:\n" +
                        "<b>куриный бульон - 130</b>\n" +
                        "После того, как нужный ингредиент введен, отправьте сообщение.\n" +
                        "По окончании ввода нажмите кнопку <b>\"Закончить\"</b>\n" +
                        "Эта кнопка будет всплывать после каждого введенного ингредиента.",
                currentTest.getName(),
                currentTest.getCurrentItem(),
                isFirstMessage ? "" : "Ваши ответы:\n" + currentTest.currentItemAnswers()
        );
        var answer = textMessage(message, update);
        answer.enableHtml(true);

        if (!isFirstMessage) {
            var keyboardMarkup = completeItemTestButton();
            answer.setReplyMarkup(keyboardMarkup);
        }

        return answer;
    }

    private InlineKeyboardMarkup completeItemTestButton() {
        var keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        keyboardRows.add(List.of(
                button("Завершить", "/completeItemTest")
        ));
        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }


    /**
     *  Вью вывода результатов тестирования
     *  EmployeeView.TEST_RESULT_VIEW
     * */
    public SendMessage testResultView(Update update, String testResultMessage) {
        var answer = textMessage(testResultMessage, update);

        answer.setReplyMarkup(homePageReplyMarkup());

        return answer;
    }


    /**
     * UTILS
     * */

    /**
     * Базовая инлайн-кнопка
     */
    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    /**
     * Базовое текстовое сообщение
     */
    public SendMessage textMessage(String message, Update update) {
        return SendMessage.builder()
                .chatId(chatId(update))
                .text(message)
                .build();
    }

    protected long chatId(Update update) {
        return Optional.ofNullable(update)
                .map(u ->
                        u.hasMessage() ? u.getMessage().getFrom().getId() :
                                u.hasCallbackQuery() ? u.getCallbackQuery().getFrom().getId() :
                                        u.hasPollAnswer() ? u.getPollAnswer().getUser().getId() :
                                                null
                )
                .orElseThrow(() -> new IllegalArgumentException("Update пустой"));
    }

    private List<InlineKeyboardButton> homepageKeyboardRow() {
        return List.of(
                button("Главная страница", "/start")
        );
    }

    private InlineKeyboardMarkup homePageReplyMarkup() {
        var keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        keyboardRows.add(homepageKeyboardRow());
        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }
}
