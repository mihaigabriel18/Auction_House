package gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.swing.*;

import static javax.swing.WindowConstants.*;

/**
 * Start window frame, graphical class, starts a new frame on a separate thread,
 * Contains a panel with the active auctions and a panel with the existing products
 * that the store has available to sell.
 * This is also a singleton, only one of this panels are available to the user,
 * only one instance being available.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class StartWindow implements Runnable {

    /**
     * Panel with active auction threads, waiting to start
     */
    ThreadPanel threadPanel;

    /**
     * Panel with available products on the auction house's stock
     */
    ProductDeposit productDeposit;

    JFrame frame;

    /**
     * Private constructor for singleton class
     */
    private StartWindow() {

    }

    /**
     * Initializes the frame characteristics, the {@link #threadPanel} and {@link #productDeposit}
     * {@inheritDoc}
     */
    @Override
    public void run() {
        this.frame = new JFrame("Auction House");
        this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.frame.setSize(900,600);

        this.threadPanel = new ThreadPanel();
        this.productDeposit = new ProductDeposit();

        // add thread panel to main window
        this.frame.add(threadPanel.getPanel());
        // start the thread panel as a separate thread
        (new Thread(threadPanel)).start();

        this.frame.add(productDeposit.getPanel());

        (new Thread(productDeposit)).start();


        // make panel visible
        this.frame.setLayout(null);
        this.frame.setVisible(true);
        this.frame.setResizable(false);
    }

    /**
     * Inner static class that hols the only instance of the singleton,
     * assures thread-safe singleton implementation
     */
    private static class ReferenceHolder {
        public static final StartWindow INSTANCE = new StartWindow();
    }

    /**
     * Retrieve the only instance of the thread-safe singleton
     * @return the only {@link StartWindow} instance
     */
    public static StartWindow getInstance() {
        return ReferenceHolder.INSTANCE;
    }
}
