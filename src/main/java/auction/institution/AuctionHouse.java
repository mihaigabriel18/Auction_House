package auction.institution;

import auction.institution.clients.Client;
import auction.institution.employee.Administrator;
import auction.institution.employee.broker.Broker;
import auction.storage.NoSuchProductException;
import auction.storage.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.javatuples.Pair;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * The auction house is a <strong>singleton</strong>, only one instance of this class will ever exist,
 * and it holds information about all the products, clients, brokers and active auctions.
 * This class also uses the <strong>composite design pattern</strong> treating lists of objects as a single
 * object, encapsulating the fields to a larger extend.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@Getter
@Setter
public class AuctionHouse {

    @NonFinal
    Administrator administrator;

    List<Product> productList;
    List<Client> clientList;
    List<Auction> activeAuctions;
    List<Broker> brokerList;

    Lock productListLock;

    /**
     * private constructor for class
     */
    private AuctionHouse() {
        productList = new ArrayList<>();
        clientList = new ArrayList<>();
        activeAuctions = new ArrayList<>();
        brokerList = new ArrayList<>();
        this.productListLock = new ReentrantLock();
    }

    /**
     * Inner static class that hols the only instance of the singleton,
     * assures thread-safe singleton implementation
     */
    private static class ReferenceHolder {
        public static final AuctionHouse INSTANCE = new AuctionHouse();
    }

    /**
     * Retrieve the only instance of the thread-safe singleton
     * @return the only {@link AuctionHouse} instance
     */
    public static AuctionHouse getInstance() {
        return ReferenceHolder.INSTANCE;
    }

    public void lockProductList() {
        productListLock.lock();
    }

    public void unlockProductList() {
        productListLock.unlock();
    }

    public void addNewClient(Client client) {
        clientList.add(client);
    }

    public void addNewBroker(Broker broker) {
        brokerList.add(broker);
    }

    public void addNewProduct(Product product) {
        productList.add(product);
    }

    public void removeProduct(Product product) {
        boolean result = productList.removeIf(prodIter -> prodIter.getId().equals(product.getId()));
        if (!result) {
            throw new InvalidParameterException(product.getName() + "is not a valid product or is not for sale");
        }
    }

    /**
     * Get product with the specified id form the database
     * @param id product's unique identifier
     * @return {@link Product} object if we find it in the database,
     *          <strong>null</strong> else
     */
    public Auction getAuctionById(int id) {
        return activeAuctions.stream().filter(auction ->
                auction.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Get product with the specified id form the database
     * @param id product's unique identifier
     * @return {@link Product} object if we find it in the database,
     *          <strong>null</strong> otherwise
     */
    public Product getProductById(int id) {
        return productList.stream().filter(product ->
                product.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Get client with the specified id from the database
     * @param id client's unique identifier
     * @return {@link Client} object if we find it in the database
     * @throws NullPointerException if there is no such product in the database
     */
    public Client getClientById(int id) {
        Client client = clientList.stream().filter(client1 ->
                client1.getId().equals(id)).findFirst().orElse(null);
        return Objects.requireNonNull(client);
    }

    public Auction createAuctionForProduct(int id, int nrParticipants, int nrMaxSteps)
            throws NoSuchProductException {

        Product product = getProductById(id);
        // there is no such product
        if (product == null)
            throw new NoSuchProductException("No product with id: " + id);

        // create new auction and start it on a new thread
        Auction auction = new Auction(id, nrParticipants, id, nrMaxSteps, product);
        (new Thread(auction)).start();

        activeAuctions.add(auction);

        return auction;
    }

    /**
     * Calculate the winner of a bid step taking into account the fact that there could be multiple
     * {@link Client}s with the same bid, counting the one with the most auctions won as the winner
     * @param biddingResult List of bidding results, consisting of {@link Pair}s between
     *                      clients bids adn the clients themselves
     * @return the winner of the bid along with the sum he bidded
     */
    public Pair<Integer, Client> getWinningBid(List<Pair<Integer, Client>> biddingResult) {
        Integer winnerBid = biddingResult.stream().map(Pair::getValue0)
                .max(Comparator.naturalOrder()).orElse(null);
        List<Pair<Integer, Client>> bidWinners = biddingResult.stream().filter(result ->
                result.getValue0().equals(winnerBid)).collect(Collectors.toList());
        Integer winnerWithMaxWon = bidWinners.stream().map(winner -> winner.getValue1().getNrOfWonAuctions())
                .max(Comparator.naturalOrder()).orElse(null);
        return bidWinners.stream().filter(winner ->
                winner.getValue1().getNrOfWonAuctions() == winnerWithMaxWon).findFirst().orElse(null);
    }

}
