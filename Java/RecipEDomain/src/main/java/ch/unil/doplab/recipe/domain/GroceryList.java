package ch.unil.doplab.recipe.domain;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class GroceryList {
    private Map<String, Ingredient> ingredientMap; // Map by ingredient name and unit for easy lookup

    public GroceryList() {
        this.ingredientMap = new HashMap<>();
    }

    /**
     * Sets the entire list of ingredients from the API response, consolidating quantities and standardizing units.
     * @param ingredients The list of consolidated ingredients from the API
     */
    public void setIngredients(List<Ingredient> ingredients) {
        ingredientMap.clear();
        for (Ingredient ingredient : ingredients) {
            String key = ingredient.getName() + "_" + ingredient.getUnit(); // Unique key by name and unit
            ingredientMap.put(key, ingredient);
        }
    }

    /**
     * Returns a Map of all ingredients in the grocery list, with consolidated quantities.
     */
    public Map<String, Ingredient> getIngredients() {
        return ingredientMap;
    }

    /**
     * Displays the consolidated grocery list in a user-friendly format.
     */
    public void displayGroceryList() {
        for (Ingredient ingredient : ingredientMap.values()) {
            System.out.println(ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName());
        }
    }
}