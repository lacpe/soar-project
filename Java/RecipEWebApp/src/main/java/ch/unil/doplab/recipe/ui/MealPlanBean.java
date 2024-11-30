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

@Named
@SessionScoped
public class MealPlanBean implements Serializable {

    private APIHandler apiHandler = new APIHandler();
    private MealPlan mealPlan;
    private List<Meal> allMeals; // Flattened list of meals
    private int currentMealIndex;
    private boolean useMockData = true;

    // Default images for meal types
    private static final String DEFAULT_IMAGE_BREAKFAST = "/images/defaultmeals/croissant.png";
    private static final String DEFAULT_IMAGE_LUNCH = "/images/defaultmeals/lunch_colored.png";
    private static final String DEFAULT_IMAGE_DINNER = "/images/defaultmeals/dining-room.png";

    @PostConstruct
    public void init() {
        System.out.println("Initializing MealPlanBean...");
        if (useMockData) {
            generateMockMealPlan();
        } else {
            generateMealPlan();
        }
        flattenDailyMeals();
        currentMealIndex = 0; // Start with the first meal
    }

    // Generate a meal plan based on user preferences (API)
    public void generateMealPlan() {
        System.out.println("Generate New Meal Plan button clicked (API Mode)!");
        UserProfile userProfile = new UserProfile();
        userProfile.setMealPlanPreference(UserProfile.MealPlanPreference.WEEK);
        userProfile.setDesiredServings(2);

        // Use APIHandler to fetch the meal plan
        this.mealPlan = apiHandler.generateMealPlan(userProfile);
        flattenDailyMeals();
    }

    // Mock method to generate a meal plan
    public void generateMockMealPlan() {
        System.out.println("Generating mock meal plan...");
        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>();


        // Mock meals for Monday
        List<Meal> mondayMeals = new ArrayList<>();
        mondayMeals.add(new Meal(1, "Loaded Avocado Toast", "/images/mockmeals/avocado-toast.png", null));
        mondayMeals.add(new Meal(2, "Crispy Garden Zucchini Cakes", "/images/mockmeals/zucchini-cakes.png", null));
        mondayMeals.add(new Meal(3, "Savoury Wrapped Chicken Feast", "/images/mockmeals/chicken-feast.png", null));

        // Mock meals for Tuesday
        List<Meal> tuesdayMeals = new ArrayList<>();
        tuesdayMeals.add(new Meal(4, "Breakfast Burrito", null, null));
        tuesdayMeals.add(new Meal(5, "Quinoa Salad", null, null));
        tuesdayMeals.add(new Meal(6, "Grilled Salmon", null, null));

        // Mock meals for Wednesday
        List<Meal> wednesdayMeals = new ArrayList<>();
        wednesdayMeals.add(new Meal(7, "Pancakes with Maple Syrup", "/images/mockmeals/pancakes.png", null));
        wednesdayMeals.add(new Meal(8, "Vegetarian Buddha Bowl", "/images/mockmeals/buddha-bowl.png", null));
        wednesdayMeals.add(new Meal(9, "Herbed Lemon Chicken", null, null));

        // Mock meals for Thursday
        List<Meal> thursdayMeals = new ArrayList<>();
        thursdayMeals.add(new Meal(10, "Smoothie Bowl", null, null));
        thursdayMeals.add(new Meal(11, "Caprese Sandwich", null, null));
        thursdayMeals.add(new Meal(12, "Beef Stir Fry", "/images/mockmeals/beef-stir-fry.png", null));

        // Mock meals for Friday
        List<Meal> fridayMeals = new ArrayList<>();
        fridayMeals.add(new Meal(13, "French Toast", null, null));
        fridayMeals.add(new Meal(14, "Caesar Salad", "/images/mockmeals/caesar-salad.png", null));
        fridayMeals.add(new Meal(15, "Spaghetti Carbonara", null, null));

        // Mock meals for Saturday
        List<Meal> saturdayMeals = new ArrayList<>();
        saturdayMeals.add(new Meal(16, "Scrambled Eggs", "/images/mockmeals/scrambled-eggs.png", null));
        saturdayMeals.add(new Meal(17, "Avocado Chicken Wrap", null, null));
        saturdayMeals.add(new Meal(18, "BBQ Ribs", "/images/mockmeals/bbq-ribs.png", null));

        // Mock meals for Sunday
        List<Meal> sundayMeals = new ArrayList<>();
        sundayMeals.add(new Meal(19, "Granola and Yogurt", "/images/mockmeals/granola-yogurt.png", null));
        sundayMeals.add(new Meal(20, "Greek Salad", null, null));
        sundayMeals.add(new Meal(21, "Roast Turkey", null, null));

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

    // Flatten all meals into a single list
    private void flattenDailyMeals() {
        allMeals = new ArrayList<>();
        if (mealPlan != null && mealPlan.getDailyMeals() != null) {
            for (List<Meal> meals : mealPlan.getDailyMeals().values()) {
                allMeals.addAll(meals);
            }
        }
    }

    // Get image path with default fallback logic
    public String getImagePath(Meal meal, int index) {
        if (meal.getImageUrl() == null || meal.getImageUrl().isEmpty()) {
            switch (index) {
                case 0:
                    return DEFAULT_IMAGE_BREAKFAST; // Default for breakfast
                case 1:
                    return DEFAULT_IMAGE_LUNCH; // Default for lunch
                case 2:
                    return DEFAULT_IMAGE_DINNER; // Default for dinner
                default:
                    return "/images/default.png"; // Fallback for other cases
            }
        }
        return meal.getImageUrl(); // Return actual image if present
    }

    // View a recipe by ID
    public String viewRecipe(int mealId) {
        System.out.println("Clicked meal ID: " + mealId);
        return "RecipeDetail.xhtml?faces-redirect=true&id=" + mealId;
    }


    // Navigation methods
    public Meal getCurrentRecipe() {
        if (allMeals == null || currentMealIndex < 0 || currentMealIndex >= allMeals.size()) {
            return null;
        }
        return allMeals.get(currentMealIndex);
    }

    public void viewNextRecipe() {
        if (hasNextRecipe()) {
            currentMealIndex++;
        }
    }

    public void viewPreviousRecipe() {
        if (hasPreviousRecipe()) {
            currentMealIndex--;
        }
    }

    public boolean hasNextRecipe() {
        return currentMealIndex < allMeals.size() - 1;
    }

    public boolean hasPreviousRecipe() {
        return currentMealIndex > 0;
    }

    // Getters for use in the UI
    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public List<Meal> getAllMeals() {
        return allMeals;
    }

    public boolean isUseMockData() {
        return useMockData;
    }

    public void setUseMockData(boolean useMockData) {
        this.useMockData = useMockData;
    }

    // Method to get meal type based on its position in the daily meal list
    public String getMealType(Integer index) {
        if (index == null) {
            return "Unknown"; // Handle null gracefully
        }
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
}

