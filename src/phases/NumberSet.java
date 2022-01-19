package phases;

/**
 * This is a rule where cards must have the same number.
 */
public class NumberSet extends Rule {
    public NumberSet(int count, int cards) {
        super(count, cards);
    }
    public String toString() {
        return String.format("%d %s of %d", getCount(), getCount() > 1? "sets":"set", getNumCards());
    }
}
