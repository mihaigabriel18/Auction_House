package command.initialize_auctionhouse;

import auction.institution.AuctionHouse;
import auction.institution.employee.Administrator;
import command.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Load an administrator to the store {@link Command}
 * first parameter - name of administrator
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoadAdministrator implements Command {

    String name;

    public LoadAdministrator(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // there can only be 1 admin that will be instantiated at the start
        Administrator administrator = new Administrator(name);

        AuctionHouse.getInstance().setAdministrator(administrator);
    }
}
