package phases;

/**
 * This serves as a rule template.
 */
public class Rule {
    private final int count, numCards;

    /**
     * Create a new rule.
     * @param a how many of this rule
     * @param b how many cards in the set/run
     */
    public Rule(int a, int b) {
        count = a;
        numCards = b;
    }
    public int getCount() {
        return count;
    }
    public int getNumCards() {
        return numCards;
    }
}
