package ch.unil.doplab.recipe.domain;

import ch.unil.doplab.recipe.domain.NutritionalInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NutritionalInfoTest {

    private NutritionalInfo nutritionalInfo;

    @BeforeEach
    public void setUp() {
        nutritionalInfo = new NutritionalInfo(250, 10, 5, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);; // example values: calories, protein, fat, carbs
    }

    @Test
    public void testNutritionalInfoInitialization() {
        assertEquals(250, nutritionalInfo.getCalories(), "Calories should match the initialized value");
        assertEquals(10, nutritionalInfo.getProtein(), "Protein should match the initialized value");
        assertEquals(5, nutritionalInfo.getFat(), "Fat should match the initialized value");
        assertEquals(30, nutritionalInfo.getCarbs(), "Carbs should match the initialized value");
    }

    @Test
    public void testGetCalories() {
        assertEquals(250, nutritionalInfo.getCalories(), "getCalories should return 250");
    }

    @Test
    public void testGetProtein() {
        assertEquals(10, nutritionalInfo.getProtein(), "getProtein should return 10");
    }

    @Test
    public void testGetFat() {
        assertEquals(5, nutritionalInfo.getFat(), "getFat should return 5");
    }

    @Test
    public void testGetCarbs() {
        assertEquals(30, nutritionalInfo.getCarbs(), "getCarbs should return 30");
    }

    @Test
    public void testDisplayNutritionalInfo() {
        // Verify that the display method executes without throwing an exception
        assertDoesNotThrow(() -> nutritionalInfo.displayNutritionalInfo(), "displayNutritionalInfo should execute without exceptions");
    }
}
