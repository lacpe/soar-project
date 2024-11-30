package ch.unil.doplab.recipe.ui;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import ch.unil.doplab.recipe.domain.Meal;
import ch.unil.doplab.recipe.domain.NutritionalInfo;

@Named
@RequestScoped
public class RecipeDetailBean implements Serializable {

    private Meal currentRecipe;

    public RecipeDetailBean() {
        // Retrieve the meal ID from the query parameter
        String mealIdParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

        if (mealIdParam != null) {
            try {
                int mealId = Integer.parseInt(mealIdParam);

                // Fetch the meal from MealPlanBean
                MealPlanBean mealPlanBean = (MealPlanBean) FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .get("mealPlanBean");

                if (mealPlanBean != null) {
                    this.currentRecipe = mealPlanBean.getMealPlan().getMealById(mealId);
                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid meal ID: " + mealIdParam);
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
}
