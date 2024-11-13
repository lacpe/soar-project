package ch.unil.doplab.recipe.domain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.OutputStream;

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
        String timeFrame = userProfile.getMealPlanPreference().toString().toLowerCase(); // Get time frame preference (e.g., day or week)
        String url = buildMealPlanUrl(userProfile, timeFrame); // Construct URL for API request
        Map<String, List<Meal>> dailyMeals = new LinkedHashMap<>(); // Store meals ordered by day
        List<Integer> mealIds = new ArrayList<>(); // Collect meal IDs for bulk data requests
        int desiredServings = userProfile.getDesiredServings(); // Get user's desired servings

        try {
            // Make API request for the meal plan data
            String response = makeApiRequest(url, "GET", null);
            JSONObject jsonResponse = new JSONObject(response); // Parse JSON response
            List<String> daysOfWeek = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");

            if (timeFrame.equals("day")) {
                // For daily meal plans, directly retrieve meals array
                JSONArray mealsArray = jsonResponse.getJSONArray("meals");
                dailyMeals.put("Day 1", parseMeals(mealsArray, mealIds)); // Parse meals and store in map
            } else {
                // For weekly meal plans, process meals for each day
                JSONObject weekObject = jsonResponse.getJSONObject("week");
                for (String day : daysOfWeek) {
                    if (weekObject.has(day)) { // Check if day exists in the response
                        JSONArray mealsArray = weekObject.getJSONObject(day).getJSONArray("meals");
                        dailyMeals.put(capitalize(day), parseMeals(mealsArray, mealIds)); // Parse and add meals for each day
                    }
                }
            }

            // Populate meal details with user's desired servings
            populateMealDetailsBulk(dailyMeals, mealIds, desiredServings);

            // Generate the consolidated grocery list based on the meals
            List<Meal> allMeals = new ArrayList<>();
            dailyMeals.values().forEach(allMeals::addAll);
            GroceryList groceryList = generateConsolidatedShoppingList(allMeals);

            // Create and return a MealPlan with user profile and daily meals
            return new MealPlan(userProfile, dailyMeals, groceryList);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating meal plan: " + e.getMessage());
        }

        return new MealPlan(userProfile, dailyMeals, new GroceryList()); // Return an empty MealPlan with userProfile if there’s an error
    }

    /**
     * Constructs the URL for the meal plan request based on user preferences.
     * @param userProfile The user profile with dietary preferences
     * @param timeFrame   "day" or "week" for the meal plan
     * @return The constructed URL as a string
     */
    private String buildMealPlanUrl(UserProfile userProfile, String timeFrame) {
        // Base URL with API key, timeframe, and calorie target
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
        return url; // Return constructed URL
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
            meals.add(meal); // Add meal to the list
        }
        return meals;
    }

    /**
     * Populates detailed information for meals in bulk, using the Spoonacular API.
     * @param dailyMeals Map of meals organized by day
     * @param mealIds    List of meal IDs to fetch details for in bulk
     * @param desiredServings Number of servings the user wants for each meal
     */

    public void populateMealDetailsBulk(Map<String, List<Meal>> dailyMeals, List<Integer> mealIds, int desiredServings) {
        if (mealIds.isEmpty()) return; // Exit if no meal IDs

        // Construct URL for bulk meal details endpoint
        String url = "https://api.spoonacular.com/recipes/informationBulk?apiKey=" + API_KEY
                + "&ids=" + String.join(",", mealIds.stream().map(String::valueOf).toArray(String[]::new)) + "&includeNutrition=true";

        try {
            // Make API request for bulk meal details
            String response = makeApiRequest(url, "GET", null);
            // System.out.println("Raw JSON Response:\n" + response);

            // Parse bulk response
            JSONArray bulkResponseArray = new JSONArray(response);

            // Populate cache with meal details for each meal ID
            for (int i = 0; i < bulkResponseArray.length(); i++) {
                JSONObject mealJson = bulkResponseArray.getJSONObject(i);
                int mealId = mealJson.getInt("id");

                // If the meal isn't in the cache, cache it with all details, including nutritional info
                if (!mealDetailsCache.containsKey(mealId)) {
                    Meal meal = createMealFromJson(mealJson, desiredServings);

                    // Parse and set nutritional information from the response
                    NutritionalInfo nutritionalInfo = extractNutritionalInfo(mealJson);
                    meal.setNutritionalInfo(nutritionalInfo);

                    mealDetailsCache.put(mealId, meal); // Cache the meal details
                }
            }

            // Update each meal in dailyMeals with cached details
            for (List<Meal> meals : dailyMeals.values()) {
                for (Meal meal : meals) {
                    Meal cachedMeal = mealDetailsCache.get(meal.getId());
                    if (cachedMeal != null) {
                        meal.setIngredients(cachedMeal.getIngredients());
                        meal.setInstructions(cachedMeal.getInstructions());
                        meal.setNutritionalInfo(cachedMeal.getNutritionalInfo());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching meal details in bulk: " + e.getMessage());
        }
    }

    /**
     * Generates a consolidated grocery list using the Spoonacular Compute Shopping List API.
     * @param meals The list of meals for the week
     * @return GroceryList object containing all ingredients needed for the meal plan
     */

    public GroceryList generateConsolidatedShoppingList(List<Meal> meals) {
        try {
            // Collect all ingredients into a single JSON array
            JSONArray itemsArray = new JSONArray();

            for (Meal meal : meals) {
                for (Ingredient ingredient : meal.getIngredients()) {
                    if (ingredient.getName() != null && ingredient.getUnit() != null && ingredient.getQuantity() > 0) {
                        // Format ingredient as a single string: "<quantity> <unit> <name>"
                        String ingredientString = ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName();
                        itemsArray.put(ingredientString); // Add ingredient to items array
                    }
                }
            }

            // Construct the JSON payload as {"items": [ ... ]}
            JSONObject payload = new JSONObject();
            payload.put("items", itemsArray);

            // Print payload for debugging
            //System.out.println("Sending JSON Payload: " + payload.toString());

            // Construct the API request URL
            String url = "https://api.spoonacular.com/mealplanner/shopping-list/compute?apiKey=" + API_KEY;

            // Send the request and parse the response
            String response = makeApiRequest(url, "POST", payload.toString());

            // Print the received JSON response to inspect its structure
            //System.out.println("Received JSON Response: " + response);

            // Convert the response to a JSON object
            JSONObject jsonResponse = new JSONObject(response);

            // Parse the response to get the organized grocery list by aisle
            Map<String, List<Ingredient>> groceryListByAisle = parseShoppingListResponse(jsonResponse);

            // Create a new GroceryList object and populate it with the organized grocery list
            GroceryList groceryList = new GroceryList();
            groceryList.generateGroceriesFromIngredients(groceryListByAisle);

            // Parse the response to extract the consolidated ingredient list
            return groceryList;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating shopping list: " + e.getMessage());
        }
        return new GroceryList();  // Return an empty list if there's an error
    }

     /**
     * Extracts nutritional information from the JSON response and constructs a NutritionalInfo object.
     * @param mealJson JSON object containing nutritional details
     * @return NutritionalInfo object
     */

    private NutritionalInfo extractNutritionalInfo(JSONObject mealJson) {
        // Initialize default values for nutritional info
        int calories = 0, protein = 0, fat = 0, carbs = 0;
        double saturatedFat = 0, fiber = 0, sugar = 0, sodium = 0;
        double vitaminC = 0, calcium = 0, iron = 0, potassium = 0, vitaminA = 0, vitaminK = 0, magnesium = 0;

        if (mealJson.has("nutrition")) {
            JSONArray nutrients = mealJson.getJSONObject("nutrition").getJSONArray("nutrients");

            // Loop through nutrients to find relevant data
            for (int j = 0; j < nutrients.length(); j++) {
                JSONObject nutrient = nutrients.getJSONObject(j);
                String name = nutrient.getString("name");
                int amount = (int) nutrient.getDouble("amount"); // Assuming grams are integers

                // Match nutrient name to corresponding field
                switch (name.toLowerCase()) {
                    case "calories":
                        calories = amount;
                        break;
                    case "protein":
                        protein = amount;
                        break;
                    case "fat":
                        fat = amount;
                        break;
                    case "saturated fat":
                        saturatedFat = amount;
                        break;
                    case "carbohydrates":
                        carbs = amount;
                        break;
                    case "fiber":
                        fiber = amount;
                        break;
                    case "sugar":
                        sugar = amount;
                        break;
                    case "sodium":
                        sodium = amount;
                        break;
                    case "vitamin c":
                        vitaminC = amount;
                        break;
                    case "calcium":
                        calcium = amount;
                        break;
                    case "iron":
                        iron = amount;
                        break;
                    case "potassium":
                        potassium = amount;
                        break;
                    case "vitamin a":
                        vitaminA = amount;
                        break;
                    case "vitamin k":
                        vitaminK = amount;
                        break;
                    case "magnesium":
                        magnesium = amount;
                        break;
                }
            }
        }
        return new NutritionalInfo(calories, protein, fat, carbs, saturatedFat, fiber, sugar, sodium, vitaminC, calcium, iron, potassium, vitaminA, vitaminK, magnesium);
    }

    /**
     * Parses JSON response from the Compute Shopping List API into a grocery list by aisle.
     * @param jsonResponse JSON object containing the shopping list response
     * @return Map of aisles with a list of consolidated ingredients
     */
    private Map<String, List<Ingredient>> parseShoppingListResponse(JSONObject jsonResponse) {
        Map<String, List<Ingredient>> groceryListByAisle = new LinkedHashMap<>();

        JSONArray aislesArray = jsonResponse.getJSONArray("aisles");  // The API organizes items by aisle
        for (int i = 0; i < aislesArray.length(); i++) {
            JSONObject aisleObject = aislesArray.getJSONObject(i);
            String aisleName = aisleObject.getString("aisle"); // Get aisle name

            // Get items in this aisle
            JSONArray aisleItems = aisleObject.getJSONArray("items"); // Get items in the aisle
            List<Ingredient> ingredientsInAisle = new ArrayList<>();

            // Loop through items and add each to ingredientsInAisle
            for (int j = 0; j < aisleItems.length(); j++) {
                JSONObject item = aisleItems.getJSONObject(j);
                String name = item.getString("name");

                // Extract metric amount and unit if available
                double amount = 0;
                String unit = "";

                // Check for "measures" object to get "amount" and "unit" in metric
                if (item.has("measures") && item.getJSONObject("measures").has("metric")) {
                    JSONObject metricMeasure = item.getJSONObject("measures").getJSONObject("metric");
                    amount = metricMeasure.getDouble("amount");
                    unit = metricMeasure.optString("unit", "");
                }
                // Add item to the list for this aisle
                ingredientsInAisle.add(new Ingredient(name, amount, unit));
            }
            groceryListByAisle.put(aisleName, ingredientsInAisle); // Add aisle to map
        }
        return groceryListByAisle;
    }

    /**
     * Creates a Meal object from JSON data, scaling ingredients based on desired servings.
     * @param mealJson JSON object containing meal details
     * @param desiredServings Number of servings
     * @return Meal object with details populated
     */
    private Meal createMealFromJson(JSONObject mealJson, int desiredServings) {
        // Extract meal details from JSON
        int id = mealJson.getInt("id"); // Get meal ID
        String title = mealJson.getString("title"); // Get meal title
        String imageUrl = mealJson.optString("image", ""); // Get meal image URL

        int originalServings = mealJson.getInt("servings"); // Get original servings

        double scalingFactor = (double) desiredServings / originalServings; // Calculate scaling factor

        // Create Meal object using the constructor with required parameters
        Meal meal = new Meal(id, title, imageUrl, null);

        // Scale and set ingredients
        JSONArray ingredientsArray = mealJson.optJSONArray("extendedIngredients");
        meal.setIngredients(scaleIngredients(ingredientsArray, scalingFactor));

        // Set instructions and other details (no scaling required here)
        JSONArray analyzedInstructions = mealJson.optJSONArray("analyzedInstructions");
        List<String> instructions = new ArrayList<>();

        if (analyzedInstructions != null && analyzedInstructions.length() > 0) {
            JSONArray stepsArray = analyzedInstructions.getJSONObject(0).getJSONArray("steps");
            for (int j = 0; j < stepsArray.length(); j++) {
                instructions.add(stepsArray.getJSONObject(j).getString("step"));
            }
        }
        meal.setInstructions(instructions);

        // Extract and set nutritional information if available
        NutritionalInfo nutritionalInfo = extractNutritionalInfo(mealJson);
        meal.setNutritionalInfo(nutritionalInfo);

        return meal;
    }

    /**
     * Scales ingredients based on the desired servings.
     * @param ingredientsArray JSON array of ingredients
     * @param scalingFactor Factor to scale each ingredient by
     * @return List of scaled ingredients
     */

    private List<Ingredient> scaleIngredients(JSONArray ingredientsArray, double scalingFactor) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsArray.length(); i++) {
            JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
            String name = ingredientJson.getString("name");
            double amount = ingredientJson.getDouble("amount") * scalingFactor; // Scale amount
            String unit = ingredientJson.optString("unit", ""); // Get unit

            ingredients.add(new Ingredient(name, amount, unit)); // Add to list
        }
        return ingredients;
    }

    /**
    * Makes an HTTP request (GET or POST) to the specified URL and returns the response as a string.
    * @param urlString URL to send the request to
    * @param method    The HTTP method ("GET" or "POST")
    * @param jsonPayload JSON payload for POST requests (null for GET requests)
    * @return Response from the API as a String
    * @throws Exception if there's an issue with the request
    */
    private String makeApiRequest(String urlString, String method, String jsonPayload) throws Exception {
        URL url = new URL(urlString); // Create URL object
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Open connection
        conn.setRequestMethod(method); // Set request method

        if ("POST".equalsIgnoreCase(method)) { // Handle POST request setup
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true); // Enable output stream for payload

            if (jsonPayload != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8");
                    os.write(input, 0, input.length); // Write JSON payload to output stream
                }
            }
        }

        // Check response code and throw error if not 200
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        // Read and build response string
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect(); // Close connection
        return response.toString(); // Return response
    }

    /**
     * Capitalizes the first letter of a given day.
     * @param day Day name to capitalize
     * @return Capitalized day name
     */
    private String capitalize(String day) {
        return day.substring(0, 1).toUpperCase() + day.substring(1); // Capitalize first letter
    }
}
