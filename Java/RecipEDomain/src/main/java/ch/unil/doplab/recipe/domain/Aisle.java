package ch.unil.doplab.recipe.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Aisle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", updatable = false, nullable = false)
    private UUID aisleId;
    @ElementCollection
    private List<Ingredient> aisle;

    public Aisle() {
        this(new ArrayList<>());
    }

    public Aisle(List<Ingredient> aisle) {
        this.aisle = aisle;
    }

    public UUID getAisleId() {
        return aisleId;
    }

    public void setAisleId(UUID aisleId) {
        this.aisleId = aisleId;
    }

    public List<Ingredient> getAisle() {
        return aisle;
    }

    public void setAisle(List<Ingredient> aisle) {
        this.aisle = aisle;
    }

}
