package cards;

import java.util.LinkedList;

public class MiddlePileManager {
    private final LinkedList<MiddlePile> middlePiles;

    public MiddlePileManager() {
        middlePiles = new LinkedList<>();
    }

    public void addMiddlePile(MiddlePile pile) {
        middlePiles.add(pile);
    }

    public LinkedList<MiddlePile> getMiddlePiles() {
        return middlePiles;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (MiddlePile pile: middlePiles) {
            output.append(pile.getRule().toString());
            output.append("\n");
        }
        return output.toString();
    }

    public void clear() {
        middlePiles.clear();
    }
}
