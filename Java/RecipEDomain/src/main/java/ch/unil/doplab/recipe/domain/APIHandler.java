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
    private static final String API_KEY = "855c7cae4e6f4ff69c586bb42b97e0c7";

    /*
     * Generates a meal plan based on user preferences such as diet, calorie target,
     * and ingredients to exclude (disliked or allergenic).
     * @param planType       either "daily" or "weekly" for the timeframe
     * @param userProfile    UserProfile object containing user preferences
     * @return               a MealPlan object with generated meals
     */
    public MealPlan generateMealPlan(UserProfile userProfile) {

        String timeFrame = userProfile.getMealPlanPreference();  // "day" or "week"

        String url = "https://api.spoonacular.com/mealplanner/generate"
                     + "?apiKey=" + API_KEY
                     + "&timeFrame=" + timeFrame
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
        List<Integer> mealIds = new ArrayList<>();  // Collect all meal IDs for bulk request
        try {
                // Make the API call to fetch meal plan data
            String response = makeApiRequest(url);
            System.out.println("Raw JSON Response:\n" + response);

            JSONObject jsonResponse = new JSONObject(response);

            if (timeFrame.equals("day")) {
                // Parsing a daily plan
                JSONArray mealsArray = jsonResponse.getJSONArray("meals");
                String day = "Day 1";
                dailyMeals.put(day, parseMeals(mealsArray, mealIds));
            } else {
                // Parsing a weekly plan
                JSONObject weekObject = jsonResponse.getJSONObject("week");
                for (String day : weekObject.keySet()) {
                    JSONArray mealsArray = weekObject.getJSONObject(day).getJSONArray("meals");
                    dailyMeals.put(day.substring(0, 1).toUpperCase() + day.substring(1), parseMeals(mealsArray, mealIds)); // Capitalize day name
                }
            }

            // Fetch and set detailed information for all meals at once using the bulk endpoint
            populateMealDetailsBulk(dailyMeals, mealIds);

            // Fetch detailed nutritional info for each meal using individual API calls
            for (List<Meal> meals : dailyMeals.values()) {
                for (Meal meal : meals) {
                    NutritionalInfo nutritionalInfo = fetchNutritionalInfo(meal.getId());
                    meal.setNutritionalInfo(nutritionalInfo);
                }
            }

        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Request timed out. Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating meal plan: " + e.getMessage());
        }

        return new MealPlan(userProfile, dailyMeals);
    }

    /**
    * Helper method to parse meals from a JSON array and populate mealIds list.
    * @param mealsArray The JSON array of meals.
    * @param mealIds    List to collect meal IDs for later bulk request.
    * @return           A list of Meal objects for a single day.
    */
    private List<Meal> parseMeals(JSONArray mealsArray, List<Integer> mealIds) {
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; i < mealsArray.length(); i++) {
            JSONObject mealJson = mealsArray.getJSONObject(i);
            int id = mealJson.getInt("id");
            mealIds.add(id);
            String imageType = mealJson.getString("imageType");
            String imageUrl = "https://spoonacular.com/recipeImages/" + id + "." + imageType;

            Meal meal = new Meal(id, mealJson.getString("title"), imageUrl, null);
            meals.add(meal);
        }
        return meals;
    }

    /**
     * Populates detailed information for all meals at once using the Get Recipe Information Bulk endpoint.
     * @param dailyMeals   Map of meals organized by day
     * @param mealIds      List of meal IDs to fetch details for in bulk
     */
    public void populateMealDetailsBulk(Map<String, List<Meal>> dailyMeals, List<Integer> mealIds) {
        if (mealIds.isEmpty()) return;

        // Construct the URL for the bulk endpoint, joining all meal IDs with commas
        String url = "https://api.spoonacular.com/recipes/informationBulk?apiKey=" + API_KEY
                     + "&ids=" + String.join(",", mealIds.stream().map(String::valueOf).toArray(String[]::new));

        try {
            String response = makeApiRequest(url);
            JSONArray bulkResponseArray = new JSONArray(response);

            // Create a map to associate each ID with its corresponding Meal object
            Map<Integer, Meal> mealMap = new HashMap<>();
            for (List<Meal> meals : dailyMeals.values()) {
                for (Meal meal : meals) {
                    mealMap.put(meal.getId(), meal);
                }
            }

            // Populate each meal with ingredients and instructions from the bulk response
            for (int i = 0; i < bulkResponseArray.length(); i++) {
                JSONObject mealJson = bulkResponseArray.getJSONObject(i);
                int mealId = mealJson.getInt("id");

                Meal meal = mealMap.get(mealId);
                if (meal != null) {
                    // Set ingredients from `extendedIngredients`
                    List<Ingredient> ingredients = new ArrayList<>();
                    JSONArray ingredientsArray = mealJson.getJSONArray("extendedIngredients");
                    for (int j = 0; j < ingredientsArray.length(); j++) {
                        JSONObject ingredientJson = ingredientsArray.getJSONObject(j);
                        Ingredient ingredient = new Ingredient(
                            ingredientJson.getString("name"),
                            ingredientJson.getDouble("amount"),
                            ingredientJson.getString("unit")
                        );
                        ingredients.add(ingredient);
                    }
                    meal.setIngredients(ingredients);

                    // Set instructions from `analyzedInstructions`
                    List<String> instructions = new ArrayList<>();
                    JSONArray analyzedInstructions = mealJson.optJSONArray("analyzedInstructions");
                    if (analyzedInstructions != null && analyzedInstructions.length() > 0) {
                        JSONArray stepsArray = analyzedInstructions.getJSONObject(0).getJSONArray("steps");
                        for (int j = 0; j < stepsArray.length(); j++) {
                            instructions.add(stepsArray.getJSONObject(j).getString("step"));
                        }
                    }
                    meal.setInstructions(instructions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching meal details in bulk: " + e.getMessage());
        }
    }

    /**
     * Fetches detailed nutritional info for a meal by making an API call to the Nutrition Widget endpoint.
     * @param mealId   The ID of the meal to fetch nutritional info for
     * @return         A NutritionalInfo object containing calories, protein, fat, and carbs
     */
    private NutritionalInfo fetchNutritionalInfo(int mealId) {
        String url = "https://api.spoonacular.com/recipes/" + mealId + "/nutritionWidget.json" + "?apiKey=" + API_KEY;

        try {
            String response = makeApiRequest(url);
            JSONObject nutritionJson = new JSONObject(response);

            // Parse nutrition details, using helper to convert string with units to integer
            int calories = parseNutritionalValue(nutritionJson.getString("calories"));
            int protein = parseNutritionalValue(nutritionJson.getString("protein"));
            int fat = parseNutritionalValue(nutritionJson.getString("fat"));
            int carbs = parseNutritionalValue(nutritionJson.getString("carbs"));

            return new NutritionalInfo(calories, protein, fat, carbs);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching nutritional info for meal ID " + mealId + ": " + e.getMessage());
            return new NutritionalInfo(0, 0, 0, 0); // Default values if data cannot be fetched
        }
    }

    /**
     * Parses a nutritional value string (e.g., "6g") to an integer, removing non-numeric characters.
     * @param valueWithUnit  The nutritional value string with units
     * @return               The numeric part of the string as an integer
     */
    private int parseNutritionalValue(String valueWithUnit) {
        try {
            // Remove all non-numeric characters, e.g., "6g" -> "6"
            String numericValue = valueWithUnit.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse nutritional value: " + valueWithUnit);
            return 0; // Default to 0 if parsing fails
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
