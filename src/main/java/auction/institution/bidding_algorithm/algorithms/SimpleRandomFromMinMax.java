package auction.institution.bidding_algorithm.algorithms;

import auction.institution.bidding_algorithm.Strategy;

import java.util.Random;

/**
 * Calculated a bid using a simple random algorithm, the constrains being a minimum and a maximum
 * to get a random in between them
 */
public class SimpleRandomFromMinMax implements Strategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public int applyAlgorithm(int... constrains) {
        int min = constrains[0];
        int max = constrains[1];
        return min + (max - min) / ((new Random()).nextInt(10) + 1);
    }
}
