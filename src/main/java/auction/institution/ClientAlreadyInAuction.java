package auction.institution;

import auction.institution.clients.Client;

/**
 * Checked exception, thrown when we try to register a {@link Client} into an {@link Auction} where he is
 * already registered before
 */
public class ClientAlreadyInAuction extends Exception {

    public ClientAlreadyInAuction(String message) {
        super(message);
    }
}
