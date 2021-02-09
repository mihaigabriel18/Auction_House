package auction.institution.clients;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * A certain type of the abstract class {@link Client}
 * Denotes a physical person with a special field for the person's birthday
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ToString
public class PhysicalPerson extends Client {

    @Getter
    @Setter
    String birthdayDate;

    public PhysicalPerson(int id, String name, String address, String birthdayDate) {
        super(id, name, address);
        this.birthdayDate = birthdayDate;
    }
}
