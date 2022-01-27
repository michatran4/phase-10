package phases;

/**
 * This is a rule where there is an amount of cards of one color.
 * The goal is just to collect one color, so the count will always be one.
 */
public class ColorSet extends Rule {
    public ColorSet(int cards) {
        super(1, cards);
    }

    public String toString() {
        return getNumCards() + " cards of one color";
    }
}
