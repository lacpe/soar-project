import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@SessionScoped
public class MealPlanBean implements Serializable {

    private APIHandler apiHandler = new APIHandler(); // Use your existing APIHandler class
    private MealPlan mealPlan; // The generated meal plan
    private Recipe selectedRecipe; // The selected recipe details

    @PostConstruct
    public void init() {
        // Optionally generate a default meal plan on initialization
        generateMealPlan(new UserProfile());
    }

    // Generate a meal plan based on user preferences
    public void generateMealPlan() {
        System.out.println("Generate New Meal Plan button clicked!");
        UserProfile userProfile = new UserProfile();
        userProfile.setMealPlanPreference(MealPlanPreference.WEEK); // Example preference
        userProfile.setDesiredServings(2); // Example servings

    // Use APIHandler to fetch the meal plan
        this.mealPlan = apiHandler.generateMealPlan(userProfile);
    }

    // View recipe details for a specific meal
    public String viewRecipe(int mealId) {
        // Use the meal details from the meal plan
        Meal meal = mealPlan.getMealById(mealId);

        if (meal != null) {
            // Populate the selected recipe using the meal data
            this.selectedRecipe = new Recipe(
                meal.getId(),
                meal.getName(),
                meal.getImage(),
                meal.getInstructions(),
                meal.getIngredients(),
                meal.getNutritionalInfo()
            );
        }

        // Navigate to the recipe details page
        return "recipedetails.xhtml";
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public Recipe getSelectedRecipe() {
        return selectedRecipe;
    }
}
