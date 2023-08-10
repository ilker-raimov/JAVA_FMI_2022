package nutrition;

import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NutritionInfoTest {

    @Test
    void testNutritionConstructorIllegalArguments() {
        double carbohydrates = 10;
        double fats = 10;
        double proteins = 70;

        double negativeCarbohydrates = -10;
        double negativeFats = -10;
        double negativeProteins = -10;

        assertThrows(IllegalArgumentException.class,
                () -> new NutritionInfo(negativeCarbohydrates, negativeFats, negativeProteins));
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionInfo(carbohydrates, fats, proteins));
    }

    @Test
    void testNutritionConstructorLegalArguments() {
        double carbohydrates = 10;
        double fats = 10;
        double proteins = 80;

        NutritionInfo nutritionInfo = new NutritionInfo(carbohydrates, fats, proteins);

        assertEquals(450.0, nutritionInfo.calories(), 0.001,
                "The calories must be calculated properly");
    }
}
