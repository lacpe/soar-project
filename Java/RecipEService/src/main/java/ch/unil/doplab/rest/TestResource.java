package ch.unil.doplab.rest;

import ch.unil.doplab.UserProfile;
import ch.unil.doplab.domain.ApplicationState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

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
