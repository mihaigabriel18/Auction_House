package auction.storage;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract class defining a product, a list of products is held in {@link auction.institution.AuctionHouse}
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
public abstract class Product {

    @EqualsAndHashCode.Include
    Integer id;

    String name;

    AtomicReference<Double> salePrice;

    double minimumPrice;

    int year;

    public void setSalePrice(Double salePrice) {
        this.salePrice = new AtomicReference<>(salePrice);
    }

    protected Product() {
        salePrice = new AtomicReference<>();
    }

    public Double getSalePrice() {
        return salePrice.get();
    }

    protected Product(Integer id, String name, double minimumPrice, int year) {
        this.id = id;
        this.name = name;
        this.minimumPrice = minimumPrice;
        this.year = year;
    }
}
