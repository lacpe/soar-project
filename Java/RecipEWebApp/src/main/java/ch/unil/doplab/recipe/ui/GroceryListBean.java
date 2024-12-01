package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.domain.APIHandler;
import ch.unil.doplab.recipe.domain.GroceryList;
import ch.unil.doplab.recipe.RecipEService;
import java.io.Serializable;

import ch.unil.doplab.recipe.domain.Ingredient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.*;

@Named
@SessionScoped
public class GroceryListBean extends GroceryList implements Serializable {
    private static final long serialVersionUID = 1L;
    private GroceryList groceryList;
    private APIHandler apiHandler = new APIHandler();
    private boolean useMockData = true;

    @Inject
    RecipEService recipEService;

    @Inject
    MealPlanBean mealPlanBean;

    public GroceryListBean() {
        this(null, null);
    }

    public GroceryListBean(UUID groceryListId, Map<String, List<Ingredient>> ingredientsByAisle) {
        groceryList = new GroceryList(groceryListId, ingredientsByAisle);
    }

    @PostConstruct
    public void init() {
        mealPlanBean.init();
        if (useMockData) {
            groceryList = generateMockGroceryList();
        }
        else {
            groceryList = generateGroceryList();
        }
    }

    public GroceryList generateGroceryList() {
        return apiHandler.generateConsolidatedShoppingList(mealPlanBean.getMealPlan().getAllMeals());
    }

    public GroceryList generateMockGroceryList() {
        Map<String, List<Ingredient>> ingredientsByAisle = new HashMap<>();
        // Generating a "Feculents" aisle
        List<Ingredient> feculents = new ArrayList<>();
        feculents.add(new Ingredient("Rice", 200d, "grams"));
        feculents.add(new Ingredient("Bread", 100d, "grams"));
        feculents.add(new Ingredient("Pasta", 100d, "grams"));
        feculents.get(0).setInStock(true);
        ingredientsByAisle.put("Feculents", feculents);
        // Generating a "Drinks" aisle
        List<Ingredient> drinks = new ArrayList<>();
        drinks.add(new Ingredient("Diet Coke", 1d, "L"));
        drinks.add(new Ingredient("Milk", 500, "mL"));
        ingredientsByAisle.put("Drinks", drinks);
        return new GroceryList(ingredientsByAisle);
    }

    public GroceryList getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }
}
