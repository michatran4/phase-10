import java.util.LinkedList;
import java.util.Queue;

public class PlayerManager {
    private final Queue<String> playerList;
    public PlayerManager() {
        playerList = new LinkedList<>();
    }

    /**
     * Adds a player to the queue.
     * @param name the player name
     */
    public void add(String name) {
        playerList.add(name);
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
}
