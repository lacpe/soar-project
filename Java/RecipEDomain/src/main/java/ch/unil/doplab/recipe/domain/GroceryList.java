package ch.unil.doplab.recipe.domain;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GroceryList {
    private UUID groceryListId;
    private Map<String, Ingredient> groceries; // Map by ingredient name and unit for easy lookup

    public GroceryList() {
        this.groceries = new HashMap<>();
    }

    public GroceryList(Map<String, Ingredient> groceries) {
        this(null, groceries);
    }

    public GroceryList(UUID groceryListId, Map<String, Ingredient> groceries) {
        this.groceryListId = groceryListId;
        this.groceries = groceries;
    }

    // Getters and setters
    public Map<String, Ingredient> getGroceries() {
        return groceries;
    }

    public void setGroceries(Map<String, Ingredient> groceries) {
        this.groceries = groceries;
    }

    public UUID getGroceryListId() {
        return groceryListId;
    }

    public void setGroceryListId(UUID groceryListId) {
        this.groceryListId = groceryListId;
    }

    /**
     * Sets the entire list of ingredients from the API response, consolidating quantities and standardizing units.
     * @param ingredients The list of consolidated ingredients from the API
     */
    public void generateGroceriesFromIngredients(List<Ingredient> ingredients) {
        this.groceries.clear();
        for (Ingredient ingredient : ingredients) {
            String key = ingredient.getName() + "_" + ingredient.getUnit(); // Unique key by name and unit
            this.groceries.put(key, ingredient);
        }
    }

    /**
     * Adds an ingredient to the grocery list, consolidating quantities if the ingredient
     * already exists in the list.
     */
    public void addIngredient(Ingredient ingredient) {
        String key = ingredient.getName() + "_" + ingredient.getUnit(); // Unique key by name and unit

        if (groceries.containsKey(key)) {
            // Consolidate quantities if ingredient with same name and unit already exists
            Ingredient existingIngredient = groceries.get(key);
            double newQuantity = existingIngredient.getQuantity() + ingredient.getQuantity();
            existingIngredient.setQuantity(newQuantity); // Update the quantity
        } else {
            // Add new ingredient if it doesn't exist in the list
            groceries.put(key, ingredient);
        }
    }

    /**
     * Displays the consolidated grocery list in a user-friendly format.
     */
    public void displayGroceryList() {
        for (Ingredient ingredient : groceries.values()) {
            System.out.println(ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName());
        }
    }
}