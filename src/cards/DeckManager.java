package cards;

import java.util.HashMap;
import java.util.Map;

public class DeckManager {
    private final Map<String, PlayerDeck> deckMap;

    public DeckManager() {
        deckMap = new HashMap<>();
    }

    public void put(String player, PlayerDeck deck) {
        deckMap.put(player, deck);
    }

    public PlayerDeck get(String player) {
        return deckMap.get(player);
    }

    /**
     * @return if any decks are empty, to end the current round.
     */
    public boolean checkDecks() {
        for (PlayerDeck deck: deckMap.values()) {
            if (deck.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String toString() { // not in order
        StringBuilder output = new StringBuilder();
        for (String s: deckMap.keySet()) {
            output.append(s).append(": ");
            output.append(deckMap.get(s).toString());
            output.append("\n");
        }
        return output.toString();
    }

    public void clearDecks() {
        for (String player: deckMap.keySet()) {
            if (player.contains("CPU")) {
                deckMap.put(player, new CPUDeck());
            }
            else {
                deckMap.put(player, new PlayerDeck());
            }
        }
    }
}
