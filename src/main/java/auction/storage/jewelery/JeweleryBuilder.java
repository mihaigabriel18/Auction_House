package auction.storage.jewelery;

import auction.storage.furniture.Furniture;

/**
 * Builder design pattern for {@link Furniture}
 */
public class JeweleryBuilder {

    private final Jewelery jewelery;

    public JeweleryBuilder() {
        jewelery = new Jewelery();
    }

    public JeweleryBuilder withId(Integer id) {
        jewelery.setId(id);
        return this;
    }

    public JeweleryBuilder withName(String name) {
        jewelery.setName(name);
        return this;
    }

    public JeweleryBuilder withMinimumPrice(double minimumPrice) {
        jewelery.setMinimumPrice(minimumPrice);
        return this;
    }

    public JeweleryBuilder withYear(int year) {
        jewelery.setYear(year);
        return this;
    }

    public JeweleryBuilder withMaterial(String material) {
        jewelery.setMaterial(material);
        return this;
    }

    public JeweleryBuilder withValuableGem(boolean valuableGem) {
        jewelery.setValuableGem(valuableGem);
        return this;
    }

    public Jewelery build() {
        return jewelery;
    }
}
