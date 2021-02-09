package auction.institution.bidding_algorithm;

import lombok.AllArgsConstructor;

/**
 * Each specific algorithm that is going to implement the {@link Strategy} interface
 * will be instantiated with the aid of this class.
 */
@AllArgsConstructor
public class BidAlgorithm {

    private final Strategy strategy;

    /**
     * Calls the specific algorithm method that is going to be used to calculate the bid
     * @param constrains constrains to calculate the bid with
     * @return the bid
     */
    public int calculateBid(int... constrains) {
        return strategy.applyAlgorithm(constrains);
    }
}
