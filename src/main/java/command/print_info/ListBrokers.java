package command.print_info;

import auction.institution.AuctionHouse;
import command.Command;

import static java.lang.System.*;

/**
 * Prints the brokers of the {@link AuctionHouse} {@link Command}
 */
public class ListBrokers implements Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        AuctionHouse.getInstance().getBrokerList().forEach(out::println);
    }
}
