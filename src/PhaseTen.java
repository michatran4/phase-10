import java.util.*;

public class PhaseTen {
    private Queue<String> playerList;
    private Set<String> skipped;
    private Map<String, Integer> scoreboard;

    public PhaseTen() {
        playerList = new LinkedList<>();
        skipped = new HashSet<>();
    }

    public int getScore(String player) {
        return scoreboard.get(player);
    }

    public String getScoreboard() {
        String output = "";
        for (String player: scoreboard.keySet()) {
            output += player + " - " + scoreboard.get(player) + "\n";
        }
        return output;
    }
}
