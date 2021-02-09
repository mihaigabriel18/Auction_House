package auction.institution.employee.broker;

import auction.institution.Auction;
import auction.institution.clients.Client;
import auction.storage.Product;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * A proxy connection between an auction and a broker is established through this class
 * It used the <strong>proxy design pattern</strong> and is supposed to intermediate the
 * connection between the broker and the auction, letting the GUI log specific messages when
 * a connection with a broker has been made
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BrokerProxy implements IBroker {

    Broker broker;

    Auction auction;

    public BrokerProxy(Broker broker, Auction auction) {
        this.broker = broker;
        this.auction = auction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int requestClientForBid(Auction auction, Client client, int minimumBid, int... constrains) {
        int rawBid = broker.requestClientForBid(auction, client, minimumBid, constrains);
        auction.getAuctionFrame().logAuctionInfo("Broker " + broker.getName() + " has received a bid of " +
                rawBid + " dollars from " + client.getName());
        return rawBid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int keepCommission(int bid, Client client) {
        int commissionedBid = broker.keepCommission(bid, client);
        auction.getAuctionFrame().logAuctionInfo("Broker " + broker.getName() + " has kept " +
                (bid - commissionedBid) + " dollars from " + client.getName() + "'s bid");
        return commissionedBid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProduct(Product product) {
        broker.removeProduct(product);

    }

}
