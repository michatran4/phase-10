package turns;

import phases.*;

import java.util.LinkedList;

/**
 * Turn validator, mostly for the player.
 */
public class TurnValidator {
    private Turn turn;
    private Phase phase;

    public TurnValidator(Turn t, Phase p) {
        turn = t;
        phase = p;
    }

    // TODO have a way to choose where hit cards are placed
}
