package org.klozevitz.enities.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.klozevitz.enities.BaseEntity;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_question")
public class TestQuestion extends BaseEntity {
    private String name;
    private String variant1;
    private String variant2;
    private String variant3;
    private String variant4;
    @OneToOne
    @JoinColumn(name = "item", referencedColumnName = "id")
    @JsonIgnore
    private Item item;

    public TestQuestion(Item item) {
        Map<String, Double> ingredients = item
                .getIngredients()
                .stream()
                .collect(Collectors
                        .toMap(
                                Ingredient::getName,
                                Ingredient::getWeight
                        )
                );
        this.name = item.getName();
        this.variant1 = variant(ingredients);
        this.variant2 = wrongVariant(ingredients);
        this.variant3 = wrongVariant(ingredients);
        this.variant4 = wrongVariant(ingredients);
        this.item = item;
    }

    private String variant(Map<String, Double> ingredients) {
        StringBuilder sb = new StringBuilder();
        ingredients.keySet().forEach(key -> sb
                .append(key)
                .append("  -  ")
                .append(ingredients.get(key).intValue())
                .append("\n"));

        return sb.toString();
    }

    private String wrongVariant(Map<String, Double> ingredients) {
        Map<String, Double> wrongIngredients;

        do {
            wrongIngredients = wrongMap(ingredients);
        } while (compareIngredients(ingredients, wrongIngredients));

        return variant(wrongIngredients);
    }

    private boolean compareIngredients(Map<String, Double> original, Map<String, Double> wrong) {
        for (Map.Entry<String, Double> entry : original.entrySet()) {
            String key = entry.getKey();
            Double originalValue = entry.getValue();
            Double wrongValue = wrong.get(key);

            if (!originalValue.equals(wrongValue)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Double> wrongMap(Map<String, Double> originalIngredients) {
        Map<String, Double> modifiedIngredients = new HashMap<>(originalIngredients);
        List<String> ingredientNames = new ArrayList<>(modifiedIngredients.keySet());

        if (ingredientNames.isEmpty()) {
            return modifiedIngredients;
        }

        int maxChanges = Math.max(1, (int) Math.ceil(ingredientNames.size() * 0.5));
        int ingredientsToChange = ThreadLocalRandom.current().nextInt(1, maxChanges + 1);
        Collections.shuffle(ingredientNames);

        for (int i = 0; i < ingredientsToChange && i < ingredientNames.size(); i++) {
            String ingredientName = ingredientNames.get(i);
            Double originalWeight = modifiedIngredients.get(ingredientName);

            double changePercent = ThreadLocalRandom.current().nextDouble(0.01, 0.21);
            boolean increase = ThreadLocalRandom.current().nextBoolean();
            double changedWeight = increase ?
                    originalWeight * (1 + changePercent) :
                    originalWeight * (1 - changePercent);

            double roundedWeight = changedWeight > 10 ?
                    Math.round(changedWeight / 5) * 5 :
                    Math.round(changedWeight);

            modifiedIngredients.put(ingredientName, roundedWeight);
        }

        return modifiedIngredients;
    }
}
