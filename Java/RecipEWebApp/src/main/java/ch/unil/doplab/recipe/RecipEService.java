package ch.unil.doplab.recipe.rest;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@ApplicationScoped
public class RecipEService {

    // Base URL for your RESTful API
    private static final String BASE_URL = "http://localhost:8080/RecipEService-1.0-SNAPSHOT/api";

    // WebTargets for different resources
    private WebTarget userProfileTarget;
    private WebTarget mealPlanTarget;
    private WebTarget groceryListTarget;
    private WebTarget serviceTarget;

    @PostConstruct
    public void init() {
        System.out.println("RecipEService initialized: " + this.hashCode());
        Client client = ClientBuilder.newClient();

        // Initialize WebTargets for the resources
        userProfileTarget = client.target(BASE_URL).path("userProfiles");
        mealPlanTarget = client.target(BASE_URL).path("mealPlans");
        groceryListTarget = client.target(BASE_URL).path("groceryLists");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    /*
     * Service operations
     */

    // Reset the service (useful for testing or development)
    public void resetService() {
        String response = serviceTarget
                .path("reset")
                .request()
                .get(String.class);
        System.out.println("Service reset response: " + response);
    }

    // Get available dietary preferences (or other global configurations)
    public List<String> getDietaryPreferences() {
        return serviceTarget
                .path("dietaryPreferences")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {});
    }

    /*
     * User Profile operations
     */

    // Fetch all user profiles
    public List<String> getAllUserProfiles() {
        return userProfileTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {});
    }

    // Fetch a specific user profile by ID
    public String getUserProfile(String userId) {
        return userProfileTarget
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }

    /*
     * Meal Plan operations
     */

    // Generate a meal plan for a specific user
    public String generateMealPlan(String userId) {
        return mealPlanTarget
                .path("generate")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .post(null, String.class);
    }

    // Get a meal plan by user ID
    public String getMealPlan(String userId) {
        return mealPlanTarget
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }

    /*
     * Grocery List operations
     */

    // Generate a grocery list for a specific meal plan
    public String generateGroceryList(String mealPlanId) {
        return groceryListTarget
                .path("generate")
                .path(mealPlanId)
                .request(MediaType.APPLICATION_JSON)
                .post(null, String.class);
    }

    // Get grocery list details by meal plan ID
    public String getGroceryList(String mealPlanId) {
        return groceryListTarget
                .path(mealPlanId)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }
}