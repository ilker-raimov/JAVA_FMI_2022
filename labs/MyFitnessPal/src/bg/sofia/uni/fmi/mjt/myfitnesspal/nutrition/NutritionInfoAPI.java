package bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;

public interface NutritionInfoAPI {
    NutritionInfo getNutritionInfo(String foodName) throws UnknownFoodException;
}
