package ch.unil.doplab.recipe.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {

    private Ingredient ingredient;

    @BeforeEach
    public void setUp() {
        ingredient = new Ingredient("Flour", 2.0, "cups");
    }

    @Test
    public void testIngredientInitialization() {
        assertEquals("Flour", ingredient.getName(), "Ingredient name should match the initialized value");
        assertEquals(2.0, ingredient.getQuantity(), "Ingredient quantity should match the initialized value");
        assertEquals("cups", ingredient.getUnit(), "Ingredient unit should match the initialized value");
    }

    @Test
    public void testSetQuantity() {
        ingredient.setQuantity(3.5);
        assertEquals(3.5, ingredient.getQuantity(), "Quantity should be updated to 3.5");
    }

    @Test
    public void testSetQuantityToZero() {
        ingredient.setQuantity(0);
        assertEquals(0, ingredient.getQuantity(), "Quantity should be updated to 0");
    }

    @Test
    public void testSetUnit() {
        ingredient.setUnit("grams");
        assertEquals("grams", ingredient.getUnit(), "Unit should be updated to 'grams'");
    }

    @Test
    public void testEqualsWithSameNameAndUnit() {
        Ingredient anotherIngredient = new Ingredient("Flour", 1.0, "cups");
        assertEquals(ingredient, anotherIngredient, "Ingredients with the same name and unit should be equal");
    }

    @Test
    public void testNotEqualsWithDifferentUnit() {
        Ingredient anotherIngredient = new Ingredient("Flour", 2.0, "grams");
        assertNotEquals(ingredient, anotherIngredient, "Ingredients with the same name but different units should not be equal");
    }

    @Test
    public void testNotEqualsWithDifferentName() {
        Ingredient anotherIngredient = new Ingredient("Sugar", 2.0, "cups");
        assertNotEquals(ingredient, anotherIngredient, "Ingredients with different names should not be equal");
    }

    @Test
    public void testIngredientWithNutritionalInfo() {
        // Assuming Ingredient has a constructor that accepts NutritionalInfo
        NutritionalInfo nutritionalInfo = new NutritionalInfo(100, 5, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Ingredient ingredientWithInfo = new Ingredient("Milk", 1.5, "liters", "https://example.com/milk.jpg", "Fresh milk", nutritionalInfo);

        assertEquals("Milk", ingredientWithInfo.getName(), "Ingredient name should match");
        assertEquals(1.5, ingredientWithInfo.getQuantity(), "Quantity should match");
        assertEquals("liters", ingredientWithInfo.getUnit(), "Unit should match");
        assertEquals(nutritionalInfo, ingredientWithInfo.getNutritionalInfo(), "Nutritional info should match the provided object");
    }

    @Test
    public void testDisplayIngredientInfo() {
        // Verify that the display method executes without exceptions
        assertDoesNotThrow(() -> ingredient.displayIngredientInfo(), "displayIngredientInfo should execute without exceptions");
    }
}
