package ch.unil.doplab.recipe.ui;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import ch.unil.doplab.recipe.domain.MealPlan;
import ch.unil.doplab.recipe.domain.Meal;
import ch.unil.doplab.recipe.domain.NutritionalInfo;
import ch.unil.doplab.recipe.domain.APIHandler;
import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.domain.UserProfile.MealPlanPreference;

@Named // Replaces @ManagedBean
@SessionScoped // Replaces @SessionScoped from jakarta.faces.bean
public class MealPlanBean implements Serializable {

    private APIHandler apiHandler = new APIHandler(); // Use the existing APIHandler class
    private MealPlan mealPlan; // The generated meal plan
    private boolean useMockData = true; // Toggle between mock data and API data

    @PostConstruct
    public void init() {
        System.out.println("Initializing MealPlanBean...");
        if (useMockData) {
            generateMockMealPlan(); // Use mock data
        } else {
            generateMealPlan(); // Fetch data from the API
        }
    }

    // Generate a meal plan based on user preferences (API)
    public void generateMealPlan() {
        System.out.println("Generate New Meal Plan button clicked (API Mode)!");
        UserProfile userProfile = new UserProfile();
        userProfile.setMealPlanPreference(MealPlanPreference.WEEK); // Example preference
        userProfile.setDesiredServings(2); // Example servings

        // Use APIHandler to fetch the meal plan
        this.mealPlan = apiHandler.generateMealPlan(userProfile);
    }

    // Mock method to generate a meal plan
    public void generateMockMealPlan() {
        System.out.println("Generating mock meal plan...");
        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>();

        // Mock meals for Monday
        List<Meal> mondayMeals = new ArrayList<>();
        mondayMeals.add(new Meal(1, "Loaded Avocado Toast", "/resources/images/avocado-toast.jpg", null));
        mondayMeals.add(new Meal(2, "Crispy Garden Zucchini Cakes", "/resources/images/zucchini-cakes.jpg", null));
        mondayMeals.add(new Meal(3, "Savoury Wrapped Chicken Feast", "/resources/images/chicken-feast.jpg", null));

        // Mock meals for Tuesday
        List<Meal> tuesdayMeals = new ArrayList<>();
        tuesdayMeals.add(new Meal(4, "Breakfast Burrito", "/resources/images/breakfast-burrito.jpg", null));
        tuesdayMeals.add(new Meal(5, "Quinoa Salad", "/resources/images/quinoa-salad.jpg", null));
        tuesdayMeals.add(new Meal(6, "Grilled Salmon", "/resources/images/grilled-salmon.jpg", null));

        // Add meals to days
        dailyMeals.put("Monday", mondayMeals);
        dailyMeals.put("Tuesday", tuesdayMeals);

        // Initialize meal plan with mock data
        this.mealPlan = new MealPlan(dailyMeals, 2); // Assume 2 servings as default
    }

    // Method to get meal type based on its position in the daily meal list
    public String getMealType(int index) {
        switch (index) {
            case 0:
                return "Breakfast";
            case 1:
                return "Lunch";
            case 2:
                return "Dinner";
            default:
                return "Other"; // For any additional meals, like snacks
        }
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setUseMockData(boolean useMockData) {
        this.useMockData = useMockData;
    }

    public boolean isUseMockData() {
        return useMockData;
    }
}
