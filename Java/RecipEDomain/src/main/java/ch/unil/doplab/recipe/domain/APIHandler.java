package ch.unil.doplab.recipe.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIHandler {
    private static final String API_KEY = "d5f32705bfb64f0992a6786d1d98062d";

    /*
     * Generates a meal plan based on user preferences such as diet, calorie target,
     * and ingredients to exclude (disliked or allergenic).
     * @param planType       either "daily" or "weekly" for the timeframe
     * @param diet           the type of diet (e.g., "vegetarian", "vegan") per Spoonacular's diet list
     * @param calorieTarget  daily calorie target for the meal plan
     * @param userProfile    UserProfile object containing user preferences
     * @return               a MealPlan object with generated meals
     */
    public MealPlan generateMealPlan(String planType, UserProfile userProfile) {
        String url = "https://api.spoonacular.com/mealplanner/generate"
                     + "?apiKey=" + API_KEY
                     + "&timeFrame=" + planType
                     + "&targetCalories=" + userProfile.getDailyCalorieTarget().orElse(0);

        // Include diet and exclusions based on user profile
        if (userProfile.getDietType() != null && !userProfile.getDietType().isEmpty()) {
            url += "&diet=" + userProfile.getDietType();
        }

        // Exclude ingredients based on user dislikes and allergies
        StringJoiner excludeIngredients = new StringJoiner(",");
        for (String disliked : userProfile.getDislikedIngredients()) {
            excludeIngredients.add(disliked);
        }
        for (String allergy : userProfile.getAllergies()) {
            excludeIngredients.add(allergy);
        }
        if (excludeIngredients.length() > 0) {
            url += "&exclude=" + excludeIngredients.toString();
        }

        Map<String, List<Meal>> dailyMeals = new HashMap<>();
        try {
            // Make the API call to fetch meal plan data
            String response = makeApiRequest(url);
            JSONObject jsonResponse = new JSONObject(response);

            // Parse the response for each meal and populate details
            JSONArray mealsArray = jsonResponse.getJSONArray("meals");
            for (int i = 0; i < mealsArray.length(); i++) {
                JSONObject mealJson = mealsArray.getJSONObject(i);
                Meal meal = new Meal(
                        mealJson.getInt("id"),
                        mealJson.getString("title"),
                        mealJson.getString("image"),
                        new NutritionalInfo(
                            mealJson.getInt("calories"),
                            mealJson.getInt("protein"),
                            mealJson.getInt("fat"),
                            mealJson.getInt("carbs")
                        )
                );

                // Fetch and set detailed information for the meal
                populateMealDetails(meal);

                // Organize meals by day (based on daily or weekly plan)
                String day = determineDay(planType, i);
                dailyMeals.computeIfAbsent(day, k -> new ArrayList<>()).add(meal);
            }
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Request timed out. Please try again later."); }
          catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating meal plan: " + e.getMessage());
        }

        return new MealPlan(planType, userProfile, dailyMeals);
    }

    private String determineDay(String planType, int index) {
        if (planType.equals("daily")) {
            return "Day 1";
        } else if (planType.equals("weekly")) {
            return "Day " + ((index / 3) + 1); // assuming 3 meals per day
        }
        return "Unknown";
    }

    /**
     * Populates detailed information for a meal, including ingredients, nutritional info, and instructions.
     */
    public void populateMealDetails(Meal meal) {
        String url = "https://api.spoonacular.com/recipes/" + meal.getId() + "/information"
                + "?apiKey=" + API_KEY + "&includeNutrition=true";

        try {
            // Make API call to fetch recipe details
            String response = makeApiRequest(url);
            JSONObject jsonResponse = new JSONObject(response);

            // Parse ingredients
            List<Ingredient> ingredients = new ArrayList<>();
            JSONArray ingredientsArray = jsonResponse.getJSONArray("extendedIngredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
                int ingredientId = ingredientJson.getInt("id");
                Ingredient ingredient = fetchIngredientDetails(ingredientId); // Fetch detailed info
                ingredient.setQuantity(ingredientJson.getDouble("amount"));
                ingredient.setUnit(ingredientJson.getString("unit"));
                ingredients.add(ingredient);
            }
            meal.setIngredients(ingredients);

            // Fetch nutritional info and set it in the meal
            NutritionalInfo nutritionalInfo = fetchNutritionalInfo(meal.getId());
            meal.setNutritionalInfo(nutritionalInfo);

            // Fetch instructions
            List<String> instructions = fetchInstructions(meal.getId());
            meal.setInstructions(instructions);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching meal details for meal ID " + meal.getId() + ": " + e.getMessage());
        }
    }
 /**
     * Fetches cooking instructions for a given meal by using the Analyze Recipe Instructions endpoint.
     * @param mealId The ID of the meal to fetch instructions for.
     * @return A list of cooking instructions as individual steps.
     */
    private List<String> fetchInstructions(int mealId) {
        List<String> instructions = new ArrayList<>();
        String url = "https://api.spoonacular.com/recipes/" + mealId + "/analyzedInstructions"
                + "?apiKey=" + API_KEY;

        try {
            String response = makeApiRequest(url);
            JSONArray instructionsArray = new JSONArray(response);

            // Parsing the instruction steps from the response
            if (instructionsArray.length() > 0) { // Check if there are instructions
                JSONArray stepsArray = instructionsArray.getJSONObject(0).getJSONArray("steps");
                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject stepObj = stepsArray.getJSONObject(i);
                    String step = stepObj.getString("step");
                    instructions.add(step);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching instructions for meal ID " + mealId + ": " + e.getMessage());
        }

        return instructions;
    }

     private NutritionalInfo fetchNutritionalInfo(int mealId) {
        String url = "https://api.spoonacular.com/recipes/" + mealId + "/nutritionWidget.json"
                     + "?apiKey=" + API_KEY;

        try {
            String response = makeApiRequest(url);
            JSONObject nutritionJson = new JSONObject(response);

            // Parse nutrition details
            int calories = nutritionJson.getInt("calories");
            int protein = nutritionJson.getInt("protein");
            int fat = nutritionJson.getInt("fat");
            int carbs = nutritionJson.getInt("carbs");

            return new NutritionalInfo(calories, protein, fat, carbs);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching nutritional info for meal ID " + mealId + ": " + e.getMessage());
            return new NutritionalInfo(0, 0, 0, 0); // Default values if data cannot be fetched
        }
    }

    private Ingredient fetchIngredientDetails(int ingredientId) {
        String url = "https://api.spoonacular.com/food/ingredients/" + ingredientId + "/information"
                + "?apiKey=" + API_KEY + "&amount=1";

        try {
            String response = makeApiRequest(url);
            JSONObject ingredientJson = new JSONObject(response);

            // Parse detailed ingredient information
            String name = ingredientJson.getString("name");
            String image = "https://spoonacular.com/cdn/ingredients_100x100/" + ingredientJson.getString("image");
            String description = ingredientJson.optString("original", "No description available");

            // Parse additional nutritional details if available
            JSONObject nutrition = ingredientJson.getJSONObject("nutrition");
            double calories = nutrition.getJSONArray("nutrients").getJSONObject(0).getDouble("amount");
            double protein = nutrition.getJSONArray("nutrients").getJSONObject(1).getDouble("amount");
            double fat = nutrition.getJSONArray("nutrients").getJSONObject(2).getDouble("amount");
            double carbs = nutrition.getJSONArray("nutrients").getJSONObject(3).getDouble("amount");

            NutritionalInfo nutritionalInfo = new NutritionalInfo((int) calories, (int) protein, (int) fat, (int) carbs);

            // Create and return the Ingredient object with detailed information
            return new Ingredient(name, 0, "", image, description, nutritionalInfo);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching ingredient details for ingredient ID " + ingredientId + ": " + e.getMessage());
            return new Ingredient("Unknown", 0, ""); // Fallback if data cannot be fetched
        }
    }

    /**
     * Makes an HTTP GET request to the specified URL and returns the response as a string.
     * @param urlString URL to send the GET request to
     * @return response from the API as a String
     * @throws Exception if there's an issue with the request
     */
    private String makeApiRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect();
        return response.toString();
    }
}
