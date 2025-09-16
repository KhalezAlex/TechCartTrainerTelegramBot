package org.klozevitz.services.util;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.klozevitz.enities.menu.Category;
import org.klozevitz.enities.menu.Ingredient;
import org.klozevitz.enities.menu.Item;
import org.klozevitz.enities.menu.TestQuestion;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Log4j
@Service
@NoArgsConstructor
public class ExcelToTestParser {

    public Set<Category> parseFile(Workbook workbook) {
        Set<Category> menu = new HashSet<>();

        var index = 0;
        var numberOfSheets = workbook.getNumberOfSheets();
        while (index < numberOfSheets) {
            var sheet = workbook.getSheetAt(index);
            var categoryItems = parseCategoryItems(sheet);
            var categoryName = workbook.getSheetName(index);
            var category = category(categoryItems, categoryName);
            menu.add(category);
            index++;
        }

        return menu;
    }


    private Category category(Set<Item> menu, String categoryName) {
        var category = Category.builder()
                .name(categoryName)
                .menu(menu)
                .build();

        category.getMenu().forEach(item -> item.setCategory(category));

        return category;
    }

    public Set<Item> parseCategoryItems(Sheet sheet) {
        try {
            final Iterator<Row> wbIterator = sheet.iterator();
            final Set<Item> menu = new HashSet<>();

            wbIterator.next();
            while (wbIterator.hasNext()) {
                var currentItem = parseItem(wbIterator);
                menu.add(currentItem);
            }

            return menu;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Item parseItem(Iterator<Row> wbIterator) {
        var currentRow = wbIterator.next();
        var currentItem = itemFromRow(currentRow);
        while ((currentRow = wbIterator.next()) != null && !endOfItemBlock(currentRow)) {
            var ingredient = parseIngredient(currentRow);
            ingredient.setItem(currentItem);
            currentItem.getIngredients().add(ingredient);
        }
        var testQuestion = new TestQuestion(currentItem);
        currentItem.setTestQuestion(testQuestion);
        return currentItem;
    }

    private boolean endOfItemBlock(Row row) {
        var cellType = row.getCell(0).getCellType();

        return cellType == Cell.CELL_TYPE_STRING && row.getCell(0).getStringCellValue().equals("-");
    }

    private Ingredient parseIngredient(Row currentRow) {
        return Ingredient.builder()
                .name(currentRow.getCell(1).getStringCellValue())
                .weight(currentRow.getCell(2).getNumericCellValue())
                .units(currentRow.getCell(3).getStringCellValue())
                .build();
    }

    private Item itemFromRow(Row currentRow) {
        return Item.builder()
                .name(currentRow.getCell(0).getStringCellValue())
                .weight(currentRow.getCell(2).getNumericCellValue())
                .units(currentRow.getCell(3).getStringCellValue())
                .ingredients(new HashSet<>())
                .build();
    }
}
