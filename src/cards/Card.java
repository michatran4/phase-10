package cards;

import java.awt.*;

/**
 * This represents a card.
 * Cards can have a number and color, or they are special.
 */
public class Card implements Comparable<Card> {
    private int num;
    private String color;
    private String special;

    public Card(int n, String c) {
        num = n;
        color = c;
    }

    public Card(String s) {
        special = s;
        if (special.equals("SKIP")) {
            num = 13;
        }
        else if (special.equals("WILD")) {
            num = 14;
        }
    }

    public int getNum() {
        return num;
    }

    public Color getColor() {
        return switch (color) {
            case "RED" -> Color.RED;
            case "BLUE" -> Color.BLUE;
            case "YELLOW" -> Color.YELLOW;
            case "GREEN" -> Color.GREEN;
            default -> null;
        };
    }

    public boolean equals(Object obj) { // for treemap position
        if (!(obj instanceof Card)) {
            return false;
        }
        Card other = (Card) obj;
        return other.getNum() == getNum() && other.getColor() == getColor();
    }

    public int compareTo(Card o) {
        if (num == o.getNum()) {
            return Integer.compare(hashCode(), o.hashCode());
        }
        return Integer.compare(num, o.getNum());
    }

    public String toString() {
        if (special == null) {
            return color + " " + num;
        }
        return special;
    }
}
