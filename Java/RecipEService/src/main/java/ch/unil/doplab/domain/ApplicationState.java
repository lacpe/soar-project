package ch.unil.doplab.domain;

import ch.unil.doplab.Ingredient;
import ch.unil.doplab.Meal;
import ch.unil.doplab.MealPlan;
import ch.unil.doplab.UserProfile;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@ApplicationScoped
public class ApplicationState {
    private Map<UUID, UserProfile> userProfiles;


    @PostConstruct
    public void init() {
        userProfiles = new TreeMap<>();
    }

    public UserProfile addUserProfile(UserProfile userProfile) {
        if (userProfile.getUserId() != null) {
            return addUserProfile(userProfile.getUserId(), userProfile);
        }
        return addUserProfile(UUID.randomUUID(), userProfile);
    }

    public UserProfile addUserProfile(UUID id, UserProfile userProfile) {
        userProfiles.put(id, userProfile);
        return userProfile;
    }

    public UserProfile getUserProfile(UUID id) {return userProfiles.get(id);}

    public Map<UUID, UserProfile> getAllUserProfiles() {return userProfiles;}
}
