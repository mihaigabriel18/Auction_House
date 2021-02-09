package auction.storage.furniture;

/**
 * Builder design pattern for {@link Furniture}
 */
public class FurnitureBuilder {

    private final Furniture furniture;

    public FurnitureBuilder() {
        this.furniture = new Furniture();
    }

    public FurnitureBuilder withId(Integer id) {
        furniture.setId(id);
        return this;
    }

    public FurnitureBuilder withName(String name) {
        furniture.setName(name);
        return this;
    }

    public FurnitureBuilder withMinimumPrice(double minimumPrice) {
        furniture.setMinimumPrice(minimumPrice);
        return this;
    }

    public FurnitureBuilder withYear(int year) {
        furniture.setYear(year);
        return this;
    }

    public FurnitureBuilder withType(String type) {
        furniture.setType(type);
        return this;
    }

    public FurnitureBuilder withMaterial(String material) {
        furniture.setMaterial(material);
        return this;
    }

    public Furniture build() {
        return furniture;
    }
}
