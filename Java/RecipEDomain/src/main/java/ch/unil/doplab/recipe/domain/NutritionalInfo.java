package ch.unil.doplab.recipe.domain;

public class NutritionalInfo {
    private int calories, protein, fat, carbs;
    private double saturatedFat, fiber, sugar, sodium, vitaminC, calcium, iron, potassium, vitaminA, vitaminK, magnesium;

    public NutritionalInfo(int calories, int protein, int fat, int carbs, double saturatedFat, double fiber, double sugar, double sodium, double vitaminC, double calcium, double iron, double potassium, double vitaminA, double vitaminK, double magnesium) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.saturatedFat = saturatedFat;
        this.fiber = fiber;
        this.sugar = sugar;
        this.sodium = sodium;
        this.vitaminC = vitaminC;
        this.calcium = calcium;
        this.iron = iron;
        this.potassium = potassium;
        this.vitaminA = vitaminA;
        this.vitaminK = vitaminK;
        this.magnesium = magnesium;
    }

    // Getters
    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    public int getCarbs() {
        return carbs;
    }

    // Method to display nutritional information for debugging or presentation
    public void displayNutritionalInfo() {
        System.out.println("Nutritional values per serving");
        System.out.println("Calories: " + calories + " kcal");
        System.out.println("Protein: " + protein + " g");
        System.out.println("Fat: " + fat + " g");
        System.out.println("Saturated Fat: " + saturatedFat + " g");
        System.out.println("Carbohydrates: " + carbs + " g");
        System.out.println("Fiber: " + fiber + " g");
        System.out.println("Sugar: " + sugar + " g");
        System.out.println("Sodium: " + sodium + " mg");
        System.out.println("Vitamin C: " + vitaminC + " mg");
        System.out.println("Calcium: " + calcium + " mg");
        System.out.println("Iron: " + iron + " mg");
        System.out.println("Potassium: " + potassium + " mg");
        System.out.println("Vitamin A: " + vitaminA + " IU");
        System.out.println("Vitamin K: " + vitaminK + " µg");
        System.out.println("Magnesium: " + magnesium + " mg");
    }
}
