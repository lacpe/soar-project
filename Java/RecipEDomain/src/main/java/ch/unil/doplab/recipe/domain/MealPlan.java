package ch.unil.doplab.recipe.domain;

import java.util.*;

public class MealPlan {
    private UUID mealPlanId;
    private Map<String, List<Meal>> dailyMeals; // Key: Day, Value: List of Meals (breakfast, lunch, dinner)
    private int calorieTarget;
    private int desiredServings;

    public MealPlan() {
        this(null, null, 0);
    }

    public MealPlan(Map<String, List<Meal>> dailyMeals) {
        this(null, dailyMeals, 0);
    }

    public MealPlan(Map<String, List<Meal>> dailyMeals, int desiredServings) {
        this(null, dailyMeals, desiredServings);
    }

    // Updated constructor to include UserProfile
    public MealPlan(UUID mealPlanId, Map<String, List<Meal>> dailyMeals, int desiredServings) {
        this.mealPlanId = mealPlanId;
        this.dailyMeals = dailyMeals != null ? dailyMeals : new LinkedHashMap<>();
        this.desiredServings = desiredServings;
    }

    //Displays the meal plan with desired servings information.
    public void displayMealPlan() {
        String servingsText = this.desiredServings == 1 ? "serving" : "servings";
        System.out.println("\nGenerated Meal Plan: " + this.desiredServings + " " + servingsText);

        for (String day : dailyMeals.keySet()) {
            System.out.println("Day: " + day);
            for (Meal meal : dailyMeals.get(day)) {
                meal.displayMealInfo();
                meal.displayInstructions();
                meal.displayMealIngredients();
            }
            System.out.println();
        }
    }

    public int getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(int calorieTarget) {
        this.calorieTarget = calorieTarget;
    }

    public Map<String, List<Meal>> getDailyMeals() {
        return dailyMeals;
    }

    public void setDailyMeals(Map<String, List<Meal>> dailyMeals) {
        this.dailyMeals = dailyMeals;
    }

    public UUID getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(UUID mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public int getDesiredServings() {
        return desiredServings;
    }

    public void setDesiredServings(int desiredServings) {
        this.desiredServings = desiredServings;
    }

    public List<Meal> getAllMeals() {
        List<Meal> allMeals = new ArrayList<>();
        for (String day : dailyMeals.keySet()) {
            allMeals.addAll(dailyMeals.get(day));
        }
        return allMeals;
    }

    public Meal getMealById(int mealId) {
        System.out.println("Searching for Meal with ID: " + mealId);
        Meal foundMeal = getAllMeals().stream()
                .filter(meal -> meal.getId() == mealId)
                .findFirst()
                .orElse(null);

        if (foundMeal == null) {
            System.out.println("No meal found with ID: " + mealId);
        } else {
            System.out.println("Found Meal: " + foundMeal.getTitle());
            System.out.println("Instructions: " + foundMeal.getInstructions());
        }

     return foundMeal;
    }

}
