package auction.institution;

/**
 * Checked exception, thrown when a client a requests to join an {@link Auction} that has not yet started.
 */
public class NoSuchAuctionException extends Exception {

    public NoSuchAuctionException(String message) {
        super(message);
    }
}
