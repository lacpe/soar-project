package ch.unil.doplab.recipe.domain;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import java.util.Map;
import java.util.HashMap;

public class GroceryList {
    private Map<String, Ingredient> ingredientMap; // Map by ingredient name and unit for easy consolidation

    public GroceryList() {
        this.ingredientMap = new HashMap<>();
    }

    /**
     * Adds an ingredient to the grocery list, consolidating quantities if the ingredient
     * already exists in the list.
     */
    public void addIngredient(Ingredient ingredient) {
        String key = ingredient.getName() + "_" + ingredient.getUnit(); // Unique key by name and unit

        if (ingredientMap.containsKey(key)) {
            // Consolidate quantities if ingredient with same name and unit already exists
            Ingredient existingIngredient = ingredientMap.get(key);
            double newQuantity = existingIngredient.getQuantity() + ingredient.getQuantity();
            existingIngredient.setQuantity(newQuantity); // Update the quantity
        } else {
            // Add new ingredient if it doesn't exist in the list
            ingredientMap.put(key, ingredient);
        }
    }

    /**
     * Returns a Map of all ingredients in the grocery list, with consolidated quantities.
     */
    public Map<String, Ingredient> getIngredients() {
        return ingredientMap;
    }

    public Map<String, Ingredient> setIngredients(Map<String, Ingredient> ingredients) {
        ingredientMap.clear();
        ingredientMap = ingredients;
        return ingredientMap;
    }

    // Method to display ingredients in a user-friendly format with detailed info
    public void displayGroceryList() {
        for (Ingredient ingredient : ingredientMap.values()) {
            System.out.println(ingredient.getQuantity() + " " + ingredient.getUnit() + " of " + ingredient.getName());
            System.out.println();
        }
    }
}
