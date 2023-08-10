package diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FoodEntryTest {

    @Test
    void testFoodEntryNullParameters() {
        String food = "food1";
        double servingSize = 10.0;
        NutritionInfo NI = new NutritionInfo(20, 30, 50);
        double negativeServingSize = -1.0;

        assertThrows(IllegalArgumentException.class, () -> new FoodEntry(null, servingSize, NI),
                "Food name can't be null");
        assertThrows(IllegalArgumentException.class, () -> new FoodEntry(food, negativeServingSize, NI),
                "Serving size must be a non-negative number");
        assertThrows(IllegalArgumentException.class, () -> new FoodEntry(food, servingSize, null),
                "Nutrition info can't be null");
    }

    @Test
    void testFoodEntryNotNullParameters() {
        String food = "food1";
        double servingSize = 10.0;
        NutritionInfo NI = new NutritionInfo(20, 30, 50);
        FoodEntry foodEntry = new FoodEntry(food, servingSize, NI);

        assertEquals(food, foodEntry.food(), "Retrieving food from getter should return data correctly");
        assertEquals(servingSize, foodEntry.servingSize(),
                "Retrieving serving size from getter should return data correctly");
        assertEquals(NI, foodEntry.nutritionInfo(),
                "Retrieving nutrition info from getter should return data correctly");
    }
}
