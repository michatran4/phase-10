package cards;

import phases.*;
import turns.Turn;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * The CPU deck extends the player deck with CPU moves.
 * This deck should never directly modify the count of the cards in the deck.
 * Instead, it should be using remove methods.
 */
public class CPUDeck extends PlayerDeck { // TODO decide pile to draw from
    // TODO Debugging and print
    public final Map<Card, Integer> deck;

    public CPUDeck() {
        super();
        deck = getDeck();
    }

    private TreeMap<Integer, Integer> getHistogram() {
        TreeMap<Integer, Integer> histogram = new TreeMap<>();
        for (Card c: deck.keySet()) {
            if (histogram.get(c.getNum()) != null) {
                histogram.put(c.getNum(),
                        histogram.get(c.getNum()) + deck.get(c));
            }
            else {
                histogram.put(c.getNum(), deck.get(c));
            }
        }
        return histogram;
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
     * This turn should have no hit cards; it should be determined in
     * external logic.
     *
     * @param phase the phase to have matching cards for
     * @return the next turn containing the dropped cards and the discarded card
     */
    public Turn getNextTurn(Phase phase) {
        LinkedList<Card> dropped = new LinkedList<>();
        if (getSize() - phase.getTotalNumCards() > 0) { // sufficient cards
            // make a histogram for ease of checking
            TreeMap<Integer, Integer> histogram = getHistogram();
            int wildCards = 0; // number of wild cards
            if (histogram.get(14) != null) {
                wildCards = histogram.get(14);
                histogram.remove(14); // ignore wild card usage in main logic
            }
            histogram.remove(13); // ignore skip card usage in main logic
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
                    // TODO test
                    // create a color histogram
                    Map<String, Integer> colors = new TreeMap<>();
                    for (Card card: deck.keySet()) {
                        if (card.getColor() != null) { // ignore skips,
                            // wilds were already counted
                            String c = card.getColor();
                            if (colors.get(c) != null) {
                                colors.put(c, colors.get(c) + deck.get(card));
                            }
                            else {
                                colors.put(c, deck.get(card));
                            }
                        }
                    }
                    for (String color: colors.keySet()) {
                        int count = colors.get(color);
                        /*
                        histogram doesn't matter, it's only one color set.
                        just remove cards
                        */
                        if (count >= rule.getNumCards()) {
                            dropped.addAll(removeCardsWithColor(color,
                                    rule.getNumCards()));
                            // remove cards with a specific color
                            break;
                        }
                        /*
                        no need to check if count is ever 0.
                        this is a histogram, and it's keys have a value of 1+.
                        therefore, solely wildcards will never be used.
                         */
                        // wild card usage because it is insufficient
                        else if (count + wildCards >= rule.getNumCards()) {
                            // TODO test with count cards
                            dropped.addAll(removeCardsWithColor(color, count));
                            int remainder = rule.getNumCards() - count;
                            wildCards -= remainder;
                            dropped.addAll(removeCardsWithNum(14,
                                    remainder));
                            break;
                        }
                    }
                }
                else if (rule instanceof NumberRun) {
                    int foundNum = -1;
                    for (int num: histogram.keySet()) {
                        int wilds = wildCards; // store wilds, decrement test
                        boolean flag = false;
                        for (int i = 0; i < rule.getNumCards(); i++) { //
                            // assumed to only be count of 1
                            // set it to true once a run is broken
                            // or insufficient wilds
                            if (histogram.get(num + i) == null) {
                                // check wilds
                                if (wilds-- == 0) {
                                    flag = true;
                                    break; // avoid checking further runs
                                }
                            }
                        }
                        if (!flag) {
                            foundNum = num;
                            break;
                        }
                    }
                    if (foundNum != -1) {
                        for (int i = 0; i < rule.getNumCards(); i++) {
                            if (histogram.get(foundNum + i) != null) {
                                // histogram modification doesn't matter as
                                // runs are always the last rule
                                dropped.addAll(
                                        removeCardsWithNum(foundNum + i,
                                        1));
                            }
                            else {
                                dropped.addAll(
                                        removeCardsWithNum(14, 1));
                            }
                        }
                    }
                }
            }
        }

        // if there is a successful phase played, let the hit method do the
        // discarding. find a hit, then discard after.
        if (dropped.size() != 0) {
            return new Turn(dropped, null, null);
        }

        Card discard = getDiscardCard();
        deck.remove(discard);
        return new Turn(dropped, discard, null);
    }

    private Card getDiscardCard() {
        Card discard = null;
        if (deck.size() == 0) throw new IllegalStateException();
        if (deck.size() == 1) { // one type of card
            for (Card card: deck.keySet()) {
                discard = card;
                break;
            }
        }
        else {
            // find skip
            TreeMap<Integer, Integer> histogram = getHistogram();
            if (histogram.containsKey(13)) { // contains a skip
                for (Card card: deck.keySet()) {
                    if (card.toString().equals("SKIP")) {
                        discard = card;
                        break;
                    }
                }
            }
            else if (histogram.containsValue(1)) { // one of a kind
                for (Card card: deck.keySet()) {
                    if (deck.get(card) == 1) {
                        discard = card;
                        break;
                    }
                }
            }
            else { // discard a card with the smallest count
                // least likely to have built up a set
                int lowest = Integer.MAX_VALUE;
                for (Card card: deck.keySet()) {
                    if (deck.get(card) < lowest) {
                        lowest = deck.get(card);
                        discard = card;
                    }
                }
            }
        }
        if (discard == null) {
            throw new IllegalStateException();
        }

        return discard;
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
    public Turn getNextTurn(MiddlePileManager middlePileManager) {
        // TODO test
        LinkedList<Card> hit = new LinkedList<>();
        for (MiddlePile middlePile: middlePileManager.getMiddlePiles()) {
            if (getSize() == 1) break;
            if (middlePile.getRule() instanceof NumberRun) {
                int start = middlePile.getStartBound();
                int end = middlePile.getEndBound();
                // add with a loop to each middle pile
                while (start > 1 && getSize() > 1) { // start is at least 2
                    LinkedList<Card> removed = removeCardsWithNum(start - 1, 1);
                    if (removed.size() == 1) {
                        hit.addAll(removed);
                        start--;
                    }
                    else {
                        break;
                    }
                }
                while (end < 12 && getSize() > 1) { // end is at most 11
                    LinkedList<Card> removed = removeCardsWithNum(end + 1, 1);
                    if (removed.size() == 1) {
                        hit.addAll(removed);
                        end++;
                    }
                    else {
                        break;
                    }
                }
            }
            else { // add all the cards that are the same, typical case
                Card normal = middlePile.getFirstNormalCard();
                if (middlePile.getRule() instanceof ColorSet) {
                    hit.addAll(removeCardsWithColor(normal.getColor()));
                }
                if (middlePile.getRule() instanceof NumberSet) {
                    hit.addAll(removeCardsWithNum(normal.getNum()));
                }
                if (deck.size() == 0) {
                    addCard(hit.poll());
                }
            }
        }

        Card discard = getDiscardCard();
        deck.remove(discard);
        return new Turn(null, discard, hit);
    }
}
