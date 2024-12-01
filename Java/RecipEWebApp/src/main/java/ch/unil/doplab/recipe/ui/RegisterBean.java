package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.domain.UserProfile;
import ch.unil.doplab.recipe.RecipEService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.HashSet;
import java.util.UUID;

@RequestScoped
@Named
public class RegisterBean {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

    @Inject
    private RecipEService recipEService;

    public String register() {
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match", null));
            return null; // Stay on the same page
        }

        /* Create a new UserProfile using the existing constructor, parameters as follows
        Generate a UUID for the user
        Use email as the username
        Password will be hashed in UserProfile constructor
        No diet type by default
        Empty set for allergies
        Empty set for disliked ingredients
        No daily calorie target
        Default meal plan preference */

        UserProfile newUser = recipEService.addUser(new UserProfile(UUID.randomUUID(), email, password, null, new HashSet<>(), new HashSet<>(), 0, UserProfile.MealPlanPreference.DAY));

        if (newUser != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful", null));
            return "Login"; // Redirect to login page
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email already exists", null));
            return null; // Stay on the same page
        }
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
