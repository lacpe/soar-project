package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import ch.unil.doplab.recipe.domain.GroceryList;
import ch.unil.doplab.recipe.domain.MealPlan;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}/mealplan")
    public MealPlan getUserMealPlan(@PathParam("userId") UUID userId) {
        return state.getUserMealPlan(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}/grocerylist")
    public GroceryList getUserGroceryList(@PathParam("userId") UUID userId) {
        return state.getUserGroceryList(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}/mealplan/generate")
    public MealPlan generateUserMealPlan(@PathParam("userId") UUID userId) {
        return state.generateMealPlan(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}/grocerylist/generate")
    public GroceryList generateUserGroceryList(@PathParam("userId") UUID userId) {
        return state.generateGroceryList(userId);
    }
}
