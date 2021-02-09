package auction.institution.bidding_algorithm;

/**
 * Interface that is going to be implemented by every concrete algorithm class
 * Every algorithm will use this patent to declare new algorithms for calculating new bids,
 * new bidding algorithms can be added they just have to respect this method's patent
 * This is the core of the <strong>strategy pattern</strong> i have implemented
 */
public interface Strategy {

    /**
     * Calculates the bid using a specific algorithm
     * @param constrains variable number of parameters used to bound or help calculate the bid
     * @return integer value of the bid
     */
    int applyAlgorithm(int... constrains);
}
