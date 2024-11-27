package ch.unil.doplab.recipe.ui;

import ch.unil.doplab.recipe.domain.GroceryList;
import ch.unil.doplab.recipe.RecipEService;
import java.io.Serializable;

import ch.unil.doplab.recipe.domain.Ingredient;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@SessionScoped
public class GroceryListBean extends GroceryList implements Serializable {
    private static final long serialVersionUID = 1L;
    private GroceryList theList;

    @Inject
    RecipEService recipEService;

    public GroceryListBean() {
        this(null, null);
    }

    public GroceryListBean(UUID groceryListId, Map<String, List<Ingredient>> ingredientsByAisle) {
        theList = new GroceryList(groceryListId, ingredientsByAisle);
    }

    public void init() {
        theList = null;
    }

}
