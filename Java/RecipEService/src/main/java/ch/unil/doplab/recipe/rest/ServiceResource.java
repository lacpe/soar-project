package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import ch.unil.doplab.recipe.domain.GroceryList;
import ch.unil.doplab.recipe.domain.MealPlan;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/service")
public class ServiceResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/populateDB")
    public Response populateDB() {
        state.populateDb();
        return Response.ok("StudyBuddy database was populated at " + LocalDateTime.now()).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/clearDB")
    public Response clearDB() {
        try {
            state.clearDb();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        state.clearDb();
        return Response.ok("StudyBuddy database was cleared at " + LocalDateTime.now()).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/populateApp")
    public Response populateApp() {
        state.populateApplication();
        return Response.ok("Application was populated at " + LocalDateTime.now()).build();
    }

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
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/mealplan/check/{userId}")
    public boolean checkMealPlan(@PathParam("userId") UUID userId) {
        return state.checkUserMealPlan(userId);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/grocerylist/check/{userId}")
    public boolean checkGroceryList(@PathParam("userId") UUID userId) {
        return state.checkUserGroceryList(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/authenticate/{user}/{password}")
    public UUID authenticate(@PathParam("user") String username, @PathParam("password") String password) {
        return state.authenticateUser(username, password);
    }
}
