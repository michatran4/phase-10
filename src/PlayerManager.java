import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PlayerManager {
    private final Queue<String> playerList;
    private final Map<String, Integer> phaseMap;
    public PlayerManager() {
        playerList = new LinkedList<>();
        phaseMap = new HashMap<>();
    }

    /**
     * Adds a player to the queue.
     * @param name the player name
     */
    public void add(String name) {
        playerList.add(name);
        phaseMap.put(name, 1);
    }

    /**
     * @return the number of players
     */
    public int getNumPlayers() {
        return playerList.size();
    }

    /**
     * @return the next player in the queue to go
     */
    public String getNextPlayer() {
        String player = playerList.poll();
        playerList.add(player);
        return player;
    }

    /**
     * @return the next player, for knowing who to skip
     */
    public String peek() {
        return playerList.peek();
    }

    /**
     * Returns the provided player's current phase.
     * @param name the player to check
     * @return the phase number
     */
    public int getPhase(String name) {
        return phaseMap.get(name);
    }

    /**
     * @return if any players have reached phase 10.
     */
    public boolean checkPhases() {
        for (String player: phaseMap.keySet()) {
            if (phaseMap.get(player) == 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * Advances a player to the next phase.
     * @param name the player name
     */
    public void incrementPhase(String name) {
        if (phaseMap.get(name) >= 10) { // 10 is the winning phase
            throw new IllegalStateException();
        }
        phaseMap.put(name, phaseMap.get(name) + 1);
    }

    /**
     * Gets players when no rounds are in progress, for score checking and the queue is unnecessary.
     * @return the players, as an array
     */
    public String[] getPlayers() {
        return playerList.toArray(new String[0]);
    }
}
