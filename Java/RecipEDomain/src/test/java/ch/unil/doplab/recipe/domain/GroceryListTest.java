package ch.unil.doplab.recipe.domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        groceryList.addIngredient(flour);

        // Verify the ingredient was added
        Map<String, Ingredient> ingredients = groceryList.getIngredients();
        assertEquals(1, ingredients.size(), "Grocery list should contain one ingredient");
        assertTrue(ingredients.containsKey("Flour_cups"), "Grocery list should contain 'Flour' with unit 'cups'");
        assertEquals(2.0, ingredients.get("Flour_cups").getQuantity(), "Quantity of flour should be 2.0 cups");
    }

    @Test
    public void testAddIngredientConsolidatesQuantity() {
        // Add the same ingredient twice
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        Ingredient moreFlour = new Ingredient("Flour", 1.5, "cups");
        groceryList.addIngredient(flour);
        groceryList.addIngredient(moreFlour);

        // Verify quantities were consolidated
        Map<String, Ingredient> ingredients = groceryList.getIngredients();
        assertEquals(1, ingredients.size(), "Grocery list should contain one consolidated ingredient");
        assertTrue(ingredients.containsKey("Flour_cups"), "Grocery list should contain 'Flour' with unit 'cups'");
        assertEquals(3.5, ingredients.get("Flour_cups").getQuantity(), "Quantity of flour should be consolidated to 3.5 cups");
    }

    @Test
    public void testAddIngredientDifferentUnits() {
        // Add the same ingredient with different units
        Ingredient flourCups = new Ingredient("Flour", 2.0, "cups");
        Ingredient flourGrams = new Ingredient("Flour", 500.0, "grams");
        groceryList.addIngredient(flourCups);
        groceryList.addIngredient(flourGrams);

        // Verify both ingredients exist in the map separately
        Map<String, Ingredient> ingredients = groceryList.getIngredients();
        assertEquals(2, ingredients.size(), "Grocery list should contain two separate entries for flour with different units");
        assertTrue(ingredients.containsKey("Flour_cups"), "Grocery list should contain 'Flour' with unit 'cups'");
        assertTrue(ingredients.containsKey("Flour_grams"), "Grocery list should contain 'Flour' with unit 'grams'");
        assertEquals(2.0, ingredients.get("Flour_cups").getQuantity(), "Quantity of flour (cups) should be 2.0");
        assertEquals(500.0, ingredients.get("Flour_grams").getQuantity(), "Quantity of flour (grams) should be 500.0");
    }

    @Test
    public void testSetIngredients() {
        // Create a new ingredient map
        Ingredient flour = new Ingredient("Flour", 2.0, "cups");
        Ingredient sugar = new Ingredient("Sugar", 1.0, "cups");
        Map<String, Ingredient> newIngredients = Map.of(
                "Flour_cups", flour,
                "Sugar_cups", sugar
        );

        // Set the ingredients map in groceryList
        groceryList.setIngredients(newIngredients);

        // Verify that the new ingredients map was set correctly
        Map<String, Ingredient> ingredients = groceryList.getIngredients();
        assertEquals(2, ingredients.size(), "Grocery list should contain two ingredients after setting");
        assertTrue(ingredients.containsKey("Flour_cups"), "Grocery list should contain 'Flour' with unit 'cups'");
        assertTrue(ingredients.containsKey("Sugar_cups"), "Grocery list should contain 'Sugar' with unit 'cups'");
    }

    @Test
    public void testDisplayGroceryList() {
        // Add ingredients for display testing
        groceryList.addIngredient(new Ingredient("Flour", 2.0, "cups"));
        groceryList.addIngredient(new Ingredient("Milk", 1.5, "liters"));

        // Ensure displayGroceryList executes without exceptions
        assertDoesNotThrow(() -> groceryList.displayGroceryList(), "displayGroceryList should execute without exceptions");
    }
}
