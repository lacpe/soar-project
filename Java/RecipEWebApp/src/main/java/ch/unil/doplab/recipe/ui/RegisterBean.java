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

        // Create a new UserProfile using the existing constructor
        UserProfile newUser = new UserProfile(
                UUID.randomUUID(), // Generate a UUID for the user
                email,             // Use email as the username
                password,          // Password will be hashed in UserProfile constructor
                null,              // No diet type by default
                new HashSet<>(),   // Empty set for allergies
                new HashSet<>(),   // Empty set for disliked ingredients
                0,                 // No daily calorie target
                UserProfile.MealPlanPreference.DAY // Default meal plan preference
        );

        boolean success = recipEService.registerUser(newUser);

        if (success) {
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
