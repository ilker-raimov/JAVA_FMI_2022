package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;

import java.util.Collection;
import java.util.List;

public interface FoodDiary {
    FoodEntry addFood(Meal meal, String foodName, double servingSize) throws UnknownFoodException;

    Collection<FoodEntry> getAllFoodEntries();

    List<FoodEntry> getAllFoodEntriesByProteinContent();

    double getDailyCaloriesIntake();

    double getDailyCaloriesIntakePerMeal(Meal meal);
}
