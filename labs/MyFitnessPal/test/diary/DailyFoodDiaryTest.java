package diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.DailyFoodDiary;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntryProteinContentComparator;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.Meal;
import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DailyFoodDiaryTest {

    @Test
    void testAddFoodNullParameters() {
        NutritionInfo NI = new NutritionInfo(10, 80, 10);
        NutritionInfoAPI mockedNIA = mock(NutritionInfoAPI.class);
        try {
            Mockito.when(mockedNIA.getNutritionInfo(anyString())).thenReturn(NI);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedNIA);

        Meal nullMeal = null;
        Meal meal1 = Meal.BREAKFAST;

        String nullFoodName = null;
        String foodName = "food1";

        double servingSize = 10.0;
        double illegalServingSize = -1.0;

        assertThrows(IllegalArgumentException.class, () -> DFD.addFood(nullMeal, foodName, servingSize),
                "Adding null meal should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> DFD.addFood(meal1, nullFoodName, servingSize),
                "Adding meal with null name should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> DFD.addFood(meal1, foodName, illegalServingSize),
                "Adding meal with negative serving size should throw IllegalArgumentException");
    }

    @Test
    void testAddFoodNotNullParameters() {
        NutritionInfo NI = new NutritionInfo(10, 80, 10);
        NutritionInfoAPI mockedNIA = mock(NutritionInfoAPI.class);

        try {
            Mockito.when(mockedNIA.getNutritionInfo(anyString())).thenReturn(NI);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedNIA);

        Meal meal1 = Meal.BREAKFAST;
        String foodName = "food1";
        double servingSize = 10.0;

        try {
            Assertions.assertNotNull(DFD.addFood(meal1, foodName, servingSize));
        } catch (UnknownFoodException e) {

        }
    }

    @Test
    void testGetAllFoodEntries() {
        NutritionInfo NI = new NutritionInfo(10, 80, 10);
        NutritionInfoAPI mockedNIA = mock(NutritionInfoAPI.class);

        try {
            Mockito.when(mockedNIA.getNutritionInfo(anyString())).thenReturn(NI);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedNIA);

        Meal meal1 = Meal.BREAKFAST;
        Meal meal2 = Meal.LUNCH;
        String foodName1 = "food1";
        String foodName2 = "food2";
        double servingSize1 = 10.0;
        double servingSize2 = 15.0;

        try {
            List<FoodEntry> resultCheckList = new ArrayList<>();

            resultCheckList.add(DFD.addFood(meal1, foodName1, servingSize1));
            resultCheckList.add(DFD.addFood(meal2, foodName2, servingSize2));

            assertIterableEquals(DFD.getAllFoodEntries(), resultCheckList);
        } catch (UnknownFoodException e) {

        }
    }

    @Test
    void testGetAllFoodEntriesByProteinContent() {
        Meal meal1 = Meal.BREAKFAST;
        Meal meal2 = Meal.LUNCH;
        Meal meal3 = Meal.DINNER;

        String food1 = "food1";
        String food2 = "food2";
        String food3 = "food3";

        double servingSize1 = 10.0;
        double servingSize2 = 11.0;
        double servingSize3 = 12.0;

        FoodEntryProteinContentComparator proteinContentComparator = new FoodEntryProteinContentComparator();
        NutritionInfo NI1 = new NutritionInfo(10, 30, 60);
        NutritionInfoAPI mockedAPI = mock(NutritionInfoAPI.class);

        try {
            when(mockedAPI.getNutritionInfo(anyString())).thenReturn(NI1);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedAPI);

        try {
            DFD.addFood(meal1, food1, servingSize1);
            DFD.addFood(meal2, food2, servingSize2);
            DFD.addFood(meal3, food3, servingSize3);
        } catch (UnknownFoodException e) {

        }

        assertThrows(UnsupportedOperationException.class, () -> DFD.getAllFoodEntriesByProteinContent().add(null));

        List<FoodEntry> foodEntryListResult = DFD.getAllFoodEntriesByProteinContent();
        List<FoodEntry> foodEntryListExpected = new ArrayList<>();
        foodEntryListExpected.add(new FoodEntry(food1, servingSize1, NI1));
        foodEntryListExpected.add(new FoodEntry(food2, servingSize2, NI1));
        foodEntryListExpected.add(new FoodEntry(food3, servingSize3, NI1));
        foodEntryListExpected.sort(proteinContentComparator);

        assertIterableEquals(foodEntryListResult, foodEntryListExpected,
                "The function sorting by protein content should work properly");
    }

    @Test
    void testGetDailyCaloriesIntakeNoMeal() {
        NutritionInfo NI1 = new NutritionInfo(10, 30, 60);
        NutritionInfoAPI mockedAPI = mock(NutritionInfoAPI.class);

        try {
            when(mockedAPI.getNutritionInfo(anyString())).thenReturn(NI1);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedAPI);

        assertEquals(0.0, DFD.getDailyCaloriesIntake(), 0.001,
                "Daily calories intake should be calculated properly");
    }

    @Test
    void testGetDailyCaloriesIntakeFewMeals() {
        Meal meal1 = Meal.BREAKFAST;
        Meal meal2 = Meal.LUNCH;
        Meal meal3 = Meal.DINNER;

        String food1 = "food1";
        String food2 = "food2";
        String food3 = "food3";

        double servingSize1 = 1.0;
        double servingSize2 = 2.0;
        double servingSize3 = 3.0;

        FoodEntryProteinContentComparator proteinContentComparator = new FoodEntryProteinContentComparator();
        NutritionInfo NI1 = new NutritionInfo(10, 30, 60);
        NutritionInfoAPI mockedAPI = mock(NutritionInfoAPI.class);

        try {
            when(mockedAPI.getNutritionInfo(anyString())).thenReturn(NI1);
        } catch (UnknownFoodException e) {

        }

        DailyFoodDiary DFD = new DailyFoodDiary(mockedAPI);

        try {
            DFD.addFood(meal1, food1, servingSize1);
            DFD.addFood(meal2, food2, servingSize2);
            DFD.addFood(meal3, food3, servingSize3);
        } catch (UnknownFoodException e) {

        }

        assertEquals(3300.0, DFD.getDailyCaloriesIntake(), 0.001,
                "Daily calories intake should be calculated properly");
    }

}
