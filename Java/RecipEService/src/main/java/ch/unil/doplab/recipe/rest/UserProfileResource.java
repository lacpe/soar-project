package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.UserProfile;
import ch.unil.doplab.recipe.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("/userprofile")
public class UserProfileResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<UUID, UserProfile> getAllUserProfiles() {
        return new TreeMap<UUID, UserProfile>(state.getAllUserProfiles());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public UserProfile getUserProfile(@PathParam("id") UUID id) {
        return state.getUserProfile(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public UserProfile updateUserProfile(@PathParam("id") UUID id, UserProfile userProfile) {
        state.setUserProfile(id, userProfile);
        return state.getUserProfile(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public UserProfile createUserProfile(@PathParam("id") UUID id, UserProfile userProfile) {
        return state.addUserProfile(id, userProfile);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void deleteUserProfile(@PathParam("id") UUID id) {
        state.removeUserProfile(id);
    }
}
