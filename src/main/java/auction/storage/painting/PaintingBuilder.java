package auction.storage.painting;

import auction.storage.furniture.Furniture;

/**
 * Builder design pattern for {@link Furniture}
 */
public class PaintingBuilder {

    private final Painting painting;

    public PaintingBuilder() {
        painting = new Painting();
    }

    public PaintingBuilder withId(Integer id) {
        painting.setId(id);
        return this;
    }

    public PaintingBuilder withName(String name) {
        painting.setName(name);
        return this;
    }

    public PaintingBuilder withMinimumPrice(double minimumPrice) {
        painting.setMinimumPrice(minimumPrice);
        return this;
    }

    public PaintingBuilder withYear(int year) {
        painting.setYear(year);
        return this;
    }

    public PaintingBuilder withPainterName(String name) {
        painting.setPainterName(name);
        return this;
    }

    public PaintingBuilder withColor (String color) {
        painting.setColor(color);
        return this;
    }

    public Painting build() {
        return painting;
    }
}
