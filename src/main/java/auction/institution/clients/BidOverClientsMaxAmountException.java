package auction.institution.clients;

/**
 * If a bid above the clients maximum amount is given, this exception is thrown
 */
public class BidOverClientsMaxAmountException extends Exception {

    public BidOverClientsMaxAmountException(String message) {
        super(message);
    }
}
