import cards.PlayerDeck;

public class CPU {
    private String name;
    private int phase;
    private PlayerDeck deck;
    public CPU(String n, PlayerDeck playerDeck) {
        name = n;
        phase = 1;
        deck = playerDeck;
    }
    public String toString() {
        return name;
    }
}
