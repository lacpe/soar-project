package ch.unil.doplab.recipe.domain;

import java.util.List;

public class Meal {
    private int id;
    private String title;
    private String imageUrl;
    private NutritionalInfo nutritionalInfo;
    private List<Ingredient> ingredients;
    private List<String> instructions;      // List of step-by-step cooking instructions

    // Constructor to initialize basic meal details
    public Meal(int id, String title, String imageUrl, NutritionalInfo nutritionalInfo) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.nutritionalInfo = nutritionalInfo;
    }

    // Additional setters for adding ingredients and instructions after the object is created
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public void setNutritionalInfo(NutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    // Getters for retrieving meal information
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public NutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

     public List<String> getInstructions() {
        return instructions;
    }

    public void displayMealIngredients() {
        System.out.println("Ingredients for " + title + ":");
        if (ingredients != null && !ingredients.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
                System.out.println(" - " + ingredient.getQuantity() + " " + ingredient.getUnit() + " of " + ingredient.getName());
            }
        } else {
            System.out.println("No ingredients available for this meal.");
        }
        System.out.println();
    }


    // Additional method to display a summary of the meal for debugging or presentation
    public void displayMealInfo() {
        System.out.println("Meal: " + title);
        if (nutritionalInfo != null) {
            nutritionalInfo.displayNutritionalInfo();
        }
        System.out.println("Image: " + imageUrl);  // Only if you want to display the meal image
        System.out.println();
    }

    // Method to display instructions, useful for testing or debugging
    public void displayInstructions() {
        System.out.println("Instructions for " + title + ":");
        if (instructions != null && !instructions.isEmpty()) {
            for (int i = 0; i < instructions.size(); i++) {
                System.out.println((i + 1) + ". " + instructions.get(i));
            }
        } else {
            System.out.println("No instructions available for this meal.");
        }
    }
}
