package auction.storage.furniture;

import auction.storage.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * concrete class extension of {@link Product}
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Furniture extends Product {

    @Getter
    @Setter
    String type;

    @Getter
    @Setter
    String material;
}
