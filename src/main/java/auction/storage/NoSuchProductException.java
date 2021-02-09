package auction.storage;

/**
 * Checked exception, thrown when a client a requests a {@link Product} that is not in the database.
 */
public class NoSuchProductException extends Exception {

    public NoSuchProductException(String message) {
        super(message);
    }
}
