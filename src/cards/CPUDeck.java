package cards;

import phases.*;
import turns.Turn;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class CPUDeck extends PlayerDeck { // TODO decide pile to draw from
    // TODO Debugging and print
    public final Map<Card, Integer> deck;

    public CPUDeck() {
        super();
        deck = getDeck();
    }

    /**
     * The next turn to play for the CPU.
     * Find cards that match the phase, and make sure it has at least one
     * discard card remaining. If there are no excess cards, then do not play
     * the phase.
     *
     * If there is one remaining card, then that is the discard card.
     * Else, the default discard card is a skip card.
     * Else, find a card that is one of a kind.
     * Else, discard a card at random.
     *
     * This turn has no hit cards.
     *
     * @param phase the phase to have good cards for
     * @return the next turn containing the dropped cards and the discarded card
     */
    public Turn getNextTurn(Phase phase) {
        LinkedList<Card> dropped = new LinkedList<>();
        if (getSize() - phase.getTotalNumCards() > 0) { // sufficient cards
            // make a histogram for ease of checking
            Map<Integer, Integer> histogram = new TreeMap<>();
            for (Card c: deck.keySet()) {
                if (histogram.get(c.getNum()) != null) {
                    histogram.put(c.getNum(),
                            histogram.get(c.getNum()) + deck.get(c));
                }
                else {
                    histogram.put(c.getNum(), deck.get(c));
                }
            }
            int wildCards = 0; // number of wild cards
            if (histogram.get(14) != null) {
                wildCards = histogram.get(14);
                histogram.put(14, 0); // ignore wild card usage in main logic
            }
            for (Rule rule: phase.getRules()) {
                if (rule instanceof NumberSet) {
                    int ruleCount = rule.getCount();
                    for (int num: histogram.keySet()) { // get sets
                        int count = histogram.get(num);
                        // do not exceed the amount of cards available
                        if (count >= rule.getNumCards()) {
                            // difference will always be >= 0
                            histogram.put(num, count - rule.getNumCards());
                            dropped.addAll(removeCardsWithNum(num,
                                    rule.getNumCards()));
                            // remove cards of a specific number
                            if (--ruleCount == 0) break;
                        }
                        /*
                        no need to check if count is ever 0.
                        this is a histogram, and it's keys have a value of 1+.
                        therefore, solely wildcards will never be used.
                         */
                        // wild card usage because it is insufficient
                        else if (count + wildCards >= rule.getNumCards()) {
                            // TODO test with count cards
                            int decrement = rule.getNumCards() - count;
                            dropped.addAll(removeCardsWithNum(num, count));
                            wildCards -= decrement;
                            dropped.addAll(removeCardsWithNum(14,
                                    decrement));
                            histogram.put(num, 0); // just werks
                            if (--ruleCount == 0) break;
                        }
                    }
                    if (ruleCount != 0) { // insufficient
                        // add back cards. there weren't enough to complete
                        for (Card c: dropped) { // TODO test
                            addCard(c);
                        }
                        dropped.clear();
                        break; // avoid checking another rule
                    }
                }
                else if (rule instanceof ColorSet) { // remove cards of color
                    int count = rule.getNumCards();

                    // remove wild cards
                }
                else if (rule instanceof NumberRun) {
                    // remove cards & wilds
                }
            }
        }
        // we know that if dropped is empty then there is no drop for the turn
        // do hits

        return null;
    }

    /**
     * This next turn is a hit. Hits are after phases are completed.
     * Piles will return true if a card can be placed.
     * All cards are checked to see if they can be hit. This should be the same
     * algorithm as the turn validator.
     * Runtime shouldn't be too much of a concern since it isn't that many
     * cards.
     * However, this can be managed by having a list of checked and unchecked
     * cards until the middle pile count changes.
     *
     * This turn has no dropped cards.
     *
     * @param middlePileManager all the middle piles
     * @return the next turn containing the cards that can be hit and the
     * discarded card
     */
    public Turn getNextTurn(MiddlePileManager middlePileManager) { // TODO test
        LinkedList<Card> hit = new LinkedList<>();
        for (Card card: deck.keySet()) {
            for (MiddlePile middlePile: middlePileManager.getMiddlePiles()) {
                if (middlePile.addCard(card, false)) { // successful add
                    if (middlePile.getRule() instanceof NumberRun) {
                        // special case, check multiple of the same card
                        int extra = 0; // how many more of the card to add,
                        // try with (extra + 1)

                        // TODO make sure it keeps one remaining card to discard
                    }
                    else { // add all the cards that are the same, typical case

                        // TODO make sure it keeps one remaining card to discard
                    }
                }
            }
        }
        return null;
    }
}
