package auction.institution.bidding_algorithm.algorithms;

import auction.institution.bidding_algorithm.Strategy;


/**
 * Calculates the bid using a specific value, used when the bid is given parameter from the command line
 * interface, the constrains parameter will be just a single integer
 */
public class ConcreteBidFromCommand implements Strategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public int applyAlgorithm(int... constrains) {
        return constrains[0];
    }
}
