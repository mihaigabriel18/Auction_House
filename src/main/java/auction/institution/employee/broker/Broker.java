package auction.institution.employee.broker;

import auction.institution.Auction;
import auction.institution.AuctionHouse;
import auction.institution.clients.Client;
import auction.institution.clients.JuridicalPerson;
import auction.institution.clients.PhysicalPerson;
import auction.storage.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;

/**
 * A broker is an employee of the {@link AuctionHouse}, it has options to request
 * the associated client of an auction for their bets, the broker also keeps commission
 * from the winner and increases his capital value.
 * The brokers can remove product from the product list in {@link AuctionHouse}, it will
 * use the consumer design pattern for removing products from that list, acquiring a lock from
 * the auction house, removing the product and releasing the lock
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class Broker implements IBroker {

    @Getter
    String name;

    @Getter
    List<Client> clientList;

    @Getter
    @NonFinal
    Double brokerMoney;

    public Broker(String name) {
        this.name = name;
        this.clientList = new ArrayList<>();
        brokerMoney = 0d; // at the start the broker has no money
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int requestClientForBid(Auction auction, Client client, int minimumBid, int... constrains) {
        return client.placeBid(auction, minimumBid, constrains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int keepCommission(int bid, Client client) {
        if (client instanceof JuridicalPerson) {
            if (client.getNrOfAuctionsInvolved() > 25)
                return keepCommissionCalculate(bid, 0.1);
            else
                return keepCommissionCalculate(bid, 0.25);
        }
        else if (client instanceof PhysicalPerson) {
            if (client.getNrOfAuctionsInvolved() > 5)
                return keepCommissionCalculate(bid, 0.15);
            else
                return keepCommissionCalculate(bid, 0.2);
        }
        throw new IllegalStateException("Client is neither a juridical or physical person, check for bugs!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProduct(Product product) {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        auctionHouse.lockProductList();

        auctionHouse.removeProduct(product);

        auctionHouse.unlockProductList();
    }

    private int keepCommissionCalculate(int bid, double commission) {
        brokerMoney += commission * bid;
        return (int) ((1 - commission) * bid);
    }
}
