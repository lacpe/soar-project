package ch.unil.doplab.recipe.domain;
import jakarta.persistence.*;

// Class representing an Ingredient with properties like name, quantity, unit, and additional metadata.
@Embeddable
public class Ingredient {
    // Basic attributes for the ingredient's identity and quantity
    private String name; // Name of the ingredient
    private double quantity; // Quantity of the ingredient
    private String unit; // Unit of measurement for the ingredient
    // Additional fields to enrich the ingredient information
    private String imageUrl;            // New field for ingredient image URL
    private String description;         // New field for a brief description
    private NutritionalInfo nutritionalInfo; // New field for nutritional details
    // Field to keep track of whether the ingredient is already in stock or not
    private boolean inStock = false;

    public Ingredient() {
        this(null, 0.0, null);
    }

    // Constructor to create an Ingredient object with essential fields
    public Ingredient(String name, double quantity, String unit) {
        this.name = name; // Assign name
        this.quantity = quantity > 0 ? quantity : 0; // Ensure quantity is non-negative
        this.unit = unit; // Assign unit
    }

    // Overloaded constructor to create an Ingredient object with all fields, including metadata
    public Ingredient(String name, double quantity, String unit, String imageUrl, String description, NutritionalInfo nutritionalInfo) {
        this.name = name; // Assign name
        this.quantity = quantity; // Set quantity (assuming valid input here)
        this.unit = unit; // Assign unit
        this.imageUrl = imageUrl; // Assign image URL
        this.description = description; // Assign description
        this.nutritionalInfo = nutritionalInfo; // Assign nutritional info
    }

    // Getters and setters for each attribute

    public String getName() {
        return name;
    } // Return ingredient name

    public void setQuantity(double quantity) {
        this.quantity = quantity > 0 ? quantity : 0;
    } // Set quantity with validation (non-negative)

    public double getQuantity() {
        return quantity;
    } // Return ingredient quantity

    public void setUnit(String unit) {
        this.unit = unit;
    } // Set unit of measurement

    public String getUnit() {
        return unit;
    } // Return unit of measurement

    public String getImageUrl() {
        return imageUrl;
    } // Return URL for ingredient image

    public String getDescription() {
        return description;
    } // Return description of ingredient

    public NutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    } // Return nutritional info if available

    public boolean getInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public void changeInStock() {
        this.inStock = ! this.inStock;
    }

    // Overriding equals() to compare ingredients based on name and unit only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Return true if comparing same object
        if (o == null || getClass() != o.getClass()) return false; // False if not the same class
        Ingredient that = (Ingredient) o; // Cast object to Ingredient for comparison
        return name.equals(that.name) && unit.equals(that.unit); // True if name and unit match
    }

    // Method to display ingredient information in a user-friendly format
    public void displayIngredientInfo() {
        System.out.println("Ingredient: " + name); // Display ingredient name
        System.out.println("Quantity: " + quantity + " " + unit); // Display quantity and unit
        System.out.println("Image: " + imageUrl); // Display image URL if available
        System.out.println("Description: " + description); // Display description if available
        if (nutritionalInfo != null) { // Check if nutritional info exists
            nutritionalInfo.displayNutritionalInfo(); // Display nutritional info if available
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : "Unnamed Ingredient");
        if (quantity > 0) {
            sb.append(" (").append(quantity).append(" ").append(unit != null ? unit : "").append(")");
        }
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        return sb.toString();
    }
}
