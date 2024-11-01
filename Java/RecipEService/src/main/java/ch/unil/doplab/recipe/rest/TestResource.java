package ch.unil.doplab.recipe.rest;

import ch.unil.doplab.recipe.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {
    @Inject
    private ApplicationState state;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Yeah";
    }
}
