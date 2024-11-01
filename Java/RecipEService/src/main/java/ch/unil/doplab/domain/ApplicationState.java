package ch.unil.doplab.domain;

import ch.unil.doplab.Ingredient;
import ch.unil.doplab.Meal;
import ch.unil.doplab.MealPlan;
import ch.unil.doplab.UserProfile;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.UUID;

public class ApplicationState {
    private HashMap<UUID, UserProfile> userProfiles;
    private HashMap<UUID, MealPlan> mealPlans;
    private HashMap<UUID, Meal> meals;
    private HashMap<UUID, Ingredient> ingredients;

    @PostConstruct
    public void init() {
        userProfiles = new HashMap<>();
        mealPlans = new HashMap<>();
        meals = new HashMap<>();
        ingredients = new HashMap<>();
    }


}
