package command.client_auction;

import auction.institution.Auction;
import auction.institution.AuctionHouse;
import auction.institution.clients.Client;
import auction.storage.NoSuchProductException;
import command.Command;
import gui.StartWindow;
import gui.ThreadPanel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static java.lang.System.*;

/**
 * Start an auction {@link Command}, subscribing a client to an auction, creating an auction alongside with it
 * first parameter - client id
 * second parameter - max sum to bid
 * third parameter - state in auction, true/false
 * fourth parameter - auction id
 * fifth parameter - clients in the auction
 * sixth parameter - max steps in auction
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartAuction implements Command {

    int clientId;

    int productId;

    int nrParticipantsAuction;

    int nrMaxStepsAuction;

    int maxBid;

    boolean isActive;

    public StartAuction(String clientId, String maxBid, String isActive, String productId,
                        String nrParticipantsAuction, String nrMaxStepsAuction) {
        this.clientId = Integer.parseInt(clientId);
        this.productId = Integer.parseInt(productId);
        this.nrParticipantsAuction = Integer.parseInt(nrParticipantsAuction);
        this.nrMaxStepsAuction = Integer.parseInt(nrMaxStepsAuction);
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
            n.printStackTrace();
            err.println("Client with id " + clientId + " does not exist");
            return;
        }

        Auction auction;
        try {
            auction = client.createAuctionForProduct(productId, maxBid, nrParticipantsAuction,
                    nrMaxStepsAuction, isActive);
        } catch (NoSuchProductException e) {
            e.printStackTrace();
            err.println(e.getMessage());
            return;
        }

        ThreadPanel panel = StartWindow.getInstance().getThreadPanel();
        synchronized (panel) {
            panel.setProductId(productId);
            panel.setNrSubscribed(auction.getNrCurrentParticipants());
            panel.setNrRequired(auction.getNrRequiredParticipants());
            panel.notifyAll();
        }
    }
}
