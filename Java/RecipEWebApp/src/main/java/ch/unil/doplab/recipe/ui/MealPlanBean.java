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
        mondayMeals.add(new Meal(1, "Loaded Avocado Toast", "/images/avocado-toast.png", null));
        mondayMeals.add(new Meal(2, "Crispy Garden Zucchini Cakes", "/images/zucchini-cakes.png", null));
        mondayMeals.add(new Meal(3, "Savoury Wrapped Chicken Feast", "/images/chicken-feast.png", null));

        // Mock meals for Tuesday
        List<Meal> tuesdayMeals = new ArrayList<>();
        tuesdayMeals.add(new Meal(4, "Breakfast Burrito", "/images/breakfast-burrito.png", null));
        tuesdayMeals.add(new Meal(5, "Quinoa Salad", "/images/quinoa-salad.png", null));
        tuesdayMeals.add(new Meal(6, "Grilled Salmon", "/images/grilled-salmon.png", null));

        // Mock meals for Wednesday
        List<Meal> wednesdayMeals = new ArrayList<>();
        wednesdayMeals.add(new Meal(7, "Pancakes with Maple Syrup", "/images/pancakes.png", null));
        wednesdayMeals.add(new Meal(8, "Vegetarian Buddha Bowl", "/images/buddha-bowl.png", null));
        wednesdayMeals.add(new Meal(9, "Herbed Lemon Chicken", "/images/lemon-chicken.png", null));

        // Mock meals for Thursday
        List<Meal> thursdayMeals = new ArrayList<>();
        thursdayMeals.add(new Meal(10, "Smoothie Bowl", "/images/smoothie-bowl.png", null));
        thursdayMeals.add(new Meal(11, "Caprese Sandwich", "/images/caprese-sandwich.png", null));
        thursdayMeals.add(new Meal(12, "Beef Stir Fry", "/images/beef-stir-fry.png", null));

        // Mock meals for Friday
        List<Meal> fridayMeals = new ArrayList<>();
        fridayMeals.add(new Meal(13, "French Toast", "/images/french-toast.png", null));
        fridayMeals.add(new Meal(14, "Caesar Salad", "/images/caesar-salad.png", null));
        fridayMeals.add(new Meal(15, "Spaghetti Carbonara", "/images/carbonara.png", null));

        // Mock meals for Saturday
        List<Meal> saturdayMeals = new ArrayList<>();
        saturdayMeals.add(new Meal(16, "Scrambled Eggs", "/images/scrambled-eggs.png", null));
        saturdayMeals.add(new Meal(17, "Avocado Chicken Wrap", "/images/avocado-wrap.png", null));
        saturdayMeals.add(new Meal(18, "BBQ Ribs", "/images/bbq-ribs.png", null));

        // Mock meals for Sunday
        List<Meal> sundayMeals = new ArrayList<>();
        sundayMeals.add(new Meal(19, "Granola and Yogurt", "/images/granola-yogurt.png", null));
        sundayMeals.add(new Meal(20, "Greek Salad", "/images/greek-salad.png", null));
        sundayMeals.add(new Meal(21, "Roast Turkey", "/images/roast-turkey.png", null));

        // Adding all the meals to dailyMeals
//        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>();
        dailyMeals.put("Monday", mondayMeals);
        dailyMeals.put("Tuesday", tuesdayMeals);
        dailyMeals.put("Wednesday", wednesdayMeals);
        dailyMeals.put("Thursday", thursdayMeals);
        dailyMeals.put("Friday", fridayMeals);
        dailyMeals.put("Saturday", saturdayMeals);
        dailyMeals.put("Sunday", sundayMeals);

        // Assign dailyMeals to the mealPlan
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
