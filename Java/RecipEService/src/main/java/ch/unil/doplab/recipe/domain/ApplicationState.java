package ch.unil.doplab.recipe.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.attribute.UserPrincipal;
import java.util.*;

@ApplicationScoped
public class ApplicationState {
    // Maps that will hold all CRUD-able items.
    private Map<UUID, UserProfile> userProfiles;
    private Map<UUID, MealPlan> mealPlans;
    private Map<UUID, GroceryList> groceryLists;

    // Object to handle all interactions with the Spoonacular API.
    private APIHandler apiHandler;

    /* Bidirectional maps (from Google's Guava Collections API) to match users to meal plans, and meal
    plans to grocery lists, and vice versa. */
    private BiMap<UUID, UUID> usersMealPlans;
    private BiMap<UUID, UUID> mealPlansGroceryLists;

    // Map to match usernames to their UUID. This simplifies API requests concerning users.
    private Map<String, UUID> usernames;

    @PostConstruct
    public void init() {
        userProfiles = new TreeMap<>();
        mealPlans = new TreeMap<>();
        groceryLists = new TreeMap<>();

        apiHandler = new APIHandler();

        usersMealPlans = HashBiMap.create();
        mealPlansGroceryLists = HashBiMap.create();

        usernames = new TreeMap<>();

        populateApplication();
    }

    // In order : getters, add operations, remove operations
    public UserProfile getUserProfile(UUID id) {return userProfiles.get(id);}

    public Map<UUID, UserProfile> getAllUserProfiles() {return userProfiles;}

    public UserProfile addUserProfile(UserProfile userProfile) {
        if (userProfile.getUserId() != null) {
            return addUserProfile(userProfile.getUserId(), userProfile);
        }
        return addUserProfile(UUID.randomUUID(), userProfile);
    }

    public UserProfile addUserProfile(UUID id, UserProfile userProfile) {
        if (userProfile.getUsername() == null | userProfile.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (userProfiles.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " already exists.");
        }
        if (usernames.containsKey(userProfile.getUsername())) {
            throw new IllegalArgumentException("Username " + userProfile.getUsername() + " already in use.");
        }
        userProfiles.put(id, userProfile);
        usernames.put(userProfile.getUsername(), id);
        return userProfile;
    }

    public UserProfile setUserProfile(UUID id, UserProfile userProfile) {
        // This function changes the data to whatever userProfile is saved at UUID id to whatever data
        // is in userProfile
        UserProfile oldUserProfile = userProfiles.get(id);
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile inputed is null.");
        }
        if (oldUserProfile == null) {
            throw new IllegalArgumentException("User profile does not currently exist, cannot be updated.");
        }
        // Translating the following condition : if the old & new user profiles have the same (non-null) username
        // but their UUIDs are different (i.e. you are trying to set the same username for two different users
        // then throw an exception
        if (oldUserProfile.getUsername().equals(userProfile.getUsername())
                && oldUserProfile.getUsername() != null
                && !usernames.get(userProfile.getUsername()).equals(id)) {
            throw new IllegalArgumentException("A user with username " + userProfile.getUsername() + " already exists.");
        }
        oldUserProfile.replaceWithUser(userProfile);
        return userProfiles.get(id);
    }

    public void removeUserProfile(String username){
        removeUserProfile(usernames.get(username));
    }

    public boolean removeUserProfile(UUID id) {
        UserProfile userProfile = userProfiles.get(id);
        if (userProfile == null) {
            return false;
        }
        if (usernames.containsKey(userProfile.getUsername())) {
            usernames.remove(userProfile.getUsername());
        }
        if (usersMealPlans.containsKey(id)) {
            usersMealPlans.remove(id);
        }
        userProfiles.remove(id);
        return true;
    }

    public MealPlan getMealPlan(UUID id) {
        return mealPlans.get(id);
    }

    public Map<UUID, MealPlan> getAllMealPlans() {
        return mealPlans;
    }

    public MealPlan addMealPlan(MealPlan mealPlan) {
        return addMealPlan(UUID.randomUUID(), mealPlan);
    }

    public MealPlan addMealPlan(UUID id, MealPlan mealPlan) {
        mealPlans.put(id, mealPlan);
        return mealPlan;
    }

    public MealPlan setMealPlan(UUID id, MealPlan mealPlan) {
        mealPlans.replace(id, mealPlan);
        return mealPlan;
    }

    public boolean removeMealPlan(UUID id) {
        MealPlan mealPlan = mealPlans.get(id);
        if (mealPlan == null) {
            return false;
        }
        if (mealPlansGroceryLists.containsKey(id)) {
            mealPlansGroceryLists.remove(id);
        }
        mealPlans.remove(id);
        return true;
    }

    public GroceryList getGroceryList(UUID id) {return groceryLists.get(id);}

    public Map<UUID, GroceryList> getAllGroceryLists() {return groceryLists;}

    public GroceryList addGroceryList(GroceryList groceryList) {
        groceryLists.put(UUID.randomUUID(), groceryList);
        return groceryList;
    }

    public GroceryList setGroceryList(UUID id, GroceryList groceryList) {
        groceryLists.replace(id, groceryList);
        return groceryList;
    }

    public boolean removeGroceryList(UUID id) {
        GroceryList groceryList = groceryLists.get(id);
        if (groceryList == null) {
            return false;
        }
        if (mealPlansGroceryLists.inverse().containsKey(id)) {
            mealPlansGroceryLists.inverse().remove(id);
        }
        groceryLists.remove(id);
        return true;
    }

    // A few utility functions to make UX nicer

    private void populateApplication() {
        // Create 20 fake user profiles
        for (int i = 1; i <= 20; i++) {
            String username = "user" + i;
            String password = "password" + i + "123";
            UserProfile user = new UserProfile(UUID.randomUUID(), username, password);
            addUserProfile(user);
        }

    }

}
