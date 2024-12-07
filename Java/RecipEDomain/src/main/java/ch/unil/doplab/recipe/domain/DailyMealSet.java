package ch.unil.doplab.recipe.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
public class DailyMealSet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", updatable = false, nullable = false)
    private UUID mealSetUUID;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Meal> meals;

    public DailyMealSet() {
        this(new ArrayList<>());
    }

    public DailyMealSet(List<Meal> meals) {
        this.meals = meals;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
}
