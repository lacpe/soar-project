package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.RecipEService;
import ch.unil.doplab.recipe.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Inject
    UserProfileBean userProfileBean;
    @Inject
    private RecipEService recipEService;

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
        UserProfile user = recipEService.getUserProfile(userProfileBean.getUserId().toString());

        /* Ensure allergies is not null
        if (userProfile.getAllergies() == null) {
            userProfile.setAllergies(new HashSet<>()); // Initialize with an empty set
        } */

        // Log user profile for debugging
        logUserProfile(user);

        // Use APIHandler to fetch the meal plan
        this.mealPlan = apiHandler.generateMealPlan(user);
//        flattenDailyMeals();
        if (this.mealPlan != null && this.mealPlan.getDailyMeals() != null) {
            flattenDailyMeals();
            System.out.println("Successfully fetched meal plan: " + this.mealPlan.getDailyMeals());
        } else {
            System.out.println("Failed to fetch meal plan or meal plan is empty.");
            this.mealPlan = null; // Reset in case of failure
        }
    }

    // Mock method to generate a meal plan
    public void generateMockMealPlan() {
        System.out.println("Generating mock meal plan...");
        Map<String, DailyMealSet> dailyMeals = new LinkedHashMap<>();


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
        DailyMealSet mondayMealSet = new DailyMealSet(mondayMeals);
        DailyMealSet tuesdayMealSet = new DailyMealSet(tuesdayMeals);
        DailyMealSet wednesdayMealSet = new DailyMealSet(wednesdayMeals);
        DailyMealSet thursdayMealSet = new DailyMealSet(thursdayMeals);
        DailyMealSet fridayMealSet = new DailyMealSet(fridayMeals);
        DailyMealSet saturdayMealSet = new DailyMealSet(saturdayMeals);
        DailyMealSet sundayMealSet = new DailyMealSet(sundayMeals);
        // Adding all the meals to dailyMeals
        dailyMeals.put("Monday", mondayMealSet);
        dailyMeals.put("Tuesday", tuesdayMealSet);
        dailyMeals.put("Wednesday", wednesdayMealSet);
        dailyMeals.put("Thursday", thursdayMealSet);
        dailyMeals.put("Friday", fridayMealSet);
        dailyMeals.put("Saturday", saturdayMealSet);
        dailyMeals.put("Sunday", sundayMealSet);

        // Assign dailyMeals to the mealPlan
        this.mealPlan = new MealPlan(dailyMeals, 2); // Assume 2 servings as default

    }

    // Flatten all meals into a single list
    private void flattenDailyMeals() {
        allMeals = new ArrayList<>();
        if (mealPlan != null && mealPlan.getDailyMeals() != null) {
            for (DailyMealSet dailyMealSet : mealPlan.getDailyMeals().values()) {
                allMeals.addAll(dailyMealSet.getMeals());
            }
        }
    }

    // Get image path with default fallback logic
    public String getImagePath(Meal meal, int index) {
        if (useMockData){
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
        } else{
            // Check if the image URL is null, empty, or invalid, return a default image
            String imageUrl = meal.getImageUrl();
            if (imageUrl == null || imageUrl.isEmpty() || !isValidImageUrl(imageUrl)) {
                return getDefaultImageForIndex(index);
            }
            return imageUrl;
        }

    }

    // Helper methods for API meal validation
    private boolean isValidImageUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    private String getDefaultImageForIndex(int index) {
        switch (index) {
            case 0: return DEFAULT_IMAGE_BREAKFAST;
            case 1: return DEFAULT_IMAGE_LUNCH;
            case 2: return DEFAULT_IMAGE_DINNER;
            default: return "/images/default.png";
        }
    }

    // View a recipe by ID
    public String viewRecipe(int mealId) {
        System.out.println("Clicked meal ID: " + mealId);
        return "RecipeDetail.xhtml?faces-redirect=true&id=" + mealId;
    }

    // Truncate meal titles
    public String getTruncatedTitle(String title, int maxLength) {
        if (title == null || title.length() <= maxLength) {
            return title;
        }
        return title.substring(0, maxLength) + "...";
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

    // Log the user profile for debugging
    private void logUserProfile(UserProfile userProfile) {
        System.out.println("User Profile:");
        System.out.println("Meal Plan Preference: " + userProfile.getMealPlanPreference());
        System.out.println("Disliked Ingredients: " +
                (userProfile.getDislikedIngredients() != null ? userProfile.getDislikedIngredients() : "None"));
    }

    // Getters for use in the UI
    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
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
    public GroceryList generateGroceryList() {
        if (this.mealPlan != null && this.mealPlan.getAllMeals() != null) {
            System.out.println("Generating grocery list based on the existing meal plan...");
            GroceryList groceryList = apiHandler.generateConsolidatedShoppingList(this.mealPlan.getAllMeals());
            System.out.println("Grocery list generated successfully.");
            return groceryList;
        } else {
            System.err.println("Cannot generate grocery list: Meal plan is null or empty.");
            return new GroceryList(); // Return an empty grocery list if there's no valid meal plan
        }
    }

}

