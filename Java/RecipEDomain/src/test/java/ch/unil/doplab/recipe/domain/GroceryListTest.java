package ch.unil.doplab.recipe.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GroceryListTest {

    private GroceryList groceryList;

    @BeforeEach
    public void setUp() {
        groceryList = new GroceryList();
    }

    @Test
    public void testAddIngredient() {
        // Add a new ingredient
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        groceryList.addIngredient("Generic", flour);

        // Verify the ingredient was added
        Map<String, Aisle> ingredientsByAisle = groceryList.getIngredientsByAisle();
        for (Aisle aisle : ingredientsByAisle.values()) {
            List<Ingredient> ingredients = aisle.getAisle();
            assertEquals(1, ingredients.size(), "Grocery list should contain one ingredient");
            assertTrue(ingredients.get(0).getName().equals("Flour") & ingredients.get(0).getUnit().equals("cups"), "Grocery list should contain 'Flour' with unit 'cups'");
            assertEquals(2.0, ingredients.get(0).getQuantity(), "Quantity of flour should be 2.0 cups");
        }

    }

    @Test
    public void testAddIngredientConsolidatesQuantity() {
        // Add the same ingredient twice
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        Ingredient moreFlour = new Ingredient("Flour", 1.5, "cups");
        groceryList.addIngredient("Generic", flour);
        groceryList.addIngredient("Generic", moreFlour);

        // Verify quantities were consolidated
        Map<String, Aisle> ingredientsByAisle = groceryList.getIngredientsByAisle();
        for (Aisle aisle : ingredientsByAisle.values()) {
            List<Ingredient> ingredients = aisle.getAisle();
            assertEquals(1, ingredients.size(), "Grocery list should contain one consolidated ingredient");
            assertTrue(ingredients.get(0).getName().equals("Flour") & ingredients.get(0).getUnit().equals("cups"), "Grocery list should contain 'Flour' with unit 'cups'");
            assertEquals(3.5, ingredients.get(0).getQuantity(), "Quantity of flour should be consolidated to 3.5 cups");
        }
    }

    @Test
    public void testAddIngredientDifferentUnits() {
        // Add the same ingredient with different units
        Ingredient flourCups = new Ingredient("Flour", 2.0, "cups");
        Ingredient flourGrams = new Ingredient("Flour", 500.0, "grams");
        groceryList.addIngredient("Generic", flourCups);
        groceryList.addIngredient("Generic", flourGrams);

        // Verify both ingredients exist in the map separately
        Map<String, Aisle> ingredientsByAisle = groceryList.getIngredientsByAisle();
        for (Aisle aisle : ingredientsByAisle.values()) {
            List<Ingredient> ingredients = aisle.getAisle();
            assertEquals(2, ingredients.size(), "Grocery list should contain two separate entries for flour with different units");
            assertTrue(ingredients.get(0).getName().equals("Flour") & ingredients.get(0).getUnit().equals("cups"), "Grocery list should contain 'Flour' with unit 'cups'");
            assertTrue(ingredients.get(1).getName().equals("Flour") & ingredients.get(1).getUnit().equals("grams"), "Grocery list should contain 'Flour' with unit 'grams'");
            assertEquals(2.0, ingredients.get(0).getQuantity(), "Quantity of flour (cups) should be 2.0");
            assertEquals(500.0, ingredients.get(1).getQuantity(), "Quantity of flour (grams) should be 500.0");
        }
    }

    @Test
    public void testSetIngredients() {
        // Create a new ingredient map
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        Ingredient sugar = new Ingredient("Sugar", 1.0, "cups");
        List<Ingredient> newIngredients = List.of(flour, sugar);
        Map<String, Aisle> newIngredientsByAisle = Map.of("Generic", new Aisle(newIngredients));

        // Set the ingredients map in groceryList
        groceryList.setIngredientsByAisle(newIngredientsByAisle);

        // Verify that the new ingredients map was set correctly
        Map<String, Aisle> ingredientsByAisle = groceryList.getIngredientsByAisle();
        for (Aisle aisle : ingredientsByAisle.values()) {
            List<Ingredient> ingredients = aisle.getAisle();
            assertEquals(2, ingredients.size(), "Grocery list should contain two ingredients after setting");
            assertTrue(ingredients.get(0).getName().equals("Flour") & ingredients.get(0).getUnit().equals("cups"), "Grocery list should contain 'Flour' with unit 'cups'");
            assertTrue(ingredients.get(1).getName().equals("Sugar") & ingredients.get(1).getUnit().equals("cups"), "Grocery list should contain 'Sugar' with unit 'cups'");
        }
    }

    @Test
    public void testDisplayGroceryList() {
        // Add ingredients for display testing
        groceryList.addIngredient("Generic", new Ingredient("Flour", 2.0, "cups"));
        groceryList.addIngredient("Generic", new Ingredient("Milk", 1.5, "liters"));

        // Ensure displayGroceryList executes without exceptions
        assertDoesNotThrow(() -> groceryList.displayGroceryList(), "displayGroceryList should execute without exceptions");
    }
}
