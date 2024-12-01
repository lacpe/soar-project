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
    @Path("/mealplan/{userId}")
    public MealPlan getUserMealPlan(@PathParam("userId") UUID userId) {
        return state.getUserMealPlan(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/grocerylist/{userId}")
    public GroceryList getUserGroceryList(@PathParam("userId") UUID userId) {
        return state.getUserGroceryList(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/mealplan/generate/{userId}")
    public MealPlan generateUserMealPlan(@PathParam("userId") UUID userId) {
        return state.generateMealPlan(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/grocerylist/generate/{userId}")
    public GroceryList generateUserGroceryList(@PathParam("userId") UUID userId) {
        return state.generateGroceryList(userId);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/mealplan/generate/{userId}")
    public MealPlan regenerateUserMealPlan(@PathParam("userId") UUID userId) {
        MealPlan mealPlan = state.getUserMealPlan(userId);
        state.removeMealPlan(mealPlan.getMealPlanId());
        return state.generateMealPlan(userId);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/grocerylist/generate/{userId}")
    public GroceryList regenerateUserGroceryList(@PathParam("userId") UUID userId) {
        GroceryList groceryList = state.getUserGroceryList(userId);
        state.removeGroceryList(groceryList.getGroceryListId());
        return state.generateGroceryList(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/authenticate/{user}/{password}")
    public UUID authenticate(@PathParam("user") String username, @PathParam("password") String password) {
        return state.authenticateUser(username, password);
    }
}
