package command.client_auction;

import auction.institution.*;
import auction.institution.clients.Client;
import command.Command;
import gui.StartWindow;
import gui.ThreadPanel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static java.lang.System.*;

/**
 * Enroll in auction {@link Command}, subscribing a client to an auction
 * first parameter - client id
 * second parameter - max sum to bid
 * third parameter - state in auction, true/false
 * fourth parameter - auction id
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EnrollInAuction implements Command {

    int clientId;

    int auctionId;

    int maxBid;

    boolean isActive;

    public EnrollInAuction(String clientId, String maxBid, String isActive, String auctionId) {
        this.auctionId = Integer.parseInt(auctionId);
        this.clientId = Integer.parseInt(clientId);
        this.maxBid = Integer.parseInt(maxBid);
        this.isActive = Boolean.parseBoolean(isActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Client client;
        try {
            client = AuctionHouse.getInstance().getClientById(clientId);
        } catch (NullPointerException n) {
            err.println("Client with id " + clientId + " does not exist");
            n.printStackTrace();
            return;
        }

        Auction auction;
        try {
            auction = client.joinActiveAuction(auctionId, maxBid, isActive);
        } catch (NoSuchAuctionException | AuctionIsFullException | ClientAlreadyInAuction e) {
            e.printStackTrace();
            err.println(e.getMessage());
            return;
        }

        ThreadPanel panel = StartWindow.getInstance().getThreadPanel();
        synchronized (panel) {
            panel.setNrSubscribed(auction.getNrCurrentParticipants());
            panel.notifyAll();
        }
    }
}
