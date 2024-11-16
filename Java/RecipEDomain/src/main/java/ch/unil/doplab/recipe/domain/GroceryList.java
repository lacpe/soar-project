package ch.unil.doplab.recipe.domain;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class GroceryList {
    private UUID groceryListId;
    private Map<String, List<Ingredient>> ingredientsByAisle; // Store ingredients by aisle

    public GroceryList() {
        this(null, new HashMap<>());
    }

    public GroceryList(Map<String, List<Ingredient>> ingredientsByAisle) {
        this(null, ingredientsByAisle);
    }

    public GroceryList(UUID groceryListId, Map<String, List<Ingredient>> ingredientsByAisle) {
        this.groceryListId = groceryListId;
        this.ingredientsByAisle = ingredientsByAisle;
    }

    // Getters and setters
    public Map<String, List<Ingredient>> getIngredientsByAisle() {
        return ingredientsByAisle;
    }

    public void setIngredientsByAisle(Map<String, List<Ingredient>> ingredientsByAisle) {
        this.ingredientsByAisle = ingredientsByAisle;
    }

    public UUID getGroceryListId() {
        return groceryListId;
    }

    public void setGroceryListId(UUID groceryListId) {
        this.groceryListId = groceryListId;
    }

    /**
     * Sets the entire list of ingredients from the API response, organizing them by aisle.
     * Consolidates quantities and standardizes units for ingredients in the same aisle.
     * @param groceryListByAisle The map of aisles with consolidated ingredients
     */
    public void generateGroceriesFromIngredients(Map<String, List<Ingredient>> groceryListByAisle) {
        this.ingredientsByAisle.clear();
        this.ingredientsByAisle.putAll(groceryListByAisle);
    }

    /**
     * Adds an ingredient to the grocery list by aisle, consolidating quantities if the ingredient
     * already exists in the aisle.
     */
    public void addIngredient(String aisle, Ingredient ingredient) {
        // Retrieve the list of ingredients for the specified aisle
        List<Ingredient> ingredientsInAisle = ingredientsByAisle.getOrDefault(aisle, new ArrayList<>());

        // Check if the ingredient with the same name and unit already exists in the aisle
        boolean found = false;
        for (Ingredient existingIngredient : ingredientsInAisle) {
            if (existingIngredient.getName().equals(ingredient.getName()) &&
                existingIngredient.getUnit().equals(ingredient.getUnit())) {
                // If found, consolidate quantities
                double newQuantity = existingIngredient.getQuantity() + ingredient.getQuantity();
                existingIngredient.setQuantity(newQuantity); // Update the quantity
                found = true;
                break;
            }
        }

        // If the ingredient was not found in the aisle, add it as a new entry
        if (!found) {
            ingredientsInAisle.add(ingredient);
        }

        // Update the aisle with the modified ingredient list
        ingredientsByAisle.put(aisle, ingredientsInAisle);
    }

    /**
     * Displays the consolidated grocery list organized by aisle.
     */
    public void displayGroceryList() {
        System.out.println("\nGenerated Grocery List (Organized by Aisle):");

        for (String aisle : ingredientsByAisle.keySet()) {
            System.out.println("Aisle: " + aisle);
            for (Ingredient ingredient : ingredientsByAisle.get(aisle)) {
                System.out.println(" - " + ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName());
            }
            System.out.println(); // Blank line between aisles
        }
    }
}