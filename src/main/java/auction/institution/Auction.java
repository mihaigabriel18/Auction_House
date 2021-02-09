package auction.institution;

import auction.institution.clients.BidOverClientsMaxAmountException;
import auction.institution.clients.Client;
import auction.institution.employee.broker.Broker;
import auction.institution.employee.broker.BrokerProxy;
import auction.storage.Product;
import gui.StartWindow;
import gui.auction.AuctionFrame;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Each auction will run on a separate thread, letting any clients join them, and letting multiple
 * auctions auctions run at the same time.
 * The thread will wait until enough clients join it and after that it will simulate the actual
 * bidding process, and also make the auction GUI frame visible
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public class Auction implements Runnable {

    @Setter
    @NonFinal
    int nrCurrentParticipants;

    Integer id;

    int nrRequiredParticipants;

    int idProduct;

    int nrMaxSteps;

    List<Client> registeredClients;

    Product productToSale;

    Lock startBiddingLock;

    /**
     * Sum to bid for clients who bid an exact amount
     */
    @Setter
    @NonFinal
    Integer sumToBid;

    @Setter
    @NonFinal
    int minBid;

    /**
     * When another thread will instantiate this flag, the bidding process will begin
     */
    @NonFinal
    Object startFlag;

    AuctionFrame auctionFrame;

    /**
     * All args constructor
     *
     * @param id                     unique identifier for auction
     * @param nrRequiredParticipants number of maximum participants in the auction
     * @param idProduct              product for which the auction is started
     * @param nrMaxSteps             number of maximum auction steps
     * @param productToSale          the product the auction will sell
     */
    public Auction(Integer id, int nrRequiredParticipants, int idProduct, int nrMaxSteps, Product productToSale) {
        this.nrCurrentParticipants = 0; // default no registered clients
        this.id = id;
        this.nrRequiredParticipants = nrRequiredParticipants;
        this.idProduct = idProduct;
        this.nrMaxSteps = nrMaxSteps;
        this.registeredClients = new ArrayList<>();
        this.productToSale = productToSale;
        this.minBid = 0; // minimum sum initially is 0, updated in the future with a new value
        this.startBiddingLock = new ReentrantLock(); // lock while we wait for users to
        this.sumToBid = null;
        this.auctionFrame = new AuctionFrame(this);
    }

    /**
     * Before calling {@code notifyAll()} this method needs to be called
     */
    public void initializeStartFlag() {
        startFlag = new Object();
    }

    /**
     * Subscribe a client to an auction, increasing the number of
     * current participants to the auction.
     * <p>
     * <strong>auction will not start until the required number of people participate</strong>
     * </p>
     *
     * @param client   client to subscribe to auction
     * @param isActive client is active or not in the auction
     * @throws ClientAlreadyInAuction if the client is already in this auction
     */
    public synchronized void subscribeClientToAuction(Client client, boolean isActive) throws ClientAlreadyInAuction {
        if (registeredClients.contains(client))
            throw new ClientAlreadyInAuction("Client " + client.getId() + " has already been enrolled in " + id);
        nrCurrentParticipants++;
        registeredClients.add(client);
        // get random broker index
        List<Broker> brokerList = AuctionHouse.getInstance().getBrokerList();
        int randomBrokerIndex = (new Random()).nextInt(brokerList.size());
        // set client broker as one found above
        client.involveClientInAuction(this, brokerList.get(randomBrokerIndex), isActive);
        // append client to broker's list of clients
        brokerList.get(randomBrokerIndex).getClientList().add(client);
    }

    /**
     * Wait until the auction is populated then start bidding process
     */
    @SneakyThrows
    @Override
    public synchronized void run() {
        while (nrCurrentParticipants < nrRequiredParticipants) {
            wait();
        }
        auctionFrame.makeFrameVisible();
        startAuction();
    }

    /**
     * We want each auction to create its frame independently
     */
    @SneakyThrows
    public synchronized void startAuction() {
        registeredClients.forEach(client -> auctionFrame.logClientAndBroker(client, client.getBrokerForAuction(this)));

        while (startFlag == null) {
            this.wait();
        }

        auctionProcess();
    }

    /**
     * Unleashes the whole bidding process, ended whether there are no clients left in the auction
     * or if someone won it
     */
    private void auctionProcess() {
        Pair<Integer, Client> winner = null;
        for (int k = 0; k < nrMaxSteps; k++) {
            auctionFrame.logAuctionInfo("\nSTARTING STEP " + (k + 1) + " OF BIDDING PROCESS\n");

            List<Pair<Integer, Client>> stepOfBidding = auctionStep();
            winner = AuctionHouse.getInstance().getWinningBid(stepOfBidding);
            this.minBid = winner.getValue0(); // update new min bid value
            disqualifyClientsFromAuction();
            // check if the auction is ended prematurely
            if (registeredClients.isEmpty()) // no
                throw new IllegalStateException("Auction cannot be ended yet, this state should not be reached");
            else if (registeredClients.size() == 1) // there is a winner for our auction
                break;
        }
        // check if the product can be sold or not (minimum price has been reached)
        if (Objects.requireNonNull(winner).getValue0() > productToSale.getMinimumPrice()) {
            sellTheProduct(winner);
            sellProductFromGui(winner);
            auctionFrame.makeCloseButtonVisible();
            return;
        }

        auctionFrame.logAuctionInfo("Product has not been sold because the winning bid of " +
                winner.getValue0() + " did not exceed the minimum required price of " +
                productToSale.getMinimumPrice());
        auctionFrame.makeCloseButtonVisible();
    }

    /**
     * Sale the product from the perspective of the {@link gui.ProductDeposit} (GUI)
     *
     * @param winner winner of auction (pair of winning client and his winning bid)
     */
    private void sellProductFromGui(Pair<Integer, Client> winner) {
        // log winner
        auctionFrame.logAuctionInfo("Client " + winner.getValue1().getName() + " has won this auction " +
                "with a bid of " + winner.getValue0() + " dollars!");
        // remove the product from the gui
        StartWindow.getInstance().getProductDeposit().removeProduct(productToSale);
    }

    /**
     * Sale the product from the perspective of the {@link AuctionHouse}
     *
     * @param winner winner of auction (pair of winning client and his winning bid)
     */
    public void sellTheProduct(Pair<Integer, Client> winner) {
        // actually sell the product
        Objects.requireNonNull(winner).getValue1().winAuction();
        // log the broker's commission
        (new BrokerProxy(winner.getValue1().getBrokerForAuction(this), this))
                .keepCommission(winner.getValue0(), winner.getValue1());
        productToSale.setSalePrice((double) winner.getValue0()); // set the sales price
        // product has been sold
        winner.getValue1().getBrokerForAuction(this).removeProduct(productToSale);

        registeredClients.forEach(client -> client.disqualifyClientFromAuction(this));
    }

    /**
     * After a bidding step, some clients may be removed from the auction, because the
     * maximum bid they can afford is smaller than the current maximum bid of the auction
     */
    public void disqualifyClientsFromAuction() {
        Iterator<Client> iter = registeredClients.iterator();
        while (iter.hasNext()) {
            Client iterClient = iter.next();
            if (iterClient.getMaxBid() < minBid) {
                iterClient.disqualifyClientFromAuction(this);
                iter.remove();
            }
        }
    }

    /**
     * Actions to be performed in an auction step (a bid for every client)
     *
     * @return list of pair from clients and theirs bids (from this auction step)
     */
    @SneakyThrows
    private List<Pair<Integer, Client>> auctionStep() {
        List<Pair<Integer, Client>> bids = new ArrayList<>();
        for (Client client : registeredClients) {
            Broker broker = client.getBrokerForAuction(this);
            int clientBid;
            if (client.getStateOfClientInAuction(this)) { // is an active bidder
                while (sumToBid == null) {
                    auctionFrame.logAuctionInfo("Waiting for client " + client.getName() +
                            " to place a bid above " + minBid + " dollars");
                    synchronized (this) {
                        this.wait();
                    }
                }
                checkValidBid(client);

                clientBid = (new BrokerProxy(broker, this))
                        .requestClientForBid(this, client, minBid, sumToBid);
                sumToBid = null; // reset the sum
            } else
                clientBid = (new BrokerProxy(broker, this))
                        .requestClientForBid(this, client, minBid);

            bids.add(new Pair<>(clientBid, client));
        }
        return bids;
    }

    private void checkValidBid(Client client) throws InterruptedException {
        while (true) { // loop until the bid is correct
            if (sumToBid > client.getMaxBid()) {
                sumToBid = client.getMaxBid(); // set the sum to max
            } else if (sumToBid < minBid) {
                sumToBid = null; // reset the sum
                auctionFrame.logAuctionInfo("Try again!" + client.getName() +
                        " needs to place a bid above " + minBid + " dollars");
                while (sumToBid == null) {
                    synchronized (this) {
                        this.wait();
                    }
                }
            } else
                break;
        }
    }
}
