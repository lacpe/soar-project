package ch.unil.doplab.recipe.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Embeddable
public class NutritionalInfo {
    private int calories, protein, fat, carbs;
    private double saturatedFat, fiber, sugar, sodium, vitaminC, calcium, iron, potassium, vitaminA, vitaminK, magnesium;

    public NutritionalInfo() {
        this(0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

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

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public double getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(double saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminK() {
        return vitaminK;
    }

    public void setVitaminK(double vitaminK) {
        this.vitaminK = vitaminK;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
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
