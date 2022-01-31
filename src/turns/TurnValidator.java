package turns;

import cards.Card;
import cards.MiddlePile;
import cards.MiddlePileManager;
import phases.NumberRun;
import phases.NumberSet;
import phases.Phase;
import phases.Rule;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Since middle piles already check hit cards, just double check if the cards are valid.
 * This is mostly for the player, as the algorithm should be similar in the CPU deck.
 * The turn validator acts as a double checker for turns.
 * Therefore, turns should have perfect dropped cards.
 */
public class TurnValidator {
    private final boolean DEBUGGING;

    public TurnValidator() {
        DEBUGGING = false;
    }

    public TurnValidator(boolean b) {
        DEBUGGING = b;
    }

    /**
     * Validates dropped cards based on the phase.
     * The only concern is the multiple rule phases,
     * where number sets and runs are together.
     * However, this is taken care of by removing the cards after they are checked.
     *
     * @param turn the turn to validate
     * @param phase the phase to check with
     * @return if the turn is valid
     */
    public boolean validate(Turn turn, Phase phase) {
        LinkedList<Card> dropped = turn.getDroppedCards();
        // create a histogram for number sets and runs
        // this histogram should be outside the loop so that cards for rules don't double-dip
        Map<Integer, Integer> histogram = new TreeMap<>();
        for (Card c: dropped) {
            histogram.merge(c.getNum(), 1, Integer::sum);
        }
        if (DEBUGGING) {
            System.out.println(histogram);
        }
        // check if dropped cards don't contain only wilds
        for (int i: histogram.keySet()) {
            // since this is a balanced key set, the first number should not be a wild card
            // if the first is a wild card then the move is entirely wild cards (illegal)
            if (i == 14) { // 14 is wild
                if (DEBUGGING) {
                    System.out.println("All cards are wild.");
                }
            }
            break;
        }
        int wildCards = 0; // number of wild cards
        if (histogram.get(14) != null) {
            wildCards = histogram.get(14); // check this at the end
            histogram.put(14, 0); // ignore wild card usage in the main logic.
        }
        System.out.println(histogram.keySet());
        for (Rule rule: phase.getRules()) {
            // remove cards for each phase if they work
            if (rule instanceof NumberSet) {
                int ruleCount = rule.getCount();
                for (int num: histogram.keySet()) {
                    int count = histogram.get(num);
                    if (DEBUGGING) {
                        System.out.println(num + ": " + count);
                    }
                    // do not exceed the amount of cards available
                    if (count >= rule.getNumCards() && count - rule.getNumCards() >= 0) {
                        histogram.put(num, count - rule.getNumCards());
                        if (DEBUGGING) {
                            System.out.println(num + " count subtracted by " + rule.getNumCards());
                        }
                        if (--ruleCount == 0) break;
                    }
                    // wild card usage because it is insufficient
                    /*
                    no need to check if count is ever 0.
                    this is a histogram, and it's keys are looped through once.
                    therefore, solely wildcards will never be used.
                     */
                    else if (count + wildCards >= rule.getNumCards()) {
                        int decrement = rule.getNumCards() - count;
                        wildCards -= decrement;
                        histogram.put(num, 0); // just werks
                        if (DEBUGGING) {
                            System.out.println(num + " count subtracted by " + rule.getNumCards());
                            System.out.println("Remaining wild cards: " + wildCards);
                        }
                        if (--ruleCount == 0) break;
                    }
                }
                if (ruleCount != 0) {
                    if (DEBUGGING) {
                        System.out.println("Failed set check.");
                    }
                    return false;
                }
            }
            else if (rule instanceof NumberRun) {
                // should be a perfect number run with a count of 1
                // bruteforce but whatever
                // since the set check has already modified numbers,
                // find the first card number that isn't equal to 0. there should be a perfect run from there.

                int firstNum = 0;
                for (int i: histogram.keySet()) {
                    if (histogram.get(i) != 0) {
                        firstNum = i;
                        break;
                    }
                }
                if (firstNum == 0) {
                    throw new IllegalStateException();
                }
                for (int i = 0; i < rule.getNumCards(); i++) {
                    if (histogram.get(firstNum + i) == null) {
                        if (wildCards > 0) {
                            wildCards--;
                            if (DEBUGGING) {
                                System.out.println("Used one wild card.");
                            }
                        }
                        else {
                            if (DEBUGGING) {
                                System.out.println("Failed at num = " + (firstNum + i));
                            }
                            return false;
                        }
                    }
                    else {
                        if (histogram.get(firstNum + i) == 0) {
                            // cannot subtract further
                            if (DEBUGGING) {
                                System.out.println("Failed at num = " + (firstNum + i));
                            }
                            return false;
                        }
                        // subtract normally
                        histogram.put(firstNum + i, histogram.get(firstNum + i) - 1);
                        if (DEBUGGING) {
                            System.out.println("Subtracted number " + (firstNum + i));
                        }
                    }
                }
            }
            else { // color set
                int numCards = rule.getNumCards();
                if (numCards != dropped.size()) {
                    if (DEBUGGING) {
                        System.out.println("Failed size check.");
                    }
                    return false;
                }
                // all cards should be the same color

                Card normal = null;
                for (Card card: dropped) { // find first normal card to compare
                    if (!card.toString().equals("WILD")) {
                        normal = card;
                        break;
                    }
                }
                if (normal == null) {
                    throw new IllegalStateException(); // double check
                }
                for (Card c: dropped) {
                    if (c.toString().equals("WILD")) {
                        wildCards--;
                    }
                    else if (!(c.getColor().equals(normal.getColor()))){
                        return false;
                    }
                }
                // a color set is the only rule in the rule list
                // therefore it should be able to just set all numbers to 0 at this point
                histogram.replaceAll((k, v) -> 0);
            }
        }
        for (int i: histogram.keySet()) {
            if (histogram.get(i) != 0) {
                // guaranteed leftover cards, which should not exist
                return false;
            }
        }
        return wildCards == 0; // make sure all wild cards are used too, earlier they were set to 0
    }

    /**
     * Validates cards that are hit on to the middle piles.
     * @param turn the turn to validate
     * @param middlePileManager the middle piles to check with
     * @return if the hit cards are valid
     */
    public boolean validate(Turn turn, MiddlePileManager middlePileManager) {
        LinkedList<Card> hit = turn.getHitCards();
        for (Card c: hit) {
            boolean found = false;
            for (MiddlePile middlePile: middlePileManager.getMiddlePiles()) {
                if (middlePile.addCard(c, false)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (DEBUGGING) {
                    System.out.println("Failed to find a pile for " + c.toString());
                }
                return false;
            }
        }
        return true;
    }
}
