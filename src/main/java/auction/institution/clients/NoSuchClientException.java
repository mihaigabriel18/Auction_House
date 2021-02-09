package auction.institution.clients;


/**
 * Checked exception, thrown when we try to query a {@link Client} that does not yet exist.
 */
public class NoSuchClientException extends Exception {

    /**
     * {@inheritDoc}
     */
    public NoSuchClientException(String message) {
        super(message);
    }
}
