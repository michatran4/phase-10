package cards;

import phases.ColorSet;
import phases.NumberRun;
import phases.NumberSet;
import phases.Rule;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Middle piles are created once a player advances on to the next phase.
 * They are for cards that are hitting.
 * This class returns if cards can be added to the middle, and adds them if so.
 */
public class MiddlePile {
    private final LinkedList<Card> cards; // linked list should be in order
    private final Rule rule;
    private final boolean DEBUGGING;
    private TreeMap<Integer, Card> run;

    /**
     * Multiple middle piles should be created if a phase has multiple rules,
     * or a count > 1 for the rule.
     *
     * @param dropped cards to be added to the pile
     * @param r       the rule of this pile
     */
    public MiddlePile(LinkedList<Card> dropped, Rule r, boolean b) {
        if (dropped.size() != r.getNumCards()) {
            throw new IllegalArgumentException();
        }
        for (Card card: dropped) {
            if (card == null) { // skips already checked
                throw new IllegalStateException();
            }
        }
        cards = new LinkedList<>(dropped);
        rule = r;
        DEBUGGING = b;
        if (r instanceof NumberRun) {
            createRunMap();
        }
    }

    /**
     * Gets a single card with a number after being checked.
     * Number runs have one card of each number (except for wilds).
     *
     * @param number the number to get
     * @return the card
     */
    private Card getCardWithNum(int number) {
        if (DEBUGGING) {
            System.out.println("Number run remove: " + number);
            System.out.println("CARDS: " + cards.toString());
        }
        for (Card card: cards) {
            if (DEBUGGING) {
                System.out.println("new iteration: " + card.getNum());
            }
            if (card.getNum() == number) {
                return card;
            }
            if (DEBUGGING) {
                System.out.println("Failed comparison!");
            }
        }
        return null;
    }

    /**
     * Use a tree map for number runs instead of the cards linked list.
     */
    private void createRunMap() {
        run = new TreeMap<>();
        /*
        - Count the wild cards.
        If there are 0 wild cards, then this is a run without wild cards. Make it.
        Else,
        - Go from the minimum non-wild number until wilds run out, or until you reach 12
        - The remainder wild cards are subtracted from the minimum.
         */
        TreeMap<Integer, Integer> histogram = new TreeMap<>();
        for (Card c: cards) {
            histogram.merge(c.getNum(), 1, Integer::sum);
        }
        int wildCards = 0;
        if (histogram.containsKey(14)) {
            wildCards = histogram.get(14);
        }
        int minimum = histogram.keySet().iterator().next();
        if (DEBUGGING) {
            System.out.println("New run map created.");
            System.out.println("Minimum: " + minimum);
        }
        if (wildCards == 0) {
            for (int i = minimum; i < minimum + rule.getNumCards(); i++) {
                Card card = getCardWithNum(i);
                if (card == null) throw new IllegalStateException();
                run.put(i, card);
                if (DEBUGGING) {
                    System.out.println("New run key (0): " + i);
                }
            }
        }
        else {
            for (int i = minimum; i < minimum + rule.getNumCards(); i++) { // adds including minimum
                if (DEBUGGING) {
                    System.out.println("Checking numbers loop");
                }
                Card card = getCardWithNum(i);
                if (card == null) {
                    if (wildCards < 1) throw new IllegalStateException();
                    run.put(i, new Card("WILD"));
                    wildCards--;
                    if (DEBUGGING) {
                        System.out.println("New run key (Card not found, used wild): " + i);
                    }
                }
                else {
                    run.put(i, card);
                    if (DEBUGGING) {
                        System.out.println("New run key (2): " + i);
                    }
                }
            }
            // remaining wild cards that didn't just fill in gaps
            // start at minimum - 1 because minimum is already added
            for (int j = minimum - 1; wildCards > 0; j--) {
                Card wild = getCardWithNum(14);
                if (wild == null) throw new IllegalStateException();
                run.put(j, wild);
                wildCards--;
                if (DEBUGGING) {
                    System.out.println("New run key (3): " + j);
                }
            }
        }
        if (DEBUGGING) {
            System.out.println("Run map: " + run);
        }
    }

