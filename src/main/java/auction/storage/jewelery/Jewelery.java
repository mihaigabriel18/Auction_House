package auction.storage.jewelery;

import auction.storage.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * concrete class extension of {@link Product}
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Jewelery extends Product {

    @Getter
    @Setter
    String material;

    @Getter
    @Setter
    boolean valuableGem;
}
