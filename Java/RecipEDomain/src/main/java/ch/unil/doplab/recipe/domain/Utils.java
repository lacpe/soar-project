package ch.unil.doplab.recipe.domain;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Random;


public class Utils {
    private static final Random RANDOM = new Random();

    //Random diet type for user generation & application state population purposes
    //returns a random value from DietType
    public static UserProfile.DietType getRandomDietType() {
        return UserProfile.DietType.values()[RANDOM.nextInt(UserProfile.DietType.values().length)];
    }

    //Random meal plan preference (daily or weekly) for user generation & application state population purposes
    //returns a random value from MealPlanPreference
    public static UserProfile.MealPlanPreference getRandomMealPlanPreference() {
        return UserProfile.MealPlanPreference.values()[RANDOM.nextInt(UserProfile.MealPlanPreference.values().length)];
    }

    // Function to hash a plaintext password, borrowed from the StudyBuddy model
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Function to compare a plaintext input to a hashed password, also borrowed from the StudyBuddy model
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
