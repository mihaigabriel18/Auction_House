package command.print_info;

import auction.institution.AuctionHouse;
import command.Command;

import static java.lang.System.*;

/**
 * Prints the clients of the {@link AuctionHouse} {@link Command}
 */
public class ListClients implements Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        AuctionHouse.getInstance().getClientList().forEach(out::println);
    }
}
