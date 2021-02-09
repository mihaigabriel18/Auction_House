package gui;

import auction.storage.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel with active auctions that have started / are able to be joined by the end user.
 * It runs on e separate thread, waiting for the the {@link auction.institution.AuctionHouse} to
 * start a new auction, and the panel to add it to the list
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ThreadPanel implements Runnable {

    /**
     * Will contain all java swing components of this class
     */
    @Getter
    JPanel panel;

    JTextPane title;

    /**
     * List of active auctions
     */
    JList<String> activeAuctions;

    DefaultListModel<String> model;

    /**
     *
     */
    @NonFinal
    @Setter
    @Getter
    Integer productId;

    /**
     * Nr of clients subscribed to an auction
     */
    @NonFinal
    @Setter
    @Getter
    int nrSubscribed;

    /**
     * Nr of required clients to start an auction
     */
    @NonFinal
    @Setter
    @Getter
    int nrRequired;

    /**
     * Initializes all panel's components
     */
    public ThreadPanel() {
        this.panel = new JPanel();
        this.panel.setBounds(10,10,270, 550);
        this.panel.setBackground(Color.BLACK);
        this.panel.setLayout(null);

        this.title = new JTextPane();
        this.title.setText("Active Auctions");
        this.title.setBounds(10,20, 250, 20);
        this.title.setBackground(Color.CYAN);
        this.title.setEditable(false);

        activeAuctions = new JList<>();
        model = new DefaultListModel<>();
        activeAuctions.setBounds(10, 50, 250, 480);
        activeAuctions.setBackground(Color.WHITE);
        activeAuctions.setModel(model);

        panel.add(title);
        panel.add(activeAuctions, "Center");
    }


    /**
     * It will wait for the {@link auction.institution.AuctionHouse} to give the signal that
     * a new auction has started, after that a new item will be added to the list
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public synchronized void run() {

        while(Thread.currentThread().isAlive()) {
            this.wait(); // put a lock on the panel
            String format;
            // build the format from the renewed class fields
            if (nrRequired == nrSubscribed) { // format for starting auction
                format = "Auction for product " + productId + " has started";
            }
            else // format for not yet full auction
                format = "Prod id: " + productId + ", " + nrSubscribed + "/" + nrRequired + " subscribed";
            // if product with Id is already in list remove it and put it back there again
            if (isInJList(productId)) {
                removeJListElement(productId);
            }
            model.addElement(format);
        }
    }

    private boolean isInJList(int productId) {
        List<String> prodIds = Arrays.stream(model.toArray())
                .map(name -> ((String) name).split("[ ,]")[2])
                .collect(Collectors.toList());
        return prodIds.stream().anyMatch(prodId -> {
            Integer aux = null;
            try {
            aux = Integer.parseInt(prodId);
            } catch (NumberFormatException e) {
                // formats for auction that have already started will throw an exception and will need to be omitted
            }
            Integer productIdBoxed = productId;
            return productIdBoxed.equals(aux);
        });
    }

    private void removeJListElement(int productId) {
        int index;
        // get index of element
        for (index = 0; index < model.size(); index++) {
            Integer modelInt = null;
            try {
            modelInt = Integer.parseInt(model.get(index).split("[ ,]")[2]);
            } catch (NumberFormatException e) {
                // formats for auction that have already started will throw an exception and will need to be omitted
            }
            Integer productIdBoxed = productId;
            if (productIdBoxed.equals(modelInt))
                break;
        }
        model.remove(index);
    }

    public void removeJListElement(Product product) {
        int index;
        // get index of element
        for (index = 0; index < model.size(); index++) {
            int modelInt = Integer.parseInt(model.get(index).split(" ")[3]);
            if (product.getId().equals(modelInt))
                break;
        }
        model.remove(index);
    }
}
