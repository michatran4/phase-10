import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This class manages the players queue, their phases, and their scores.
 */
public class PlayerManager {
    private final Queue<String> playerList;
    private final Map<String, Integer> phaseMap;
    private final Map<String, Integer> scoreboard;

    public PlayerManager() {
        playerList = new LinkedList<>();
        phaseMap = new HashMap<>();
        scoreboard = new HashMap<>();
    }

    /**
     * Adds a player to the queue.
     *
     * @param name the player name
     */
    public void add(String name) {
        playerList.add(name);
        phaseMap.put(name, 1);
        scoreboard.put(name, 0);
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
     *
     * @param name the player to check
     * @return the phase number
     */
    public int getPhase(String name) {
        return phaseMap.get(name);
    }

    /**
     * @return if any players have reached phase 10, and the player with the lowest score
     */
    public String getWinner() {
        String winner = "";
        int lowestScore = Integer.MAX_VALUE;
        for (String player: phaseMap.keySet()) {
            if (phaseMap.get(player) == 10 && scoreboard.get(player) < lowestScore) {
                lowestScore = scoreboard.get(player);
                winner = player;
            }
        }
        return winner;
    }

    /**
     * Adds to a player's score.
     * @param player the player to add to
     * @param score the score to add
     */
    public void addScore(String player, int score) {
        scoreboard.put(player, scoreboard.get(player) + score);
    }

    public String getScoreboard() {
        StringBuilder output = new StringBuilder();
        for (String player: scoreboard.keySet()) {
            output.append(player)
                    .append(" - ")
                    .append(scoreboard.get(player)).append("\n");
        }
        return output.toString();
    }

    public String getPhases() {
        StringBuilder output = new StringBuilder();
        for (String player: scoreboard.keySet()) {
            output.append(player)
                    .append(" - ")
                    .append(phaseMap.get(player)).append("\n");
        }
        return output.toString();
    }

    /**
     * Advances a player to the next phase.
     *
     * @param name the player name
     */
    public void incrementPhase(String name) {
        if (phaseMap.get(name) >= 10) { // 10 is the winning phase
            throw new IllegalStateException();
        }
        phaseMap.put(name, phaseMap.get(name) + 1);
    }

    /**
     * Gets players when no rounds are in progress, for score checking and the
     * queue is unnecessary.
     *
     * @return the players, as an array
     */
    public String[] getPlayers() {
        return playerList.toArray(new String[0]);
    }

    public boolean hasPlayer(String player) {
        return playerList.contains(player);
    }

    public String toString() {
        return "PlayerManager{" +
                "phaseMap=" + phaseMap +
                '}';
    }
}
