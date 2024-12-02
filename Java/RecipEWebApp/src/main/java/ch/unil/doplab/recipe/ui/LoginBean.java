package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.RecipEService;
import ch.unil.doplab.recipe.domain.UserProfile;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

@SessionScoped
@Named
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    @Inject
    private RecipEService recipEService; // Assuming RecipEService has authentication logic

    private UserProfile loggedInUser;
    @Named
    @Inject
    private UserProfileBean userProfileBean;
    @Named
    @Inject
    private MealPlanBean mealPlanBean;

    public LoginBean() {
        reset();
    }

    public void reset() {
        email = null;
        password = null;
        loggedInUser = null;
    }

    public String login() {
        // Authenticate the user
        var user = recipEService.getUserProfile(recipEService.authenticateUser(email, password).toString());
        var session = getSession(true);

        if (user != null) {
            session.setAttribute("email", email);
            session.setAttribute("user", user);
            loggedInUser = user;
            userProfileBean.loadUserProfile(user.getUserId());
            return "userprofile"; // Redirect to user profile
        }

        // Login failed
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login", null));
        reset();
        return "Login";
    }

    public String logout() {
        invalidateSession();
        reset();
        return "Login.xhtml?faces-redirect=true";
    }

    public static HttpSession getSession(boolean create) {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        var externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return null;
        }
        return (HttpSession) externalContext.getSession(create);
    }

    public static void invalidateSession() {
        var session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // Getters and Setters
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

    public UserProfile getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserProfile loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
