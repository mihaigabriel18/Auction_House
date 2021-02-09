package auction.institution;

import auction.institution.clients.Client;

/**
 * Checked exception, thrown when a {@link Client} tries to join an {@link Auction} that is already full, has
 * no more free slots.
 */
public class AuctionIsFullException extends Exception {

    /**
     * {@inheritDoc}
     */
    public AuctionIsFullException(String message) {
        super(message);
    }
}
