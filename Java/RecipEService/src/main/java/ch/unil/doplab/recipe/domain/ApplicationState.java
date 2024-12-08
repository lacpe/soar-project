package ch.unil.doplab.recipe.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

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

    // To generate random users
    private Random RANDOM = new Random();

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init() {
        userProfiles = new TreeMap<>();
        mealPlans = new TreeMap<>();
        groceryLists = new TreeMap<>();

        apiHandler = new APIHandler();

        usersMealPlans = HashBiMap.create();
        mealPlansGroceryLists = HashBiMap.create();

        usernames = new TreeMap<>();

        var allUserProfiles = findAllUserProfiles();
        for (UserProfile userProfile : allUserProfiles) {
            userProfiles.put(userProfile.getUserId(), userProfile);
            usernames.put(userProfile.getUsername(), userProfile.getUserId());
        }
        var allMealPlans = findAllMealPlans();
        for (MealPlan mealPlan : allMealPlans) {
            mealPlans.put(mealPlan.getMealPlanId(), mealPlan);
            usersMealPlans.put(mealPlan.getUserId(), mealPlan.getMealPlanId());
        }
        var allGroceryLists = findAllGroceryLists();
        for (GroceryList groceryList : allGroceryLists) {
            groceryLists.put(groceryList.getGroceryListId(), groceryList);
            mealPlansGroceryLists.put(groceryList.getMealPlanId(), groceryList.getGroceryListId());
        }
    }

    // Functions for UserProfileResource
    public UserProfile getUserProfile(UUID id) {return userProfiles.get(id);}

    public Map<UUID, UserProfile> getAllUserProfiles() {return userProfiles;}

    @Transactional
    public UserProfile addUserProfile(UserProfile userProfile) {
        if (userProfile.getUserId() != null) {
            return addUserProfile(userProfile.getUserId(), userProfile);
        }
        return addUserProfile(UUID.randomUUID(), userProfile);
    }

    @Transactional
    public UserProfile addUserProfile(UUID id, UserProfile userProfile) {
        if (userProfile.getUsername() == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        if (userProfiles.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " already exists.");
        }
        if (usernames.containsKey(userProfile.getUsername())) {
            throw new IllegalArgumentException("Username " + userProfile.getUsername() + " already in use.");
        }
        userProfile.setUserId(id);
        userProfiles.put(id, userProfile);
        usernames.put(userProfile.getUsername(), id);
        em.persist(userProfile);
        return userProfile;
    }

    @Transactional
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
        em.merge(userProfile);
        return userProfiles.get(id);
    }

    @Transactional
    public void removeUserProfile(String username){
        removeUserProfile(usernames.get(username));
    }

    @Transactional
    public boolean removeUserProfile(UUID userId) {
        UserProfile userProfile = userProfiles.get(userId);
        UUID mealPlanId = usersMealPlans.get(userId);
        UUID groceryListId = mealPlansGroceryLists.get(mealPlanId);
        if (userProfile == null) {
            return false;
        }
        // Removes associated meal plan & grocery list (does nothing if there are none)
        if (groceryListId != null) {
            groceryLists.remove(groceryListId);
        }
        if (mealPlanId != null) {
            mealPlansGroceryLists.remove(mealPlanId);
            mealPlans.remove(mealPlanId);
        }
        usersMealPlans.remove(userId);
        // Removes user
        usernames.remove(userProfile.getUsername());
        userProfiles.remove(userId);
        if (!em.contains(userProfile)) {
            userProfile = em.merge(userProfile);
        }
        em.remove(userProfile);
        return true;
    }

    // Functions for MealPlanResource

    public MealPlan getMealPlan(UUID id) {
        return mealPlans.get(id);
    }

    public Map<UUID, MealPlan> getAllMealPlans() {
        return mealPlans;
    }

    @Transactional
    public MealPlan addMealPlan(MealPlan mealPlan) {
        if (mealPlan.getMealPlanId() != null) {
            return addMealPlan(mealPlan.getMealPlanId(), mealPlan);
        }
        return addMealPlan(UUID.randomUUID(), mealPlan);
    }

    @Transactional
    public MealPlan addMealPlan(UUID id, MealPlan mealPlan) {
        mealPlan.setMealPlanId(id);
        if (mealPlans.containsKey(id)) {
            throw new IllegalArgumentException("A meal plan with id " + id + " already exists.");
        }
        mealPlans.put(id, mealPlan);
        em.persist(mealPlan);
        return mealPlan;
    }

    @Transactional
    public MealPlan setMealPlan(UUID id, MealPlan mealPlan) {
        mealPlans.replace(id, mealPlan);
        em.merge(mealPlan);
        return mealPlans.get(id);
    }

    @Transactional
    public boolean removeMealPlan(UUID id) {
        MealPlan mealPlan = mealPlans.get(id);
        UUID groceryListId = mealPlansGroceryLists.get(id);
        if (mealPlan == null) {
            return false;
        }
        if (groceryListId != null) {
            // Removes associated grocery list, does nothing if there is none
            groceryLists.remove(groceryListId);
        }
        mealPlansGroceryLists.remove(id);
        // Removes meal plan
        mealPlans.remove(id);
        if (!em.contains(mealPlan)) {
            mealPlan = em.merge(mealPlan);
        }
        em.remove(mealPlan);
        return true;
    }

    // Functions for GroceryListResource
    public GroceryList getGroceryList(UUID id) {return groceryLists.get(id);}

    public Map<UUID, GroceryList> getAllGroceryLists() {return groceryLists;}

    @Transactional
    public GroceryList addGroceryList(GroceryList groceryList) {
        if (groceryList.getGroceryListId() != null) {
            return addGroceryList(groceryList.getGroceryListId(), groceryList);
        }
        return addGroceryList(UUID.randomUUID(), groceryList);
    }

    @Transactional
    public GroceryList addGroceryList(UUID id, GroceryList groceryList) {
        groceryList.setGroceryListId(id);
        if (groceryLists.containsKey(id)) {
            throw new IllegalArgumentException("A grocery list with id " + id + " already exists.");
        }
        groceryLists.put(id, groceryList);
        em.persist(groceryList);
        return groceryList;
    }

    public GroceryList setGroceryList(UUID id, GroceryList groceryList) {
        groceryLists.replace(id, groceryList);
        em.merge(groceryList);
        return groceryList;
    }

    @Transactional
    public boolean removeGroceryList(UUID id) {
        GroceryList groceryList = groceryLists.get(id);
        if (groceryList == null) {
            return false;
        }
        mealPlansGroceryLists.inverse().remove(id);
        groceryLists.remove(id);
        mealPlans.remove(id);
        if (!em.contains(groceryList)) {
            groceryList = em.merge(groceryList);
        }
        em.remove(groceryList);
        return true;
    }

    //Functions for ServiceResource
    @Transactional
    public MealPlan generateMealPlan(UUID userId) {
        UserProfile userProfile = userProfiles.get(userId);
        if (userProfile == null) {
            throw new IllegalArgumentException("No user profile with ID " + userId);
        }
        if (usersMealPlans.containsKey(userId)) {
            throw new IllegalArgumentException("Another meal plan for user " + userProfile.getUsername() + " already exists.");
        }
        MealPlan mealPlan = apiHandler.generateMealPlan(userProfile);
        mealPlan.setUserId(userId);
        addMealPlan(mealPlan);
        usersMealPlans.put(userId, mealPlan.getMealPlanId());
        return mealPlan;
    }

    @Transactional
    public GroceryList generateGroceryList(UUID userId) {
        MealPlan mealPlan = mealPlans.get(usersMealPlans.get(userId));
        if (mealPlan == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a registered meal plan.");
        }
        GroceryList groceryList = apiHandler.generateConsolidatedShoppingList(mealPlan.getAllMeals());
        groceryList.setMealPlanId(mealPlan.getMealPlanId());
        addGroceryList(groceryList);
        mealPlansGroceryLists.put(usersMealPlans.get(userId), groceryList.getGroceryListId());
        return groceryList;
    }

    public MealPlan getUserMealPlan(UUID userId) {
        // Returns a given user's current meal plan
        return mealPlans.get(usersMealPlans.get(userId));
    }

    public GroceryList getUserGroceryList(UUID userId) {
        // Looks up a user's current meal plan, and returns the corresponding grocery list
        return groceryLists.get(mealPlansGroceryLists.get(usersMealPlans.get(userId)));
    }

    public UUID authenticateUser(String username, String password) {
        if (!usernames.containsKey(username)) {
            throw new IllegalArgumentException("No user with name " + username + " exists.");
        }
        UUID userId = usernames.get(username);
        UserProfile userProfile = userProfiles.get(userId);
        if (Utils.checkPassword(password, userProfile.getPassword())) {
            return userId;
        }
        else {
            return null;
        }
    }

    public List<UserProfile> findAllUserProfiles() {
        return em.createQuery("SELECT u FROM UserProfile u", UserProfile.class).getResultList();
    }

    public List<MealPlan> findAllMealPlans() {
        return em.createQuery("SELECT m FROM MealPlan m", MealPlan.class).getResultList();
    }

    public List<GroceryList> findAllGroceryLists() {
        return em.createQuery("SELECT m FROM GroceryList m", GroceryList.class).getResultList();
    }

    public void clearObjects() {
        userProfiles.clear();
        mealPlans.clear();
        groceryLists.clear();
        usersMealPlans.clear();
        mealPlansGroceryLists.clear();
        usernames.clear();
    }

    public void clearTables() {
        // Disable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Get all table names in the studybuddy schema
        List<String> tables = (List<String>) em
                .createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'RecipE'")
                .getResultList();

        // Avoid removing the sequence table
        tables.remove("SEQUENCE");

        // Truncate each table
        for (String table : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
        }

        // Re-enable foreign key checks
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    @Transactional
    public void clearDb() {
        clearTables();
        clearObjects();
    }


    @Transactional
    public void populateDb() {
        for (UserProfile user : userProfiles.values()) {
            em.persist(user);
        }
    }

    @Transactional
    private void populateApplication() {
        // Create 20 fake user profiles
        for (int i = 1; i <= 20; i++) {
            String username = "user" + i;
            String password = "password" + i + "123";
            UserProfile user = new UserProfile(UUID.randomUUID(), username, password, Utils.getRandomDietType(),
                    new HashSet<>(), new HashSet<>(), RANDOM.nextInt(500) + 1500, Utils.getRandomMealPlanPreference());
            addUserProfile(user);
        }
    }

}
