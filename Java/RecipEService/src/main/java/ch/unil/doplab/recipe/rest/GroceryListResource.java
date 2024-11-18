package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import ch.unil.doplab.recipe.domain.GroceryList;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;

@Path("/grocerylist")
public class GroceryListResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<UUID, GroceryList> getAllGroceryLists() {
        return state.getAllGroceryLists();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public GroceryList getGroceryList(@PathParam("id") UUID id) {
        return state.getGroceryList(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GroceryList addGroceryList(GroceryList groceryList) {
        return state.addGroceryList(groceryList);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public GroceryList setGroceryList(@PathParam("id") UUID id, GroceryList groceryList) {
        return state.setGroceryList(id, groceryList);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean removeGroceryList(@PathParam("id") UUID id) {
        return state.removeGroceryList(id);
    }
}
