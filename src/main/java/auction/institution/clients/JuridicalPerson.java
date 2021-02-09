package auction.institution.clients;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.EnumUtils;

/**
 * A certain type of the abstract class {@link Client}
 * Denotes a juridical person with special fields for company type and social capital
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class JuridicalPerson extends Client {

    CompanyType companyType;
    double socialCapital;

    public JuridicalPerson(int id, String name, String address, String companyType, double socialCapital) {
        super(id, name, address);
        this.companyType = EnumUtils.getEnum(CompanyType.class, companyType);
        this.socialCapital = socialCapital;
    }
}
