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
    // Free access API, we have multiple in case we reach the limit by testing
    private static final String API_KEY = "d5f32705bfb64f0992a6786d1d98062d";
        // "855c7cae4e6f4ff69c586bb42b97e0c7" 2
        // "d5f32705bfb64f0992a6786d1d98062d" 1
        //"81c42538001c4c6093c593c588bdfb1b" 3
    // Cache to store meal details by meal ID because when multiple meals appeared in the meal plan, the details wouldn't get fetched correctly
    private Map<Integer, Meal> mealDetailsCache = new HashMap<>();

    //Generates a meal plan based on user preferences such as diet, calorie target, and ingredients to exclude.
    //userProfile UserProfile object containing user preferences
    //returns A MealPlan object with generated meals for the day or week

    public MealPlan generateMealPlan(UserProfile userProfile) {
        String timeFrame = userProfile.getMealPlanPreference().toString().toLowerCase(); // Get time frame preference (e.g., day or week)
        String url = buildMealPlanUrl(userProfile, timeFrame); // Construct URL for API request
        Map<String, DailyMealSet> dailyMeals = new LinkedHashMap<>(); // Store meals ordered by day
        List<Integer> mealIds = new ArrayList<>(); // Collect meal IDs for bulk data requests
        int desiredServings = userProfile.getDesiredServings(); // Get user's desired servings

        try {
            // Make API request for the meal plan data
            String response = makeApiRequest(url, "GET", null);
            // Printing the response for debugging and inspecting
            // System.out.println(response);
            JSONObject jsonResponse = new JSONObject(response); // Parse JSON response
            List<String> daysOfWeek = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
            // Added to have meals presented correctly everyday, I may need for each meal to add an attribute mealTime
            // List<String> mealsOfDay = Arrays.asList("breakfast", "lunch", "dinner");

            // If user has chosen to generate a day of meals
            if (timeFrame.equals("day")) {
                // For daily meal plans, directly retrieve meals array, not need for more
                JSONArray mealsArray = jsonResponse.getJSONArray("meals");
                dailyMeals.put("Day 1", new DailyMealSet(parseMeals(mealsArray, mealIds))); // Parse meals and store in map
            } else {
                // For weekly meal plans, process meals for each day
                JSONObject weekObject = jsonResponse.getJSONObject("week");
                // Links the meals with each day of the week, as the json response contains the days
                for (String day : daysOfWeek) {
                    if (weekObject.has(day)) { // Check if day exists in the response
                        JSONArray mealsArray = weekObject.getJSONObject(day).getJSONArray("meals");
                        dailyMeals.put(capitalize(day), new DailyMealSet(parseMeals(mealsArray, mealIds))); // Parse and add meals for each day
                    }
                }
            }

            // Populate meal details with user's desired servings
            // Calls the method so that generating a meal plan automatically populates the meals with the details
            populateMealDetailsBulk(dailyMeals, mealIds, desiredServings);

            // Create and return a MealPlan with user profile and daily meals
            return new MealPlan(dailyMeals, userProfile.getDesiredServings());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating meal plan: " + e.getMessage());
        }

        return new MealPlan(dailyMeals, userProfile.getDesiredServings()); // Return an empty MealPlan with userProfile if there’s an error
    }

    //Constructs the URL for the meal plan request based on user preferences.
    //userProfile The user profile with dietary preferences
    //it's a separate method made for clarity
    //timeFrame   "day" or "week" for the meal plan
    //returns The constructed URL as a string
    private String buildMealPlanUrl(UserProfile userProfile, String timeFrame) {
        // Base URL for generating meal plans with API key, timeframe, and calorie target
        String url = "https://api.spoonacular.com/mealplanner/generate"
                + "?apiKey=" + API_KEY
                + "&timeFrame=" + timeFrame
                + "&targetCalories=" + userProfile.getDailyCalorieTarget();

        // Add diet type to URL if specified
        if (userProfile.getDietType() != null) {
            url += "&diet=" + userProfile.getDietType().toString().toLowerCase();
        }


        // Add ingredients to exclude based on dislikes/allergies
        // Joins the lists of exclusions and allergies in one string separated by a comma to fit the params requirements
        StringJoiner excludeIngredients = new StringJoiner(",");
        for (String disliked : userProfile.getDislikedIngredients()) {
            excludeIngredients.add(disliked);
        }
        for (String allergy : userProfile.getAllergies()) {
            excludeIngredients.add(allergy);
        }
        if (excludeIngredients.length() > 0) { // Now if elements are specified will include this endpoint
            url += "&exclude=" + excludeIngredients.toString();
        }
        return url; // Return constructed URL
    }

    //Parses meals from a JSON array and populates mealIds list.
    //mealsArray The JSON array of meals.
    //mealIds    List to collect meal IDs for later bulk request.
    //returns A list of Meal objects for a single day.
    private List<Meal> parseMeals(JSONArray mealsArray, List<Integer> mealIds) {
        List<Meal> meals = new ArrayList<>();
        // for each meal in the array get a single object (ID) then add the ID to a list of IDs
        for (int i = 0; i < mealsArray.length(); i++) {
            JSONObject mealJson = mealsArray.getJSONObject(i);
            int id = mealJson.getInt("id"); // Meal ID
            mealIds.add(id); // Collecting meal IDs for bulk request

            // Create meal image URL using the provided image type
            // Most image links don't work sadly
            String imageUrl = mealJson.optString("image", "");

            // Instantiate Meal object with the id got earlier, and getting the title too. Does that for each meal in the mealsArray
            // Nutritional info is null because we haven't fetched the info yet
            Meal meal = new Meal(id, mealJson.getString("title"), imageUrl, null);
            meals.add(meal); // Add meal to the list
        }
        return meals;
    }

    //Populates detailed information for meals in bulk, using the Spoonacular API.
    //dailyMeals Map of meals organized by day
    //mealIds    List of meal IDs to fetch details for in bulk
    //desiredServings Number of servings the user wants for each meal
    public void populateMealDetailsBulk(Map<String, DailyMealSet> dailyMeals, List<Integer> mealIds, int desiredServings) {
        if (mealIds.isEmpty()) return; // Exit if no meal IDs

        // Construct URL for bulk meal details endpoint
        String url = "https://api.spoonacular.com/recipes/informationBulk?apiKey=" + API_KEY
                + "&ids=" + String.join(",", mealIds.stream().map(String::valueOf).toArray(String[]::new)) + "&includeNutrition=true"; // Transform mealIds into a string which ids being separated by a comma

        try {
            // Make API request for bulk meal details
            String response = makeApiRequest(url, "GET", null);
            // print the response for debugging
            //System.out.println("Raw JSON Response:\n" + response);

            // Parse bulk response
            JSONArray bulkResponseArray = new JSONArray(response);

            // Populate cache with meal details for each meal ID
            // Pour chaque élément de la liste commençant par le premier (0) allant jusqu'à la taille de la liste
            for (int i = 0; i < bulkResponseArray.length(); i++) {
                // Returns separates json for each meals
                JSONObject mealJson = bulkResponseArray.getJSONObject(i);
                // Sets the mealId of each meal to be later referenced in populateMealDetailsBulk
                int mealId = mealJson.getInt("id");

                // If the meal isn't in the cache, cache it with all details, including nutritional info
                if (!mealDetailsCache.containsKey(mealId)) {
                    // Instanciate a new meal using createMealFromJson
                    Meal meal = createMealFromJson(mealJson, desiredServings);

                    // Parse and set nutritional information from the response that is extracted from populateMealDetailsBulk
                    NutritionalInfo nutritionalInfo = extractNutritionalInfo(mealJson);
                    meal.setNutritionalInfo(nutritionalInfo);

                    mealDetailsCache.put(mealId, meal); // Cache the meal details
                }
            }

            // Update each meal in dailyMeals with cached details
            for (DailyMealSet dailyMealSet : dailyMeals.values()) {
                for (Meal meal : dailyMealSet.getMeals()) {
                    Meal cachedMeal = mealDetailsCache.get(meal.getId());
                    // For every meal in meals and for every cachedMeals, if meal is in cachedMeal then sets the already existing details for this meal
                    if (cachedMeal != null) {
                        meal.setImageUrl(cachedMeal.getImageUrl()); // Ensure the image URL is set from enriched data
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

    //Generates a consolidated grocery list using the Spoonacular Compute Shopping List API.
    //meals The list of meals for the week
    //returns GroceryList object containing all ingredients needed for the meal plan
    public GroceryList generateConsolidatedShoppingList(List<Meal> meals) {
        try {
            // Collect all ingredients into a single JSON array
            // The api takes a json as params of all ingredients put together, that will then return a consolidated list of ingredients summed up and converted to the right metric
            JSONArray itemsArray = new JSONArray();

            for (Meal meal : meals) {
                for (Ingredient ingredient : meal.getIngredients()) {
                    // For each ingredient of each meal if there is a name, a unit and a quantity gets them and return a string
                    if (ingredient.getName() != null && ingredient.getUnit() != null && ingredient.getQuantity() > 0) {
                        // Format ingredient as a single string: "<quantity> <unit> <name>", it's the format accepted by the API
                        String ingredientString = ingredient.getQuantity() + " " + ingredient.getUnit() + " " + ingredient.getName();
                        itemsArray.put(ingredientString); // Add ingredient to items array
                    }
                }
            }

            // Construct the JSON payload as {"items": [ ... ]}
            JSONObject payload = new JSONObject();
            // So here the list of ingredients previously made is put in a json object starting with items as this is what the api needs
            payload.put("items", itemsArray);

            // Print payload for debugging
            //System.out.println("Sending JSON Payload: " + payload.toString());

            // Construct the API request URL
            String url = "https://api.spoonacular.com/mealplanner/shopping-list/compute?apiKey=" + API_KEY;

            // Send the request and parse the response, POST is used since we have to send a json object and it's not part of the URL like the GET requests
            String response = makeApiRequest(url, "POST", payload.toString());

            // Print the received JSON response to inspect its structure
            //System.out.println("Received JSON Response: " + response);

            // Convert the response to a JSON object
            JSONObject jsonResponse = new JSONObject(response);

            // Parse the response to get the organized grocery list by aisle
            // Since the shopping list returns an aisle and ingredients, we create a map where aisle:ingredient
            Map<String, Aisle> groceryListByAisle = parseShoppingListResponse(jsonResponse);

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

    // Extracts nutritional information from the JSON response and constructs a NutritionalInfo object.
    //extracts the nutritional info from the response of populatemealdetailsbulk, method is then called in the populate method
    //mealJson JSON object containing nutritional details
    // Returns NutritionalInfo object
    private NutritionalInfo extractNutritionalInfo(JSONObject mealJson) {
        // Initialize default values for nutritional info
        int calories = 0, protein = 0, fat = 0, carbs = 0;
        double saturatedFat = 0, fiber = 0, sugar = 0, sodium = 0;
        double vitaminC = 0, calcium = 0, iron = 0, potassium = 0, vitaminA = 0, vitaminK = 0, magnesium = 0;

        // If the mealJson so the response from populatemealdetailsbulk contains nutrition, we create an array of each meal and their nutrients
        if (mealJson.has("nutrition")) {
            JSONArray nutrients = mealJson.getJSONObject("nutrition").getJSONArray("nutrients");

            // Loop through nutrients to find relevant data
            for (int j = 0; j < nutrients.length(); j++) {
                // So for each element of the array we look for the name of the nutrient and the quantity
                JSONObject nutrient = nutrients.getJSONObject(j);
                String name = nutrient.getString("name");
                int amount = (int) nutrient.getDouble("amount"); // Assuming grams are integers

                // Match nutrient name to corresponding field
                // The switch statement here directly matches the nutrient name with the corresponding variable. Each case in the switch assigns the amount to the correct nutrient variable
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

    //Parses JSON response from the Compute Shopping List API into a grocery list by aisle.
    //jsonResponse JSON object containing the shopping list response
    //returns Map of aisles with a list of consolidated ingredients
    private Map<String, Aisle> parseShoppingListResponse(JSONObject jsonResponse) {
        Map<String, Aisle> groceryListByAisle = new LinkedHashMap<>();

        JSONArray aislesArray = jsonResponse.getJSONArray("aisles");  // The API organizes items by aisle
        for (int i = 0; i < aislesArray.length(); i++) {
            // Creates json object for every aisle
            JSONObject aisleObject = aislesArray.getJSONObject(i);
            String aisleName = aisleObject.getString("aisle"); // Get aisle name from json object

            // Get items in this aisle
            JSONArray aisleItems = aisleObject.getJSONArray("items"); // Get items in the aisle from every json object
            Aisle ingredientsInAisle = new Aisle();

            // Loop through items and add each to ingredientsInAisle
            // For each ingredient of each aisle check for measures object to get the amount and unit in metric
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
                ingredientsInAisle.getAisle().add(new Ingredient(name, amount, unit));
            }
            groceryListByAisle.put(aisleName, ingredientsInAisle); // Add aisle to map
        }
        return groceryListByAisle;
    }

    //Creates a Meal object from JSON data, scaling ingredients based on desired servings.
    //mealJson JSON object containing meal details
    //desiredServings Number of servings
    //returns Meal object with details populated
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

        // If there are instructions in the json object then gets steps then adds every steps in a list that is set to the attribute
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

    // Used to scale the ingredient in createMealFromJson then the ingredients from meal are used to make the grocerylist
    //ingredientsArray JSON array of ingredients
    //scalingFactor Factor to scale each ingredient by
    //return List of scaled ingredients
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

    //Makes an HTTP request (GET or POST) to the specified URL and returns the response as a string.
    //urlString URL to send the request to
    //method    The HTTP method ("GET" or "POST")
    //jsonPayload JSON payload for POST requests (null for GET requests)
    //returns Response from the API as a String
    private String makeApiRequest(String urlString, String method, String jsonPayload) throws Exception {
        // Create a URL object from the provided URL string
        URL url = new URL(urlString); // Create URL object
        // Open an HTTP connection to the specified URL
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Open connection
        // Set the HTTP method (GET or POST) for the request
        conn.setRequestMethod(method); // Set request method

        // If the method is POST, configure the connection for sending data
        if ("POST".equalsIgnoreCase(method)) { // Handle POST request setup
            conn.setRequestProperty("Content-Type", "application/json"); // Specify JSON format for data
            conn.setDoOutput(true); // Enable output stream to send data in the body

            // If a JSON payload is provided, write it to the output stream
            if (jsonPayload != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8"); // Convert JSON payload to bytes
                    os.write(input, 0, input.length); // Write JSON payload to output stream
                }
            }
        }

        // Check response code and throw error if not 200
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        // Create a BufferedReader to read the response from the input stream
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        // StringBuilder to accumulate the response line by line
        StringBuilder response = new StringBuilder();
        String output;
        // Read each line from the response and append it to the response StringBuilder
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect(); // Close connection
        return response.toString(); // Return response
    }

    //Capitalizes the first letter of a given day.
    //day Day name to capitalize
    //returns Capitalized day name
    private String capitalize(String day) {
        return day.substring(0, 1).toUpperCase() + day.substring(1); // Capitalize first letter
    }
}
