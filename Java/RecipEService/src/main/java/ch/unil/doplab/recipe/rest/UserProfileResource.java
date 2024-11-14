package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import ch.unil.doplab.recipe.domain.UserProfile;
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
        return state.setUserProfile(id, userProfile);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserProfile createUserProfile(UserProfile userProfile) {
        return state.addUserProfile(userProfile);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public boolean deleteUserProfile(@PathParam("id") UUID id) {
        return state.removeUserProfile(id);
    }
}
