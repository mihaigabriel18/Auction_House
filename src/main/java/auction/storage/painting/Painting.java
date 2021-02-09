package auction.storage.painting;

import auction.storage.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * concrete class extension of {@link Product}
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Painting extends Product {

    @Getter
    @Setter
    String painterName;

    @Getter
    @Setter
    String color;
}
