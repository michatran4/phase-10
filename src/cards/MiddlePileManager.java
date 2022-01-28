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
}
