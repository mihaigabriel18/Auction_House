package command.client_auction;

import auction.institution.Auction;
import auction.institution.AuctionHouse;
import command.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Place a bid for an auction that is waiting a client to place a bid
 * first parameter - auction id to place bid in
 * second parameter - amount to bid
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PlaceBid implements Command {

    int auctionId;

    int amount;

    public PlaceBid(String auctionId, String amount) {
        this.auctionId = Integer.parseInt(auctionId);
        this.amount = Integer.parseInt(amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Auction auction = AuctionHouse.getInstance().getAuctionById(auctionId);
        auction.setSumToBid(amount);

        synchronized (auction) {
            auction.notifyAll();
        }
    }
}
