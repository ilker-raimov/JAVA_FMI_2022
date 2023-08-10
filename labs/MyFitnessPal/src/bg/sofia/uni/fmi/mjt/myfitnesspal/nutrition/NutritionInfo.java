package bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition;

public record NutritionInfo(double carbohydrates, double fats, double proteins) {
    static final double MAX_SUM = 100.0;

    public NutritionInfo {
        if (carbohydrates < 0 || fats < 0 || proteins < 0) {
            throw new IllegalArgumentException("Any nutrient in the nutrition info must be non-negative");
        }

        if (carbohydrates + fats + proteins != MAX_SUM) {
            throw new IllegalArgumentException("The sum of all nutrients must be 100");
        }
    }

    public double calories() {
        return proteins * MacroNutrient.PROTEIN.calories +
                fats * MacroNutrient.FAT.calories +
                carbohydrates * MacroNutrient.CARBOHYDRATE.calories;
    }

}
