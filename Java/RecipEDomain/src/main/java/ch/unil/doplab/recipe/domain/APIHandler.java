package ch.unil.doplab.recipe.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIHandler {
    private static final String API_KEY = "81c42538001c4c6093c593c588bdfb1b";

    // Cache to store meal details by meal ID to avoid redundant API calls
    private Map<Integer, Meal> mealDetailsCache = new HashMap<>();

    /**
     * Generates a meal plan based on user preferences such as diet, calorie target,
     * and ingredients to exclude.
     * @param userProfile UserProfile object containing user preferences
     * @return A MealPlan object with generated meals for the day or week
     */
    public MealPlan generateMealPlan(UserProfile userProfile) {
        // Determine the time frame (day or week) for the meal plan
        String timeFrame = userProfile.getMealPlanPreference().toString().toLowerCase();

        // Build the initial URL for the meal plan request
        String url = buildMealPlanUrl(userProfile, timeFrame);

        // Maps days to lists of meals for each day
        Map<String, List<Meal>> dailyMeals = new HashMap<>();

        // List to collect all meal IDs for bulk request
        List<Integer> mealIds = new ArrayList<>();

        try {
            // Make API request to get meal plan data
            String response = makeApiRequest(url);
            // System.out.println("Raw JSON Response:\n" + response);

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response);

            // Define days of the week in the order we want them to appear
            List<String> daysOfWeek = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");

            if (timeFrame.equals("day")) {
                // Handle daily meal plan
                JSONArray mealsArray = jsonResponse.getJSONArray("meals");
                dailyMeals.put("Day 1", parseMeals(mealsArray, mealIds));
            } else {
                // Handle weekly meal plan
                JSONObject weekObject = jsonResponse.getJSONObject("week");
                for (String day : daysOfWeek) {
                    if (weekObject.has(day)) {
                        JSONArray mealsArray = weekObject.getJSONObject(day).getJSONArray("meals");
                        dailyMeals.put(capitalize(day), parseMeals(mealsArray, mealIds));
                    }
                }
            }

            // Populate details for each meal using bulk request
            populateMealDetailsBulk(dailyMeals, mealIds);

            // Populate nutritional information for each meal
            populateNutritionalInfo(dailyMeals);

            // Final ordered map to return
            Map<String, List<Meal>> orderedDailyMeals = new LinkedHashMap<>();

            // Insert days in predefined order
            for (String day : daysOfWeek) {
                String capitalizedDay = capitalize(day);
                if (dailyMeals.containsKey(capitalizedDay)) {
                    orderedDailyMeals.put(capitalizedDay, dailyMeals.get(capitalizedDay));
                }
            }

            return new MealPlan(orderedDailyMeals);

        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Request timed out. Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating meal plan: " + e.getMessage());
        }

        return new MealPlan(dailyMeals);
    }

    /**
     * Constructs the URL for the meal plan request based on user preferences.
     * @param userProfile The user profile with dietary preferences
     * @param timeFrame   "day" or "week" for the meal plan
     * @return The constructed URL as a string
     */
    private String buildMealPlanUrl(UserProfile userProfile, String timeFrame) {
        // Base URL with API key and timeframe
        String url = "https://api.spoonacular.com/mealplanner/generate"
                + "?apiKey=" + API_KEY
                + "&timeFrame=" + timeFrame
                + "&targetCalories=" + userProfile.getDailyCalorieTarget().orElse(0);

// Add diet type to URL if specified
        if (userProfile.getDietType() != null) {
            url += "&diet=" + userProfile.getDietType().toString().toLowerCase();
        }


        // Add ingredients to exclude based on dislikes/allergies
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
        return url;
    }

    /**
     * Parses meals from a JSON array and populates mealIds list.
     * @param mealsArray The JSON array of meals.
     * @param mealIds    List to collect meal IDs for later bulk request.
     * @return A list of Meal objects for a single day.
     */
    private List<Meal> parseMeals(JSONArray mealsArray, List<Integer> mealIds) {
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; i < mealsArray.length(); i++) {
            JSONObject mealJson = mealsArray.getJSONObject(i);
            int id = mealJson.getInt("id"); // Meal ID
            mealIds.add(id); // Collecting meal IDs for bulk request

            // Create meal image URL using the provided image type
            String imageType = mealJson.getString("imageType");
            String imageUrl = "https://spoonacular.com/recipeImages/" + id + "." + imageType;

            // Instantiate Meal object
            Meal meal = new Meal(id, mealJson.getString("title"), imageUrl, null);
            meals.add(meal);
        }
        return meals;
    }

    /**
     * Populates detailed information for meals in bulk using the Spoonacular API.
     * @param dailyMeals Map of meals organized by day
     * @param mealIds    List of meal IDs to fetch details for in bulk
     */
    public void populateMealDetailsBulk(Map<String, List<Meal>> dailyMeals, List<Integer> mealIds) {
        if (mealIds.isEmpty()) return; // Exit if no meal IDs

        // Construct URL for bulk endpoint
        String url = "https://api.spoonacular.com/recipes/informationBulk?apiKey=" + API_KEY
                + "&ids=" + String.join(",", mealIds.stream().map(String::valueOf).toArray(String[]::new));

        try {
            // Make API request for bulk meal details
            String response = makeApiRequest(url);
            // System.out.println("Raw JSON Response:\n" + response);

            // Parse bulk response
            JSONArray bulkResponseArray = new JSONArray(response);

            // Populate cache with meal details for each meal ID
            for (int i = 0; i < bulkResponseArray.length(); i++) {
                JSONObject mealJson = bulkResponseArray.getJSONObject(i);
                int mealId = mealJson.getInt("id");
                if (!mealDetailsCache.containsKey(mealId)) {
                    mealDetailsCache.put(mealId, createMealFromJson(mealJson));
                }
            }

            // Update each meal in dailyMeals with cached details
            for (List<Meal> meals : dailyMeals.values()) {
                for (Meal meal : meals) {
                    Meal cachedMeal = mealDetailsCache.get(meal.getId());
                    if (cachedMeal != null) {
                        meal.setIngredients(cachedMeal.getIngredients());
                        meal.setInstructions(cachedMeal.getInstructions());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching meal details in bulk: " + e.getMessage());
        }
    }

    /**
     * Creates a Meal object from JSON data, setting ingredients and instructions.
     * @param mealJson JSON object containing meal details
     * @return Meal object with details populated
     */
    private Meal createMealFromJson(JSONObject mealJson) {
        // Extract meal details from JSON
        int id = mealJson.getInt("id");
        String title = mealJson.getString("title");
        String imageUrl = mealJson.optString("image", "");

        // Initialize NutritionalInfo as null or with actual data if available
        NutritionalInfo nutritionalInfo = null; // or get it from the JSON if available

        // Create Meal object using the constructor with required parameters
        Meal meal = new Meal(id, title, imageUrl, nutritionalInfo);

        // Populate ingredients
        JSONArray ingredientsArray = mealJson.optJSONArray("extendedIngredients");
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsArray != null) {
            for (int j = 0; j < ingredientsArray.length(); j++) {
                JSONObject ingredientJson = ingredientsArray.getJSONObject(j);
                String name = ingredientJson.optString("name", "Unknown ingredient");
                double amount = ingredientJson.optDouble("amount", 0.0);
                String unit = ingredientJson.optString("unit", "");
                ingredients.add(new Ingredient(name, amount, unit));
            }
        }
        meal.setIngredients(ingredients);

        // Populate instructions
        JSONArray analyzedInstructions = mealJson.optJSONArray("analyzedInstructions");
        List<String> instructions = new ArrayList<>();
        if (analyzedInstructions != null && analyzedInstructions.length() > 0) {
            JSONArray stepsArray = analyzedInstructions.getJSONObject(0).getJSONArray("steps");
            for (int j = 0; j < stepsArray.length(); j++) {
                instructions.add(stepsArray.getJSONObject(j).getString("step"));
            }
        }
        meal.setInstructions(instructions);

        return meal;
    }


    /**
     * Fetches and sets nutritional information for each meal.
     * @param dailyMeals Map of meals organized by day
     */
    private void populateNutritionalInfo(Map<String, List<Meal>> dailyMeals) {
        for (List<Meal> meals : dailyMeals.values()) {
            for (Meal meal : meals) {
                NutritionalInfo nutritionalInfo = fetchNutritionalInfo(meal.getId());
                meal.setNutritionalInfo(nutritionalInfo);
            }
        }
    }

    /**
     * Fetches nutritional information for a meal.
     * @param mealId The ID of the meal to fetch nutritional info for
     * @return NutritionalInfo object with details on calories, protein, fat, and carbs
     */
    private NutritionalInfo fetchNutritionalInfo(int mealId) {
        // URL for nutritional information endpoint
        String url = "https://api.spoonacular.com/recipes/" + mealId + "/nutritionWidget.json" + "?apiKey=" + API_KEY;
        try {
            // Make API request
            String response = makeApiRequest(url);
            JSONObject nutritionJson = new JSONObject(response);

            // Parse nutritional values
            int calories = parseNutritionalValue(nutritionJson.getString("calories"));
            int protein = parseNutritionalValue(nutritionJson.getString("protein"));
            int fat = parseNutritionalValue(nutritionJson.getString("fat"));
            int carbs = parseNutritionalValue(nutritionJson.getString("carbs"));

            return new NutritionalInfo(calories, protein, fat, carbs);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching nutritional info for meal ID " + mealId + ": " + e.getMessage());
            return new NutritionalInfo(0, 0, 0, 0);
        }
    }

    /**
     * Parses a nutritional value string (e.g., "6g") to an integer by removing non-numeric characters.
     * @param valueWithUnit The nutritional value string with units
     * @return The numeric part of the string as an integer
     */
    private int parseNutritionalValue(String valueWithUnit) {
        try {
            // Remove all non-numeric characters, e.g., "6g" -> "6"
            String numericValue = valueWithUnit.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse nutritional value: " + valueWithUnit);
            return 0;
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

        // Check for successful HTTP response
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

    /**
     * Capitalizes the first letter of a given day.
     * @param day Day name to capitalize
     * @return Capitalized day name
     */
    private String capitalize(String day) {
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }
}
