package phases;

/**
 * This serves as a rule template.
 */
public class Rule {
    private final int count, numCards;

    /**
     * Create a new rule.
     *
     * @param count    how many of this rule
     * @param numCards how many cards in the set/run
     */
    public Rule(int count, int numCards) {
        this.count = count;
        this.numCards = numCards;
    }

    public int getCount() {
        return count;
    }

    public int getNumCards() {
        return numCards;
    }
}
