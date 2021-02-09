package command.initialize_auctionhouse;

import auction.institution.AuctionHouse;
import auction.institution.clients.Client;
import command.Command;
import database.employees.ParseClients;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Load the clients to the store {@link Command}
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoadClients implements Command {

    static String FILENAME = "clients.json";

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        ParseClients parser = new ParseClients(FILENAME);
        List<Client> clients = parser.readClients();

        // add each client to the auction house
        clients.forEach(AuctionHouse.getInstance()::addNewClient);
    }
}
