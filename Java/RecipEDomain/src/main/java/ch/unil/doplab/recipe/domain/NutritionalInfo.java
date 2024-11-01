package ch.unil.doplab.recipe.domain;

public class NutritionalInfo {
    private int calories;
    private int protein;
    private int fat;
    private int carbs;

    public NutritionalInfo(int calories, int protein, int fat, int carbs) {
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
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
        System.out.println("Calories: " + calories);
        System.out.println("Protein: " + protein + "g");
        System.out.println("Fat: " + fat + "g");
        System.out.println("Carbs: " + carbs + "g");
    }
}