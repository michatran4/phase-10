package cards;

import phases.*;
import turns.Turn;

import java.util.*;

/**
 * The CPU deck extends the player deck with CPU moves.
 * This deck should NEVER directly modify the count of the cards in the deck.
 * Instead, it should be using remove methods.
 */
public class CPUDeck extends PlayerDeck { // TODO decide pile to draw from
    public final Map<Card, Integer> deck;
    private final boolean DEBUGGING;

    public CPUDeck(boolean b) {
        super(b);
        deck = getDeck();
        DEBUGGING = b;
    }

    /**
     * Make a histogram for the sole purpose of number set checking.
     *
     * @return a histogram of the card numbers
     */
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
     * <p>
     * If there is one remaining card, then that is the discard card.
     * Else, the default discard card is a skip card.
     * Else, find a card that is one of a kind.
     * Else, discard a card at random.
     * <p>
     * This turn should have no hit cards; it should be determined in
     * external logic.
     *
     * @param phase the phase to have matching cards for
     * @return the next turn containing the dropped cards and the discarded card
     */
    public Turn getNextTurn(Phase phase) {
        if (DEBUGGING) {
            System.out.println("Finding the next turn for phase: " + phase.toString());
            System.out.println("Rule count: " + phase.getRules().size());
        }
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
                if (DEBUGGING) {
                    System.out.println(rule.toString());
                }
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
                        for (Card c: dropped) {
                            addCard(c);
                        }
                        dropped.clear();
                        break; // avoid checking another rule
                    }
                    // prune before number runs are checked
                    //noinspection StatementWithEmptyBody
                    while (histogram.values().remove(0)) ;
                }
                else if (rule instanceof ColorSet) { // remove cards of color
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
                    if (DEBUGGING) {
                        System.out.println("Finding a number run.");
                    }
                    // ensure order is kept
                    LinkedList<Card> runDrop = new LinkedList<>();
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
                                runDrop.addAll(removeCardsWithNum(foundNum + i, 1));
                            }
                            else {
                                if (foundNum + i > 12) {
                                    runDrop.addAll(0, removeCardsWithNum(14, 1));
                                }
                                else {
                                    runDrop.addAll(removeCardsWithNum(14, 1));
                                }
                            }
                        }
                        dropped.addAll(runDrop);
                    }
                    else {
                        for (Card c: dropped) {
                            addCard(c);
                        }
                        dropped.clear();
                    }
                }
            }
        }

        // the only removing card operations should've been for dropped cards.
        // however, there should always be enough cards to discard
        if (getSize() == 0) {
            throw new IllegalStateException();
        }

        // if there is a successful phase played, let the hit method do the
        // discarding. find a hit, then discard after.
        if (dropped.size() != 0) {
            if (DEBUGGING) {
                System.out.println("Returning a set of dropped cards.");
            }
            return new Turn(dropped, null, new LinkedList<>());
        }

        Card discard = getDiscardCard(phase);
        removeCard(discard); // DO NOT DIRECTLY MODIFY THE DECK
        if (DEBUGGING) {
            System.out.println("Discarding " + discard + ", no phase played.");
        }
        return new Turn(dropped, discard, new LinkedList<>());
    }

    private Card getDiscardCard(Phase phase) {
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
            else {
                if (phase == null || phase.getRules().getLast() instanceof NumberSet) {
                    // null phase indicates hitting
                    // discard the lowest counts for number sets; they're unlikely to build a set.
                    /* TODO better AI so it doesn't get stuck on phase 7
                    if (histogram.containsValue(1)) { // one of a kind
                        for (Card card: deck.keySet()) {
                            if (deck.get(card) == 1) {
                                discard = card;
                                break;
                            }
                        }
                    }
                    else {
                        int cardNum = -1;
                        int lowestCount = Integer.MAX_VALUE;
                        for (int num: histogram.keySet()) {
                            if (histogram.get(num) < lowestCount) {
                                lowestCount = histogram.get(num);
                                cardNum = num;
                                if (lowestCount == 1) {
                                    throw new IllegalStateException();
                                }
                            }
                        }
                        if (cardNum == -1) throw new IllegalStateException();
                        for (Card card: deck.keySet()) {
                            if (card.getNum() == cardNum) {
                                discard = card;
                                break;
                            }
                        }
                    }
                     */
                    // discard a random card that isn't a wild
                    ArrayList<Card> set = new ArrayList<>(deck.keySet());
                    set.remove(new Card("WILD"));
                    int item = new Random().nextInt(set.size());
                    discard = set.get(item);
                }
                else {
                    // number runs and color runs require opposite strategy, discard the highest
                    // keep wilds, however
                    int cardNum = -1;
                    int highestCount = Integer.MIN_VALUE;
                    for (int num: histogram.keySet()) {
                        if (cardNum != -1 && num == 14) {
                            break;
                        }
                        if (histogram.get(num) > highestCount) {
                            highestCount = histogram.get(num);
                            cardNum = num;
                        }
                    }
                    if (cardNum == -1) throw new IllegalStateException();
                    for (Card card: deck.keySet()) {
                        if (card.getNum() == cardNum) {
                            discard = card;
                            break;
                        }
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
     * <p>
     * This turn has no dropped cards.
     *
     * @param middlePileManager all the middle piles
     * @return the next turn containing the cards that can be hit and the
     * discarded card
     */
    public Turn getNextTurn(MiddlePileManager middlePileManager) {
        LinkedList<Card> hit = new LinkedList<>();
        for (MiddlePile middlePile: middlePileManager.getMiddlePiles()) {
            if (getSize() == 1) break; // one card remaining no hits, just discard
            Rule rule = middlePile.getRule();
            if (rule instanceof NumberRun) { // must be in bounds
                TreeMap<Integer, Integer> histogram = getHistogram();
                histogram.remove(13); // ignore skip card usage in main logic
                if (DEBUGGING) {
                    System.out.println("New middle pile with number run: " + middlePile);
                    System.out.println("Available nums: " + middlePile.getAvailableNums());
                    System.out.println("Current deck: " + deck);
                }
                // find available numbers in the middle pile to fill with the current deck
                Set<Integer> set = middlePile.getAvailableNums();
                for (int i: set) {
                    if (histogram.containsKey(i)) { // if card exists
                        if (histogram.get(i) < 1) throw new IllegalStateException();
                        histogram.put(i, histogram.get(i) - 1);
                        hit.addAll(removeCardsWithNum(i, 1));
                        if (DEBUGGING) {
                            System.out.println("adding middle: " + hit.getLast().toString());
                        }
                    }
                    else if (histogram.containsKey(14) && histogram.get(14) > 0) { // wilds too
                        histogram.put(14, histogram.get(14) - 1);
                        hit.addAll(removeCardsWithNum(14, 1));
                        if (DEBUGGING) {
                            System.out.println("adding wild to middle: " + hit.getLast().toString());
                        }
                    }
                }
                // prune before more middle piles are checked
                //noinspection StatementWithEmptyBody
                while (histogram.values().remove(0)) ;
            }
            else { // add all the cards that are the same, typical case
                if (DEBUGGING) {
                    System.out.println("New middle pile: " + middlePile);
                    System.out.println("Current deck: " + deck);
                }
                Card normal = middlePile.getFirstNormalCard();
                if (rule instanceof ColorSet) {
                    hit.addAll(removeCardsWithColor(normal.getColor(), Integer.MAX_VALUE));
                }
                if (rule instanceof NumberSet) {
                    hit.addAll(removeCardsWithNum(normal.getNum(), Integer.MAX_VALUE));
                }
            }
        }
        // check to have at least one card to discard
        if (deck.size() == 0) {
            if (DEBUGGING) {
                System.out.println("adding card back to discard");
            }
            addCard(hit.poll());
        }
        Card discard = getDiscardCard(null);
        removeCard(discard); // DO NOT DIRECTLY MODIFY THE DECK
        return new Turn(new LinkedList<>(), discard, hit);
    }
}
