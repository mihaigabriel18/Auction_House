package command.print_info;

import auction.institution.AuctionHouse;
import auction.institution.employee.broker.Broker;
import command.Command;

import static java.lang.System.*;

/**
 * Prints the brokers of the {@link AuctionHouse} balance, how much money they
 * gained from the fees of auction winners {@link Command}
 */
public class ListBrokersBalance implements Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        AuctionHouse.getInstance().getBrokerList().stream().map(Broker::getBrokerMoney).forEach(out::println);
    }
}
