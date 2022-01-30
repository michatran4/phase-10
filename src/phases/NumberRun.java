package phases;

/**
 * This is a rule where cards must have numbers in sequential order.
 * The code is based off of it only having a count of 1 (default phases).
 */
public class NumberRun extends Rule {
    public NumberRun(int count, int cards) {
        super(count, cards);
    }
    public String toString() {
        return String.format("%d run of %d", getCount(), getNumCards());
    }
}
