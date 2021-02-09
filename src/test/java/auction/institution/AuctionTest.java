package auction.institution;

import auction.institution.clients.Client;
import auction.institution.employee.broker.Broker;
import auction.storage.Product;
import command.initialize_auctionhouse.LoadAdministrator;
import command.initialize_auctionhouse.LoadBrokers;
import command.initialize_auctionhouse.LoadClients;
import command.initialize_auctionhouse.LoadProducts;
import database.products.ParseProducts;
import org.javatuples.Pair;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static command.Console.addRequest;
import static command.Console.executeCommands;
import static org.junit.Assert.*;

public class AuctionTest {

    @Test
    public void disqualifyCorrectClientsFromAuctionTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        Pair<Auction, Client> auctionClientPair = initializeAuction(auctionHouse);

        auctionClientPair.getValue0().setMinBid(2500);
        auctionClientPair.getValue0().disqualifyClientsFromAuction();

        boolean onlyCorrectClientsInvolved = (auctionClientPair.getValue0().getRegisteredClients().size() == 1) &&
                (auctionClientPair.getValue0().getRegisteredClients().get(0).getId()
                        .equals(auctionClientPair.getValue1().getId()));

        assertTrue(onlyCorrectClientsInvolved);
    }

    private Pair<Auction, Client> initializeAuction(AuctionHouse auctionHouse) throws Exception {
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


        // make it so that the client 3 is the only one in the auction
        Auction auction = randomClient1.createAuctionForProduct(randomProduct.getId(), randomClient1.getMaxBid(),
                3, 10, false);
        randomClient2.joinActiveAuction(randomProduct.getId(), 2000, false);
        randomClient3.joinActiveAuction(randomProduct.getId(), 3000, false);

        auction.setMinBid(2500);

        return new Pair<>(auction, randomClient3);
    }

    @Test
    public void subscribeClientCorrectlyToAuctionTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        Auction auction = initializeEmptyAuctions(auctionHouse);

        Client randomClient = auctionHouse.getClientList().get(1);

        auction.subscribeClientToAuction(randomClient, false);

        boolean addedCorrectly = auction.getRegisteredClients().stream().anyMatch(client ->
                client.getId().equals(randomClient.getId()));
        boolean increasedParticipants = auction.getNrCurrentParticipants() == 2; // only added2 so far
        boolean clientAddedToAuction = randomClient.getStateInAuction().containsKey(auction);

        assertTrue(addedCorrectly && increasedParticipants);
    }

    private Auction initializeEmptyAuctions(AuctionHouse auctionHouse) throws Exception {
        addRequest(new LoadAdministrator("Admin"));
        addRequest(new LoadBrokers());
        addRequest(new LoadClients());
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);

        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));
        Client randomClient = auctionHouse.getClientList().get(0);

        Auction auction = randomClient.createAuctionForProduct(randomProduct.getId(), randomClient.getMaxBid(),
                3, 10, false);

        return auction;
    }

    @Test
    public void sellAuctionProductCorrectlyTest() throws Exception {
        AuctionHouse auctionHouse = AuctionHouse.getInstance();
        Pair<Auction, Product> auctionAndProduct = initializeAuctionWithBrokers(auctionHouse);

        auctionAndProduct.getValue0().sellTheProduct(new Pair<>(1000, auctionHouse.getClientList().get(0)));

        boolean productHasCorrectSellingPrice = auctionAndProduct.getValue1().getSalePrice().equals(1000d);
        boolean auctionDoesNotHaveProductAnymore = auctionHouse.getProductList().stream().noneMatch(product ->
                product.getId().equals(auctionAndProduct.getValue1().getId()));

        assertTrue(productHasCorrectSellingPrice && auctionDoesNotHaveProductAnymore);
    }

    private Pair<Auction, Product> initializeAuctionWithBrokers(AuctionHouse auctionHouse) throws Exception {
        addRequest(new LoadAdministrator("Admin"));
        addRequest(new LoadBrokers());
        addRequest(new LoadClients());
        executeCommands();

        List<Product> productList = (new ParseProducts("product_deposit.json")).readProducts();
        productList.forEach(auctionHouse.getAdministrator()::addProductToDeposit);

        Product randomProduct = auctionHouse.getProductList().get((new Random())
                .nextInt(auctionHouse.getProductList().size()));
        Broker randomBroker = auctionHouse.getBrokerList().get((new Random())
                .nextInt(auctionHouse.getBrokerList().size()));
        Client randomClient = auctionHouse.getClientList().get(0);

        Auction auction = randomClient.createAuctionForProduct(randomProduct.getId(), randomClient.getMaxBid(),
                3, 10, false);

        randomClient.involveClientInAuction(auction, randomBroker, false);

        return new Pair<>(auction, randomProduct);
    }

}