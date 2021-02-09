package command.initialize_auctionhouse;

import auction.institution.AuctionHouse;
import auction.institution.employee.broker.Broker;
import command.Command;
import database.employees.ParseBrokers;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Load the brokers to the store {@link Command}
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoadBrokers implements Command {

    static String FILENAME = "brokers.json";

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        ParseBrokers parser = new ParseBrokers(FILENAME);
        List<Broker> brokers = parser.readBrokers();

        // add each broker to the auction house
        brokers.forEach(AuctionHouse.getInstance()::addNewBroker);
    }
}
