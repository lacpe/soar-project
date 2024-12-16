package ch.unil.doplab.recipe;

import ch.unil.doplab.recipe.domain.GroceryList;
import ch.unil.doplab.recipe.domain.MealPlan;
import ch.unil.doplab.recipe.domain.UserProfile;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
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

    public boolean removeUserProfile(String userId) {
        return userProfileTarget
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .delete(boolean.class);
    }

    /*
     * Meal Plan operations
     */

    // Get a meal plan by meal plan ID
    public MealPlan getMealPlan(String userId) {
        return mealPlanTarget
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(MealPlan.class);
    }

    public MealPlan addMealPlan(MealPlan mealPlan) {
        mealPlan.setMealPlanId(null);
        var response = mealPlanTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(mealPlan, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 200 && response.hasEntity()) {
            var newlyCreatedMealPlan = response.readEntity(MealPlan.class);
            mealPlan.setMealPlanId(newlyCreatedMealPlan.getMealPlanId());
            return newlyCreatedMealPlan;
        } else {
            throw new WebApplicationException(response.getStatus());
        }
    }

    public MealPlan updateMealPlan(String mealPlanId, MealPlan mealPlan) {
        mealPlan.setMealPlanId(null);
        var response = mealPlanTarget
                .path(mealPlanId)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(mealPlan, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 200 && response.hasEntity()) {
            var updatedMealPlan = response.readEntity(MealPlan.class);
            mealPlan.setMealPlanId(updatedMealPlan.getMealPlanId());
            return updatedMealPlan;
        } else {
            throw new WebApplicationException(response.getStatus());
        }
    }

    public boolean removeMealPlan(String mealPlanId) {
        return mealPlanTarget
                .path(mealPlanId)
                .request(MediaType.APPLICATION_JSON)
                .delete(boolean.class);
    }

    /*
     * Grocery List operations
     */

    // Get grocery list details by grocery list ID
    public GroceryList getGroceryList(String mealPlanId) {
        return groceryListTarget
                .path(mealPlanId)
                .request(MediaType.APPLICATION_JSON)
                .get(GroceryList.class);
    }

    public GroceryList addGroceryList(GroceryList groceryList) {
        groceryList.setGroceryListId(null);
        var response = groceryListTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(groceryList, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 200 && response.hasEntity()) {
            var newlyCreatedGroceryList = response.readEntity(GroceryList.class);
            groceryList.setGroceryListId(newlyCreatedGroceryList.getGroceryListId());
            return newlyCreatedGroceryList;
        } else {
            throw new WebApplicationException(response.getStatus());
        }
    }

    public GroceryList updateGroceryList(String groceryListId, GroceryList groceryList) {
        groceryList.setGroceryListId(null);
        var response = groceryListTarget
                .path(groceryListId)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(groceryList, MediaType.APPLICATION_JSON));
        if (response.getStatus() == 200 && response.hasEntity()) {
            var updatedGroceryList = response.readEntity(GroceryList.class);
            groceryList.setGroceryListId(updatedGroceryList.getGroceryListId());
            return updatedGroceryList;
        } else {
            throw new WebApplicationException(response.getStatus());
        }
    }

    public boolean removeGroceryList(String groceryListId) {
        return groceryListTarget
                .path(groceryListId)
                .request(MediaType.APPLICATION_JSON)
                .delete(boolean.class);
    }

    /*
    Service operations
     */
    public MealPlan getMealPlanByUserId(String userId) {
        return serviceTarget
                .path("mealplan")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(MealPlan.class);
    }

    public GroceryList getGroceryListByUserId(String userId) {
        return serviceTarget
                .path("grocerylist")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .get(GroceryList.class);
    }

    public boolean checkMealPlan(String userId) {
        return serviceTarget
                .path("meaplan")
                .path("check")
                .path(userId)
                .request(MediaType.TEXT_PLAIN)
                .get(boolean.class);
    }

    // Generate a meal plan for a specific userId
    public MealPlan generateMealPlan(String userId) {
        return serviceTarget
                .path("mealplan")
                .path("generate")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .post(null, MealPlan.class);
    }

    public MealPlan regenerateMealPlan(String userId) {
        return serviceTarget
                .path("mealplan")
                .path("generate")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(userId, MediaType.TEXT_PLAIN), MealPlan.class);
    }

    public boolean checkGroceryList(String userId) {
        return serviceTarget
                .path("grocerylist")
                .path("check")
                .path(userId)
                .request(MediaType.TEXT_PLAIN)
                .get(boolean.class);
    }

    // Generate a grocery list for a specific userId
    public GroceryList generateGroceryList(String userId) {
        return serviceTarget
                .path("grocerylist")
                .path("generate")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .post(null, GroceryList.class);
    }

    public GroceryList regenerateGroceryList(String userId) {
        return serviceTarget
                .path("grocerylist/generate")
                .path(userId)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(userId, MediaType.TEXT_PLAIN), GroceryList.class);
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
