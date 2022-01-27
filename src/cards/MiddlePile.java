package cards;

import phases.*;

import java.util.LinkedList;

public class MiddlePile {
    private final LinkedList<Card> cards; // linked list should be in order
    private final Rule rule;

    /**
     * Multiple middle piles should be created if a phase has multiple rules,
     * or a count > 1 for the rule.
     *
     * @param dropped cards to be added to the pile
     * @param r       the rule of this pile
     */
    public MiddlePile(LinkedList<Card> dropped, Rule r) {
        cards = new LinkedList<>(dropped);
        rule = r;
    }

    private int getIndexOfFirstNormalCard() {
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (c.toString().equals("SKIP")) {
                throw new IllegalStateException("Skips should not exist in a middle pile."); // just double-checking
            }
            if (!c.toString().equals("WILD")) {
                return i;
            }
        }
        throw new IllegalStateException("Should not have multiple wilds in a stack.");
    }

    private int getIndexOfLastNormalCard() {
        for (int i = cards.size() - 1; i > -1; i--) {
            Card c = cards.get(i);
            if (c.toString().equals("SKIP")) {
                throw new IllegalStateException("Skips should not exist in a middle pile."); // just double-checking
            }
            if (!c.toString().equals("WILD")) {
                return i;
            }
        }
        throw new IllegalStateException("Should not have multiple wilds in a stack.");
    }

    private Card getFirstNormalCard() {
        return cards.get(getIndexOfFirstNormalCard());
    }

    private Card getLastNormalCard() {
        return cards.get(getIndexOfLastNormalCard());
    }

    public void addCard(Card toAdd) {
        if (rule instanceof NumberSet || rule instanceof ColorSet) {
            if (toAdd.toString().equals("WILD")) {
                cards.add(toAdd);
                return;
            }
            else if (toAdd.toString().equals("SKIP")) {
                System.out.println("Shouldn't be putting down skips.");
                cards.add(toAdd);
                return;
            }

            Card example = getFirstNormalCard();
            if (rule instanceof NumberSet) {
                if (example.getNum() == toAdd.getNum()) {
                    cards.add(toAdd);
                    return;
                }
            }
            if (rule instanceof ColorSet) {
                if (example.getColor() == toAdd.getColor()) {
                    cards.add(toAdd);
                    return;
                }
            }

            throw new IllegalArgumentException(); // TODO get rid of this?
        }
        else { // number run
            // find the bounds of the pile
            int first = getIndexOfFirstNormalCard();
            // this the amount wilds that go before the number. calculate what the first wild's number actually is
            int start = getFirstNormalCard().getNum() - first;

            int last = getIndexOfLastNormalCard();
            int end = cards.size() - 1 - last; // TODO document and fix numbers
        }
    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public Rule getRule() {
        return rule;
    }
}
