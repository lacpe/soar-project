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
    private GroceryList groceryList = null;

    @Inject
    private MealPlanBean mealPlanBean;

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }

    // Fetch the grocery list directly from MealPlanBean
    public GroceryList getGroceryList() {
        if (this.groceryList == null) {
            System.err.println("Grocery list is null in GroceryListBean.");
        } else if (this.groceryList.getIngredientsByAisle() == null || this.groceryList.getIngredientsByAisle().isEmpty()) {
            System.err.println("Grocery list ingredients are empty.");
        } else {
            System.out.println("Grocery list successfully fetched with " + this.groceryList.getIngredientsByAisle().size() + " aisles.");
        }
        return this.groceryList;
    }
}
