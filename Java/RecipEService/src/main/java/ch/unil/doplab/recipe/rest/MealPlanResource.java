package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import ch.unil.doplab.recipe.domain.MealPlan;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("/mealplan")
public class MealPlanResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<UUID, MealPlan> getAllMealPlans() {
        return state.getAllMealPlans();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public MealPlan getMealPlan(@PathParam("id") UUID id) {
        return state.getMealPlan(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public MealPlan setMealPlan(@PathParam("id") UUID id, MealPlan mealPlan) {
        return state.setMealPlan(id, mealPlan);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public MealPlan addMealPlan(@PathParam("id") UUID id, MealPlan mealPlan) {
        return state.addMealPlan(id, mealPlan);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeMealPlan(@PathParam("id") UUID id) {
        return state.removeMealPlan(id);
    }
}
