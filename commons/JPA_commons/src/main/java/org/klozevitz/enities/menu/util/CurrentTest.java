package org.klozevitz.enities.menu.util;

import lombok.*;
import lombok.extern.log4j.Log4j;
import org.klozevitz.enities.menu.Category;
import org.klozevitz.enities.menu.Ingredient;
import org.klozevitz.enities.menu.Item;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentTest {
    private Long categoryId;
    private String name;
    private Map<String, Map<String, Double>> test;
    private String currentItem;
    private List<String> currentItemAnswers;
    private List<Boolean> rate;

    public CurrentTest(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();
        this.test = testFromCategory(category);
        this.rate = new ArrayList<>();
        this.currentItemAnswers = new ArrayList<>();
        this.currentItem = category
                .getMenu()
                .stream()
                .findFirst()
                .get()
                .getName();
    }


    public String testResultMessage() {
        var answersAmount = rate.size();
        var correctAnswersAmount =
                rate
                        .stream()
                        .filter(result -> result)
                        .count();
        return String.format(
                "Ваш результат:\n" +
                        "всего вопросов: %s\n" +
                        "верных ответов: %s\n",
                answersAmount,
                correctAnswersAmount
        );
    }


    public void completeItemTest() {
        var currentItemIngredients = test.get(currentItem);

        if (currentItemIngredients.size() == 0) {
            rate.add(true);
        } else {
            rate.add(false);
        }

        test.remove(currentItem);

        if (test.size() == 0) {
            return;
        }

        currentItem = test.keySet().stream().findFirst().get();
        currentItemAnswers.clear();
    }

    public String currentItemAnswers() {
        final var sb = new StringBuilder();

        this.currentItemAnswers.forEach(answer ->
                sb
                        .append("<b>")
                        .append(answer)
                        .append("</b>")
                        .append("\n")
        );
        sb.append("\n");

        return sb.toString();
    }

    public boolean validateTestAnswer(String testAnswer) {
        currentItemAnswers.add(testAnswer);

        var testAnswerParts = testAnswer.split(" - ");

        if (testAnswerParts.length < 2) {
            return false;
        }

        var testAnswerIngredientName = testAnswerParts[0].trim();
        double testAnswerIngredientWeight;
        try {
            testAnswerIngredientWeight = Double.parseDouble(testAnswerParts[1].trim());
        } catch (NumberFormatException e) {
            log.info("введен неверный вес ингредиента");
            return false;
        }

        var currentItemIngredients = test.get(currentItem);
        var currentItemIngredientNames = currentItemIngredients.keySet();
        var optionalCorrectIngredientName = currentItemIngredientNames
                .stream()
                .filter(ingredient -> isTestIngredientNameValid(ingredient, testAnswerIngredientName))
                .findFirst();

        if (optionalCorrectIngredientName.isEmpty()) {
            currentItemIngredients.put(testAnswerParts[0], testAnswerIngredientWeight);
            return false;
        }

        var correctIngredientName = optionalCorrectIngredientName.get();
        var correctWeight = currentItemIngredients.get(correctIngredientName);
        var threshold = Math.abs(testAnswerIngredientWeight - correctWeight) < 0.001;

        if (!threshold) {
            return false;
        }

        currentItemIngredients.remove(correctIngredientName);
        return true;
    }

    private boolean isTestIngredientNameValid(String original, String testAnswer) {
//        if (original == null) {
//            original = "";
//        }
//        if (testAnswer == null) {
//            testAnswer = "";
//        }

        var originalSorted = sort(original);
        var testAnswerSorted = sort(testAnswer);

        int distance = levenshteinDistance(originalSorted, testAnswerSorted);
        return distance < 3;
    }

    /**
     * Перед проверкой на орфографические ошибки надо нормализовать строки -
     * сортируем в алфавитном порядке и меняем буквы 'е' на 'ё' и 'й' на 'и'
     */
    private String sort(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        return Arrays.stream(input.toLowerCase().split("\\s+"))
                .map(word ->
                        word
                                .replaceAll("[^a-zа-яё0-9]", "")
                                .replace('ё', 'е')
                                .replace('й', 'и'))
                .filter(word -> !word.isEmpty())
                .sorted()
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private int levenshteinDistance(String original, String testAnswer) {
        int originalLength = original.length();
        int answerLength = testAnswer.length();

        int[][] dp = new int[originalLength + 1][answerLength + 1];

        for (int i = 0; i <= originalLength; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= answerLength; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= originalLength; i++) {
            for (int j = 1; j <= answerLength; j++) {
                if (original.charAt(i - 1) == testAnswer.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int delete = dp[i - 1][j] + 1;
                    int insert = dp[i][j - 1] + 1;
                    int replace = dp[i - 1][j - 1] + 1;
                    dp[i][j] = Math.min(Math.min(delete, insert), replace);
                }

                if (dp[i][j] >= 3 && i == originalLength && j == answerLength) {
                    return 3;
                }
            }
        }

        return dp[originalLength][answerLength];
    }

    private Map<String, Map<String, Double>> testFromCategory(Category category) {
        return category.getMenu().stream()
                .collect(Collectors.toMap(
                        Item::getName,
                        item -> item.getIngredients().stream()
                                .collect(Collectors.toMap(
                                        Ingredient::getName,
                                        Ingredient::getWeight
                                ))
                ));
    }
}
