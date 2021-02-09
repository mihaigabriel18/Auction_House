package auction.institution;

import auction.institution.clients.Client;
import auction.institution.clients.PhysicalPerson;
import auction.institution.employee.broker.Broker;
import auction.storage.Product;
import command.initialize_auctionhouse.LoadAdministrator;
import command.initialize_auctionhouse.LoadBrokers;
import command.initialize_auctionhouse.LoadClients;
import database.products.ParseProducts;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static command.Console.*;

public class AuctionHouseTest {

    @Test
    public void checkCorrectRemovalOfProduct() {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        initializeRemoval(auctionHouse);

        // get a random broker and make it delete a random product
        Broker randomBroker = auctionHouse.getBrokerList().get((new Random())
                .nextInt(auctionHouse.getBrokerList().size()));
        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));

        randomBroker.removeProduct(randomProduct);

        boolean isPresentAfterRemoval = auctionHouse.getProductList().stream().anyMatch(product ->
                product.getId().equals(randomProduct.getId()));

        // the boolean is supposed to be false
        assertFalse(isPresentAfterRemoval);
    }

    private void initializeRemoval(AuctionHouse auctionHouse) {
        addRequest(new LoadBrokers());
        addRequest(new LoadAdministrator("Admin"));
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);
    }

    @Test
    public void checkCorrectAdditionOfProducts() {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();

        initializeAddition();

        // get a random product
        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        Product randomProduct = productList.get((new Random()).nextInt(productList.size()));

        // push the product to the store's storage
        auctionHouse.getAdministrator().addProductToDeposit(randomProduct);

        boolean isPresentAfterAddition = auctionHouse.getProductList().stream().anyMatch(product ->
                product.getId().equals(randomProduct.getId()));

        // the boolean should be true
        assertTrue(isPresentAfterAddition);
    }

    private void initializeAddition() {
        addRequest(new LoadAdministrator("Admin"));
        executeCommands();
    }

    @Test
    public void checkIfCorrectClientWinsBid() {
        List<Pair<Integer, Client>> bids1 = initializeFirstSet();
        List<Pair<Integer, Client>> bids2 = initializeSecondSet();

        AuctionHouse auctionHouse = AuctionHouse.getInstance();

        Pair<Integer, Client> winner1 = auctionHouse.getWinningBid(bids1);
        Pair<Integer, Client> winner2 = auctionHouse.getWinningBid(bids1);

        boolean isCorrectFirst = winner1.getValue0() == 200;
        boolean isCorrectSecond = winner2.getValue1().getId() == 6666666;

        assertTrue(isCorrectFirst && isCorrectSecond);
    }

    private List<Pair<Integer, Client>> initializeFirstSet() {
        Client client1 = new PhysicalPerson(5555555, "Gigi",
                "Splaiul Independentei", "20/12/2020");
        Client client2 = new PhysicalPerson(6666666, "Andrei",
                "Splaiul Libertatii", "07/09/2019");

        client1.setNrOfWonAuctions(1);
        client2.setNrOfWonAuctions(1);

        List<Pair<Integer, Client>> bids1 = new ArrayList<>();
        Pair<Integer, Client> bidClient1 = new Pair<>(100, client1);
        Pair<Integer, Client> bidClient2 = new Pair<>(200, client2); // winning bid

        bids1.add(bidClient1);
        bids1.add(bidClient2);

        return bids1;
    }

    private List<Pair<Integer, Client>> initializeSecondSet() {
        Client client1 = new PhysicalPerson(5555555, "Gigi",
                "Splaiul Independentei", "20/12/2020");
        Client client2 = new PhysicalPerson(6666666, "Andrei",
                "Splaiul Libertatii", "07/09/2019");

        client1.setNrOfWonAuctions(2);
        client2.setNrOfWonAuctions(3); // winner because of more auction won

        List<Pair<Integer, Client>> bids2 = new ArrayList<>();
        Pair<Integer, Client> bidClient1 = new Pair<>(300, client1);
        Pair<Integer, Client> bidClient2 = new Pair<>(300, client2);

        bids2.add(bidClient1);
        bids2.add(bidClient2);

        return bids2;
    }

    @Test
    public void checkCorrectCreationOfNewAuction() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        initializeAuctionCreation(auctionHouse);

        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));
        auctionHouse.createAuctionForProduct(randomProduct.getId(), (new Random()).nextInt(10),
                (new Random()).nextInt(10));

        Auction auction = auctionHouse.getAuctionById(randomProduct.getId());

        // if the auction has not been created correctly, the auction object will be null
        Assert.assertNotNull(auction);
    }

    private void initializeAuctionCreation(AuctionHouse auctionHouse) {
        addRequest(new LoadAdministrator("Admin"));
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);
    }

    @Test(expected = NoSuchAuctionException.class)
    public void throwingNoSuchAuctionExceptionTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();

        addRequest(new LoadAdministrator("Admin"));
        addRequest(new LoadBrokers());
        addRequest(new LoadClients());
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);

        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));
        Client randomClient = auctionHouse.getClientList().get(0);

        Auction auction = new Auction(2222222, 10, randomClient.getId(),
                10, randomProduct);

        auctionHouse.activeAuctions.add(auction);

        randomClient.joinActiveAuction(333333, 10, false); // it should not be able to
        // join this auction since it does not exist
    }

    @Test(expected = AuctionIsFullException.class)
    public void throwingAuctionIsFullExceptionTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();

        addRequest(new LoadAdministrator("Admin"));
        addRequest(new LoadBrokers());
        addRequest(new LoadClients());
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);
        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));

        Client randomClient1 = auctionHouse.getClientList().get(0);
        Client randomClient2 = auctionHouse.getClientList().get(1);
        Client randomClient3 = auctionHouse.getClientList().get(2);

        randomClient1.createAuctionForProduct(randomProduct.getId(), 500, 2,
                10, false);
        randomClient2.joinActiveAuction(randomProduct.getId(), 1000, false);
        randomClient3.joinActiveAuction(randomProduct.getId(), 2000, false);
    }

    @Test(expected = ClientAlreadyInAuction.class)
    public void throwingClientAlreadyInAuctionExceptionTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();

        addRequest(new LoadAdministrator("Admin"));
        addRequest(new LoadBrokers());
        addRequest(new LoadClients());
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);
        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));

        Client randomClient1 = auctionHouse.getClientList().get(0);

        randomClient1.createAuctionForProduct(randomProduct.getId(), 500, 2,
                10, false);
        randomClient1.joinActiveAuction(randomProduct.getId(), 1000, false);
    }

}