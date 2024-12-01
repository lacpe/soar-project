package ch.unil.doplab.recipe;

import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.domain.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        userProfileTarget = client.target(BASE_URL).path("userprofile");
        mealPlanTarget = client.target(BASE_URL).path("mealplan");
        groceryListTarget = client.target(BASE_URL).path("grocerylist");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    /*
     * Service operations
     */

    // Reset the service (useful for testing or development)
    // Unfortunately no such option exists but I could add one
    public void resetService() {
        String response = serviceTarget
                .path("reset")
                .request()
                .get(String.class);
        System.out.println("Service reset response: " + response);
    }

    // Get available dietary preferences (or other global configurations)
    // Same here
    public List<String> getDietaryPreferences() {
        return serviceTarget
                .path("dietaryPreferences")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {});
    }

    /*
     * User Profile operations
     */

    // Fetch a specific user profile by ID
    public UserProfile getUserProfile(String userId) {
        return userProfileTarget
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(UserProfile.class); // Deserialize JSON into UserProfile object
    }


    public void updateUserProfile(String userId, UserProfile updatedProfile) {
        try {
            System.out.println("Updating profile for ID: " + userId);
            System.out.println("Data being sent: " + updatedProfile);

            userProfileTarget
                    .path(userId)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(updatedProfile, MediaType.APPLICATION_JSON));

            System.out.println("Profile update request sent successfully.");
        } catch (Exception e) {
            System.out.println("Error during update request: " + e.getMessage());
            throw e;
        }
    }


    /*
     * Meal Plan operations
     */

    // Generate a meal plan for a specific user
    public String generateMealPlan(String userId) {
        return serviceTarget
                .path("mealplan/generate")
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
    // Changed to do it by user ID - but I could also add an option to do it by meal plan ID
    public String generateGroceryList(String userId) {
        return serviceTarget
                .path("grocerylist/generate")
                .path(userId)
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



    /**
     * Register a new user in the system.
     *
     * @param user the UserProfile object to register
     * @return true if registration is successful, false if the email already exists
     */
    public UserProfile addUser(UserProfile user) {
        user.setUserId(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = userProfileTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() == 200 && response.hasEntity()) {
            var newlyCreatedUser = response.readEntity(UserProfile.class);
            user.setUserId(newlyCreatedUser.getUserId());
            return newlyCreatedUser;
        }
        else {
            throw new WebApplicationException(response.getStatus());
        }
    }

    /**
     * Authenticate a user with the given email and password.
     *
     * @param username    the user's email (used as the username)
     * @param password the user's plain-text password
     * @return the authenticated UserProfile, or null if authentication fails
     */
    public UUID authenticateUser(String username, String password) {
        return serviceTarget
                .path("authenticate")
                .path(username)
                .path(password)
                .request(MediaType.APPLICATION_JSON)
                .get(UUID.class);
    }
}
