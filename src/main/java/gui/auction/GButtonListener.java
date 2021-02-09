package gui.auction;

import auction.institution.Auction;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Once clicked will start the auction process. Will make the auction object wait
 * for the button to be clicked
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GButtonListener implements ActionListener {

    /**
     * the auction associated with the button
     */
    Auction auction;

    public GButtonListener(Auction auction) {
        this.auction = auction;
    }

    /**
     * Will notify the auction object to start the auction process
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        auction.initializeStartFlag();
        synchronized (auction) {
            auction.notifyAll();
        }
    }
}
