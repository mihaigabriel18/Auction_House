package command.print_info;

import auction.institution.AuctionHouse;
import command.Command;

import static java.lang.System.*;

/**
 * Prints the products of the {@link AuctionHouse} {@link Command}
 */
public class ListProducts implements Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        AuctionHouse.getInstance().getProductList().forEach(out::println);
    }
}
