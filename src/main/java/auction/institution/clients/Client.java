package auction.institution.clients;

import auction.institution.*;
import auction.institution.bidding_algorithm.BidAlgorithm;
import auction.institution.bidding_algorithm.algorithms.ConcreteBidFromCommand;
import auction.institution.bidding_algorithm.algorithms.SimpleRandomFromMinMax;
import auction.institution.employee.broker.Broker;
import auction.storage.NoSuchProductException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.javatuples.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A client is a user of the {@link AuctionHouse}, it has options to make the client
 * join an auction, create a new one or place a bid for one of these auctions.
 * A client can either be active or passive in an auction, a passive client will have his bids
 * calculated by an automated algorithm and an active one will wait for commands from the CLI
 * and use the bid given there.
 * A client will have a list of brokers associated, each one for every auction the client is
 * involved in.
 * This class uses the <strong>composite design pattern</strong>, making the map of brokers and
 * auctions be treated as a single object not a collection.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@Setter
@Getter
public abstract class Client {

    Integer id;

    String name;

    String address;

    /**
     * How many auctions this client participated in
     */
    @NonFinal
    int nrOfAuctionsInvolved;

    @NonFinal
    int nrOfWonAuctions;

    /**
     * This field is going to be update everytime the client enters into a new auction
     */
    @NonFinal
    int maxBid;

    /**
     * Each auction that the client is involved in with have it's own particular broker and a
     * boolean value denoting if the client is active in that auction (active: will choose
     * how much he bids, inactive : a random algorithm will calculate how much he bids)
     */
    Map<Auction, Pair<Broker, Boolean>> stateInAuction;

    /**
     * Constructor for all arguments
     * @param id unique identifier for client
     * @param name name of client
     * @param address address of client
     */
    protected Client(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.nrOfAuctionsInvolved = 0; // default 0 auction participated in
        this.nrOfWonAuctions = 0; // default 0 auction won
        this.stateInAuction = new ConcurrentHashMap<>();
    }

    /**
     * subscribe a client to an auction
     * @param auction auction to involve the client in
     * @param broker broker to involve the client in
     * @param isActive state of the client in the auction
     */
    public void involveClientInAuction(Auction auction, Broker broker, boolean isActive) {
        nrOfAuctionsInvolved++;
        stateInAuction.put(auction, new Pair<>(broker, isActive)); // for the moment there is no broker for this auction
    }

    /**
     * make the client not take part of an auction anymore
     * @param auction auction to remove the client from
     */
    public void disqualifyClientFromAuction(Auction auction) {
        stateInAuction.remove(auction);
    }

    /**
     * gets a broker from a specific auction, associated with this client
     * @param auction auction to get the broker for
     * @return the broker for this auction
     */
    public Broker getBrokerForAuction(Auction auction) {
        return stateInAuction.get(auction).getValue0();
    }

    /**
     * checks if the client is active or passive in the given auction
     * @param auction auction to get the state of the client for
     * @return boolean value with the state of the client in this auction
     */
    public boolean getStateOfClientInAuction(Auction auction) {
        return stateInAuction.get(auction).getValue1();
    }

    /**
     * Once the client wins the auction increase the number of auctions won
     */
    public void winAuction() {
        nrOfWonAuctions++;
    }

    /**
     * This logic relies on the fact that the client has a say in the auction's characteristics.
     * It will create a new auction for which it will subscribe this client to
     * @param id id of product, which will also be the created auction's id
     * @param maxBid max bid of the client
     * @param nrParticipants nr of required participant for the auction
     * @param nrMaxSteps nr of maximum steps for the auction
     * @param isActive true if the client will be active in the auction, false otherwise
     * @return the auction just created
     * @throws NoSuchProductException if the product we want to auction for does not exist, or has been
     *                                removed before
     */
    public Auction createAuctionForProduct(int id, int maxBid, int nrParticipants, int nrMaxSteps, boolean isActive)
            throws NoSuchProductException {
        AuctionHouse auctionHouse=  AuctionHouse.getInstance();
        Auction auction = auctionHouse.createAuctionForProduct(id, nrParticipants, nrMaxSteps);
        try {
            this.maxBid = maxBid; // update max bid for this auction in particular
            auction.subscribeClientToAuction(this, isActive);
        } catch (ClientAlreadyInAuction clientAlreadyInAuction) {
            throw new IllegalStateException(); // serious problem if this line in the code is reached
        }
        return auction;
    }

    /**
     * Make client join an active auction
     * @param id id of product, which will also be the created auction's id
     * @param maxBid max bid of the client
     * @param isActive true if the client will be active in the auction, false otherwise
     * @return the {@link Auction} with the id given parameter
     * @throws NoSuchAuctionException if the certain auction has not been found
     * @throws AuctionIsFullException if the client tries to join a full auction
     * @throws ClientAlreadyInAuction if the client is already in this auction
     */
    public Auction joinActiveAuction(int id, int maxBid, boolean isActive) throws NoSuchAuctionException,
            AuctionIsFullException, ClientAlreadyInAuction {
        Auction auction = AuctionHouse.getInstance().getAuctionById(id);
        // if there is no such auction
        if (auction == null)
            throw new NoSuchAuctionException("Auction with id: " + id + " has not yet started, if you wish to" +
                    "start an auction for a product ...................................!"); // complete with command
        // pattern command , e.g : you can say "start auction for ... if you wish to buy that and wait for clients to join in"

        if (auction.getNrRequiredParticipants() == auction.getNrCurrentParticipants())
            throw new AuctionIsFullException("Cannot register client " + id + " in auction with id "
                    + auction.getId() + " because the auction is full");

        this.maxBid = maxBid; // update max bid for this auction in particular
        auction.subscribeClientToAuction(this, isActive);

        synchronized (auction) {
            auction.notifyAll();
        }
        return auction;
    }


    /**
     * Gets the bid the client will place
     * @param auction auction the client will place the bid in
     * @param minimumBid minimum bid, cannot be lower than a certain value the {@link Auction} specifies
     * @param constrains parameters the calculate the bid with
     * @return the bid value
     */
    public int placeBid(Auction auction, int minimumBid, int... constrains) {
        int sumToBid;

        if (Boolean.TRUE.equals(stateInAuction.get(auction).getValue1())) // if it is active user
            sumToBid = (new BidAlgorithm(new ConcreteBidFromCommand()))
                    .calculateBid(constrains);
        else
            sumToBid = (new BidAlgorithm(new SimpleRandomFromMinMax()))
                    .calculateBid(minimumBid, maxBid);
        return sumToBid;
    }

}
