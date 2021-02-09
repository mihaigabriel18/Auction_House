package testing;

import command.client_auction.EnrollInAuction;
import command.client_auction.PlaceBid;
import command.client_auction.StartAuction;
import command.initialize_auctionhouse.LoadAdministrator;
import command.initialize_auctionhouse.LoadBrokers;
import command.initialize_auctionhouse.LoadClients;
import command.initialize_auctionhouse.LoadProducts;
import command.print_info.ListBrokers;
import command.print_info.ListBrokersBalance;
import command.print_info.ListClients;
import command.print_info.ListProducts;
import gui.StartWindow;
import lombok.SneakyThrows;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static command.Console.addRequest;
import static command.Console.executeCommands;
import static java.lang.System.*;

/**
 * Main class, entry point of program
 */
public class Main {

    /**
     * choose which command to be executed
     * @param line line of input
     */
    private static void executeMethod(String line) {
        List<String> words = Arrays.asList(line.split(" "));
        // test possible commands
        switch (AllCommands.valueOf(words.get(0))) { // first word determines which command we call
            case start_auction -> addRequest(new StartAuction(words.get(1), words.get(2), words.get(3),
                    words.get(4), words.get(5), words.get(6)));
            case enroll_in_auction -> addRequest(new EnrollInAuction(words.get(1), words.get(2), words.get(3),
                    words.get(4)));
            case place_bid -> addRequest(new PlaceBid(words.get(1), words.get(2)));
            case load_products -> addRequest(new LoadProducts());
            case load_clients -> addRequest(new LoadClients());
            case load_brokers -> addRequest(new LoadBrokers());
            case load_administrator -> addRequest(new LoadAdministrator(words.get(1)));
            case list_brokers -> addRequest(new ListBrokers());
            case list_clients -> addRequest(new ListClients());
            case list_products -> addRequest(new ListProducts());
            case list_brokers_balance -> addRequest(new ListBrokersBalance());
            case exit, quit -> finalActions();
            default -> throw new IllegalArgumentException("Command " + words.get(0) + " does not exist");
        }
    }

    /**
     * When "quit" or "exit" is called, execute all commands so far and exit the program
     */
    private static void finalActions() {
        exit(0);
    }

    /**
     * Reads input from the input stream
     */
    private static void readInput() {
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                executeMethod(line);
                executeCommands();
            }
        }
    }

    @SneakyThrows
    public static void main(String[] args) {

        System.setOut(new PrintStream(new FileOutputStream("commandResults.out")));
        System.setErr(new PrintStream(new FileOutputStream("commandErrors.err")));

        // start swing gui on EVENT DISPATCH THREAD
        SwingUtilities.invokeLater(StartWindow.getInstance());
        out.println("Welcome to our store");
        Thread.sleep(500);
        readInput();
    }
}
