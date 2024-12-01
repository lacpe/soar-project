package ch.unil.doplab.recipe.ui;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import ch.unil.doplab.recipe.domain.Meal;
import ch.unil.doplab.recipe.domain.NutritionalInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Named
@RequestScoped
public class RecipeDetailBean implements Serializable {

    private Meal currentRecipe;

    public RecipeDetailBean() {
        String mealIdParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        System.out.println("Received meal ID parameter: " + mealIdParam);

        if (mealIdParam != null) {
            try {
                int mealId = Integer.parseInt(mealIdParam);

                // Retrieve MealPlanBean from session
                MealPlanBean mealPlanBean = (MealPlanBean) FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .get("mealPlanBean");

                if (mealPlanBean != null && mealPlanBean.getMealPlan() != null) {
                    // Fetch the meal by ID
                    this.currentRecipe = mealPlanBean.getMealPlan().getMealById(mealId);
                    System.out.println("Retrieved meal: " + (currentRecipe != null ? currentRecipe.getTitle() : "Not Found"));
                    if (currentRecipe != null && currentRecipe.getInstructions() != null) {
                        System.out.println("Instructions: " + currentRecipe.getInstructions());
                    } else {
                        System.out.println("Instructions are null or empty.");
                    }
                } else {
                    System.err.println("MealPlanBean or MealPlan is null.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid meal ID format: " + mealIdParam);
            }
        }
    }

    public Meal getCurrentRecipe() {
        return currentRecipe;
    }

    public NutritionalInfo getNutritionalInfo() {
        if (currentRecipe != null) {
            return currentRecipe.getNutritionalInfo();
        }
        return null;
    }
    public List<String> getInstructions() {
        if (currentRecipe != null && currentRecipe.getInstructions() != null) {
            System.out.println("Instructions retrieved: " + currentRecipe.getInstructions());
            return currentRecipe.getInstructions();
        }
        System.out.println("No instructions available for currentRecipe.");
        return Collections.singletonList("No instructions available.");
    }
}
