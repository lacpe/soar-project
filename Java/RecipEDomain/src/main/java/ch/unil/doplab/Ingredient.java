package ch.unil.doplab;

public class Ingredient {
    private String name;
    private double quantity;
    private String unit;
    private String imageUrl;            // New field for ingredient image URL
    private String description;         // New field for a brief description
    private NutritionalInfo nutritionalInfo; // New field for nutritional details

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity > 0 ? quantity : 0;
        this.unit = unit;
    }

    public Ingredient(String name, double quantity, String unit, String imageUrl, String description, NutritionalInfo nutritionalInfo) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.description = description;
        this.nutritionalInfo = nutritionalInfo;
    }

    // Getters and setters for each attribute
    public String getName() {
        return name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity > 0 ? quantity : 0;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public NutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return name.equals(that.name) && unit.equals(that.unit);
    }

    public void displayIngredientInfo() {
        System.out.println("Ingredient: " + name);
        System.out.println("Quantity: " + quantity + " " + unit);
        System.out.println("Image: " + imageUrl);
        System.out.println("Description: " + description);
        if (nutritionalInfo != null) {
            nutritionalInfo.displayNutritionalInfo();
        }
    }
}