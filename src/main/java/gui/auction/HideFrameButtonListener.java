package gui.auction;

import auction.institution.Auction;
import gui.StartWindow;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This button closes the auction frame, only visible after an auction has ended
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HideFrameButtonListener implements ActionListener {

    AuctionFrame auctionFrame;

    Auction auction;

    public HideFrameButtonListener(AuctionFrame auctionFrame, Auction auction) {
        this.auctionFrame = auctionFrame;
        this.auction = auction;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        auctionFrame.hideFrame();
        StartWindow.getInstance().getThreadPanel().removeJListElement(auction.getProductToSale());
    }
}
