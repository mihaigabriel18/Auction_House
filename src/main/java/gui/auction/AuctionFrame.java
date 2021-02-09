package gui.auction;

import auction.institution.Auction;
import auction.institution.clients.Client;
import auction.institution.employee.broker.Broker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import javax.swing.*;

import java.awt.*;

import static javax.swing.JFrame.*;
import static javax.swing.JScrollPane.*;


/**
 * The swing frame with an auction info, since the auction runs on a separate thread this does not have to,
 * it will run on the auction's thread.
 * Will hold a panel with logs from the auction (log messages for clients giving bids, brokers receiving their
 * bids and winner of the auction), a panel with clients subscribed to this auction with their associated brokers
 * and a button that will start the auction process
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public class AuctionFrame {

    /**
     * Auction associated with this frame
     */
    Auction auction;

    JTextArea textArea;

    JScrollPane scrollPane;

    /**
     * List of clients with their associated brokers
     */
    JList<String> clientsAndBrokers;

    DefaultListModel<String> model;

    /**
     * start the bidding process
     */
    JButton startBidding;

    JButton hideThePannel;

    JFrame frame;

    public AuctionFrame(Auction auction) {
        this.auction = auction;
        frame = new JFrame("Auction " + auction.getId());
        frame.setSize(1200, 600);
        JPanel panel = new JPanel();
        textArea = new JTextArea();
        textArea.setEditable(false);

        panel.add(textArea);

        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 50, 400, 500);

        //
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(1200, 600));
        contentPane.setBounds(10,50,1200,600);
        contentPane.setLayout(null);
        contentPane.add(scrollPane);

        // right side
        clientsAndBrokers = new JList<>();
        model = new DefaultListModel<>();
        clientsAndBrokers.setBounds(700, 50, 450, 500);
        clientsAndBrokers.setModel(model);
        contentPane.add(clientsAndBrokers);

        JTextPane title = new JTextPane();
        title.setText("Clients and their associated brokers");
        title.setBounds(700,20, 450, 20);
        title.setBackground(Color.CYAN);
        title.setEditable(false);
        contentPane.add(title);
        // end right side

        startBidding = new JButton("Start Bidding Process");
        startBidding.addActionListener(new GButtonListener(auction));
        startBidding.setBounds(450, 100, 200,50);
        contentPane.add(startBidding);

        hideThePannel = new JButton("End the auction");
        hideThePannel.addActionListener(new HideFrameButtonListener(this, auction));
        hideThePannel.setBounds(450, 300, 200,50);
        hideThePannel.setVisible(false);
        contentPane.add(hideThePannel);

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(false); // initially not visible
    }

    public void makeCloseButtonVisible() {
        hideThePannel.setVisible(true);
    }

    /**
     * Make the auction frame visible when the auction starts
     */
    public void makeFrameVisible() {
        frame.setVisible(true);
    }

    public void hideFrame() {
        frame.setVisible(false);
    }

    /**
     * Log auction info
     * @param data data to add to the text box
     */
    public void logAuctionInfo(String data) {
        textArea.append(data + "\n");
    }

    /**
     * Log the client and his broker
     * @param client client to log
     * @param broker broker to log
     */
    public void logClientAndBroker(Client client, Broker broker) {
        String format = "Client " + client.getName() + " with id " + client.getId() + " has "
                + broker.getName() + " as a broker";
        model.addElement(format);
    }
}
