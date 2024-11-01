package ch.unil.doplab.domain;

import ch.unil.doplab.Ingredient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;

public class ApplicationState {
    private HashMap<String, Ingredient> ingredients;
}
