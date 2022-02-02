package cards;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * This represents a player deck, which has sorted cards in a hand for easy
 * identification of sets and runs.
 */
public class PlayerDeck {
    private final Map<Card, Integer> deck;
    private int size;

    public PlayerDeck() {
        deck = new TreeMap<>();
        size = 0;
    }

    public void addCard(Card c) {
        deck.merge(c, 1, Integer::sum);
        size++;
    }

    /**
     * Remove card, for the GUI functionality.
     * @param card the card, deciphered from the clicked button's logic
     * @return the removed card
     */
    public Card removeCard(Card card) {
        if (!deck.containsKey(card)) throw new IllegalArgumentException();
        int count = deck.get(card);
        if (count - 1 == 0) {
            deck.remove(card);
        }
        else {
            deck.put(card, count - 1);
        }
        size--;
        return card;
    }

    /**
     * Prunes all cards with a value of 0.
     * Tested in TestPrune.java
     */
    private void prune() {
        while (deck.values().remove(0)) ;
    }

    /**
     * Remove cards with a specific number after being calculated with the
     * histogram in the CPUDeck.
     *
     * This is for number sets, and only removes the number cards.
     *
     * @param number the number to match
     * @param count  how many number sets
     * @return the removed cards
     */
    public LinkedList<Card> removeCardsWithNum(int number, int count) {
        LinkedList<Card> removed = new LinkedList<>();
        int countRemoved = 0;
        for (Card card: deck.keySet()) {
            if (card.getNum() == number) {
                while (deck.get(card) != 0) { // exhaust card supply
                    deck.put(card, deck.get(card) - 1);
                    removed.add(card);
                    countRemoved++;
                    if (countRemoved == count) {
                        break;
                    }
                }
                if (countRemoved == count) {
                    break;
                }
            }
        }
        if (countRemoved != count) {
            throw new IllegalStateException("Histogram calculation error.");
        }
        if (removed.size() != count) {
            throw new IllegalStateException();
        }
        prune();
        size -= count;
        return removed;
    }

    public LinkedList<Card> removeCardsWithNum(int number) {
        LinkedList<Card> removed = new LinkedList<>();
        for (Card card: deck.keySet()) {
            if (card.getNum() == number) {
                while (deck.get(card) != 0) { // exhaust card supply
                    deck.put(card, deck.get(card) - 1);
                    removed.add(card);
                }
            }
        }
        prune();
        size -= removed.size();
        return removed;
    }

    // TODO remove single card so there is no creation of a linked list

    /**
     * Count cards with a specific number.
     * This is for testing the deck after wild cards are used.
     *
     * @param number the number to check
     * @return the count
     */
    public int countCardsOfNum(int number) {
        int count = 0;
        for (Card card: deck.keySet()) {
            if (deck.get(card) == 0) {
                throw new IllegalStateException("Prune error.");
            }
            if (card.getNum() == number) {
                count += deck.get(card);
            }
        }
        return count;
    }

    /**
     * Remove cards with a specific color after being calculated with the
     * color histogram in the CPUDeck.
     *
     * This is for color sets.
     *
     * @param color the color to match
     * @param count  how many color sets
     * @return the removed cards
     */
    public LinkedList<Card> removeCardsWithColor(String color, int count) {
        LinkedList<Card> removed = new LinkedList<>();
        int countRemoved = 0;
        for (Card card: deck.keySet()) {
            if (card.getColor() == null) continue;
            if (card.getColor().equals(color)) {
                while (deck.get(card) != 0) { // exhaust card supply
                    deck.put(card, deck.get(card) - 1);
                    removed.add(card);
                    countRemoved++;
                    if (countRemoved == count) {
                        break;
                    }
                }
                if (countRemoved == count) {
                    break;
                }
            }
        }
        if (countRemoved != count) {
            throw new IllegalStateException("Color histogram calculation error.");
        }
        if (removed.size() != count) {
            throw new IllegalStateException();
        }
        prune();
        size -= count;
        return removed;
    }

    public LinkedList<Card> removeCardsWithColor(String color) {
        LinkedList<Card> removed = new LinkedList<>();
        for (Card card: deck.keySet()) {
            if (card.getColor() == null) continue;
            if (card.getColor().equals(color)) {
                while (deck.get(card) != 0) { // exhaust card supply
                    deck.put(card, deck.get(card) - 1);
                    removed.add(card);
                }
            }
        }
        prune();
        size -= removed.size();
        return removed;
    }

    /**
     * Count cards with a specific color.
     * This is for testing the deck after wild cards are used.
     *
     * @param color the color to check
     * @return the count
     */
    public int countCardsOfColor(String color) {
        int count = 0;
        for (Card card: deck.keySet()) {
            if (deck.get(card) == 0) {
                throw new IllegalStateException("Prune error.");
            }
            if (card.getColor().equals(color)) {
                count += deck.get(card);
            }
        }
        return count;
    }

    /**
     * Clear the deck when a round ends. TODO double check
     */
    public void clear() {
        deck.clear();
        size = 0;
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public String toString() {
        StringBuilder output = new StringBuilder("[");
        for (Card c: deck.keySet()) {
            for (int i = 0; i < deck.get(c); i++) {
                output.append(c.toString());
                output.append(", ");
            }
        }
        output = new StringBuilder(output.substring(0, output.length() - 2));
        output.append("]");
        return output.toString();
    }

    public int getScore() {
        int sum = 0;
        for (Card c: deck.keySet()) {
            int count = deck.get(c);
            for (int i = 0; i < count; i++) {
                if (c.getNum() <= 9) {
                    sum += 5;
                }
                else if (c.getNum() <= 12) {
                    sum += 10;
                }
                else {
                    sum += 25;
                }
            }
        }
        return sum;
    }

    /**
     * For the CPUDeck to get the deck, and for when the round ends, decks to
     * be replenished. TODO
     *
     * @return the current deck
     */
    public Map<Card, Integer> getDeck() {
        return deck;
    }

    public int getSize() {
        return size;
    }
}
