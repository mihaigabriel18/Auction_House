package auction.institution.employee.broker;

import auction.institution.Auction;
import auction.institution.AuctionHouse;
import auction.institution.clients.Client;
import auction.storage.Product;

public interface IBroker {

    /**
     * Request a client for his bid
     * @param auction auction the client will bid in
     * @param client the client that will bid
     * @param minimumBid the minimum bid required
     * @param constrains the additional bounds set to the bid
     * @return the bid
     */
    int requestClientForBid(Auction auction, Client client, int minimumBid, int... constrains);

    /**
     * The broker will keep a commission from the the winner client's bid
     * @param bid the winner's bid
     * @param client the winner client
     * @return the actual value of the bid, after the broker takes the cut
     */
    int keepCommission(int bid, Client client);

    /**
     * Using the consumer problem, it will erase a product form the product list in
     * {@link AuctionHouse}, locking and unlocking a {@link java.util.concurrent.locks.ReentrantLock}
     * @param product the product to be removed
     */
    void removeProduct(Product product);
}
