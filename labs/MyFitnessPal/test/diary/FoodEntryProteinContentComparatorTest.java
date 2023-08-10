package diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntryProteinContentComparator;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoodEntryProteinContentComparatorTest {

    @Test
    void testCompareFoodEntriesByProteinContent() {
        String food1 = "food1";
        String food2 = "food2";
        double servingSize1 = 1.0;
        double servingSize2 = 2.0;

        NutritionInfo nutritionInfo1 = new NutritionInfo(10, 10, 80);
        NutritionInfo nutritionInfo2 = new NutritionInfo(10, 15, 75);

        FoodEntry foodEntry1 = new FoodEntry(food1, servingSize1, nutritionInfo1); // 1 > 2
        FoodEntry foodEntry2 = new FoodEntry(food2, servingSize1, nutritionInfo2);
        FoodEntry foodEntry3 = new FoodEntry(food2, servingSize2, nutritionInfo2); //1 < 3

        FoodEntryProteinContentComparator proteinContentComparator = new FoodEntryProteinContentComparator();

        assertEquals(1, proteinContentComparator.compare(foodEntry1, foodEntry2),
                "The comparator function should compare the food entries properly");
        assertEquals(-1, proteinContentComparator.compare(foodEntry1, foodEntry3),
                "The comparator function should compare the food entries properly");
    }
}