    public Set<Integer> getAvailableNums() {
        Set<Integer> unfilled = new TreeSet<>();
        Set<Integer> filled = run.keySet();
        if (DEBUGGING) {
            System.out.println("Filled numbers: " + run.keySet());
        }
        for (int i = 1; i <= 12; i++) {
            if (!filled.contains(i)) {
                unfilled.add(i);
            }
        }
        return unfilled;
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
        throw new IllegalStateException("A stack must contain a non wild card.");
    }

    public Card getFirstNormalCard() {
        return cards.get(getIndexOfFirstNormalCard());
    }

    /**
     * Add a card to the middle pile.
     *
     * @param toAdd card to add
     * @param add   add the card to pile, or if it's just a peek test
     * @return if the card was added successfully (or addable)
     */
    public boolean addCard(Card toAdd, boolean add) { // TODO test
        if (DEBUGGING) {
            System.out.println("Middle pile: " + this);
            if (!add) System.out.println("Test add");
            System.out.println("adding card: " + toAdd.toString());
        }
        if (toAdd.toString().equals("SKIP")) {
            throw new IllegalStateException("Cannot add skips.");
        }
        if (rule instanceof NumberSet || rule instanceof ColorSet) {
            if (DEBUGGING) {
                System.out.println("Checking " + rule.getClass());
                System.out.println(rule);
            }
            if (toAdd.toString().equals("WILD")) {
                if (add) cards.add(toAdd);
                return true;
            }

            Card example = getFirstNormalCard();
            if (DEBUGGING) {
                System.out.println("EXAMPLE: " + example.toString());
            }
            if (rule instanceof NumberSet) {
                if (example.getNum() == toAdd.getNum()) {
                    if (add) cards.add(toAdd);
                    return true;
                }
            }
            if (rule instanceof ColorSet) {
                if (example.getColor().equals(toAdd.getColor())) {
                    if (add) cards.add(toAdd);
                    return true;
                }
            }
            if (DEBUGGING) {
                System.out.println("Failed checks here.");
            }
        }
        else { // number run requires checking bounds before randomly adding cards
            if (DEBUGGING) {
                System.out.println("Number run checking: " + toAdd);
            }
            if (cards.size() == 12) {
                if (DEBUGGING) {
                    System.out.println("Unavailable pile.");
                }
                return false;
            }

            // start and end are inclusive
            if (toAdd.toString().equals("WILD")) {
                for (int i = 1; i <= 12; i++) {
                    if (!run.containsKey(i)) {
                        if (add) {
                            run.put(i, toAdd);
                            int index = 0; // add it in cards in order
                            for (int compare: run.keySet()) {
                                if (compare == i) {
                                    break;
                                }
                                index++;
                            }
                            cards.add(index, toAdd);
                            if (DEBUGGING) {
                                System.out.println("Added to run map (0): " + run);
                                System.out.println(cards);
                            }
                        }
                        return true;
                    }
                }
                throw new IllegalStateException("Should be in bounds.");
            }
            int num = toAdd.getNum();
            if (!run.containsKey(num)) {
                if (add) {
                    run.put(num, toAdd);
                    int index = 0; // add it in cards in order
                    for (int compare: run.keySet()) {
                        if (compare == num) {
                            break;
                        }
                        index++;
                    }
                    cards.add(index, toAdd);
                    if (DEBUGGING) {
                        System.out.println("Added to run map (1): " + run);
                        System.out.println(cards);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Rule getRule() {
        return rule;
    }

    public String toString() {
        if (cards.size() == 0) return "[]";
        StringBuilder output = new StringBuilder("[");
        for (Card c: cards) {
            output.append(c.toString());
            output.append(", ");
        }
        output = new StringBuilder(output.substring(0, output.length() - 2));
        output.append("]");
        return output.toString();
    }
}
