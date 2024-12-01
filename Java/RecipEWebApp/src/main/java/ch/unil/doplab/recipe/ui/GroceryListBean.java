package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.domain.GroceryList;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class GroceryListBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private MealPlanBean mealPlanBean;

    // Fetch the grocery list directly from MealPlanBean
    public GroceryList getGroceryList() {
        GroceryList groceryList = mealPlanBean.getGroceryList();
        if (groceryList == null) {
            System.err.println("Grocery list is null in GroceryListBean.");
        } else if (groceryList.getIngredientsByAisle() == null || groceryList.getIngredientsByAisle().isEmpty()) {
            System.err.println("Grocery list ingredients are empty.");
        } else {
            System.out.println("Grocery list successfully fetched with " + groceryList.getIngredientsByAisle().size() + " aisles.");
        }
        return groceryList;
    }
}
