import java.util.*;

import cards.*;
import phases.*;

public class PhaseTen {
    private Set<String> skipped; // names of players that are currently skipping
    private Set<String> hitting; // players that have completed the phase
    private Map<String, Integer> scoreboard;
    private PhaseCollection phases;
    private CardPile drawPile;
    private CardPile discardPile;
    private DeckManager deckManager;
    private PlayerManager playerManager;
    private boolean DEBUGGING;
    //TODO make sure piles aren't empty, else flip

    public PhaseTen() {
        DEBUGGING = true;
        skipped = new HashSet<>();
        initPhaseCollection();
        initPiles();
        drawPile.shuffle();
        initPlayers();
        //startGame();
    }

    private void initPhaseCollection() {
        // phase 1
        LinkedList<Rule> one = new LinkedList<>();
        one.add(new NumberSet(2, 3));
        // phase 2
        LinkedList<Rule> two = new LinkedList<>();
        two.add(new NumberSet(1, 3));
        two.add(new NumberRun(1, 4));
        // phase 3
        LinkedList<Rule> three = new LinkedList<>();
        three.add(new NumberSet(1, 4));
        three.add(new NumberRun(1, 4));
        // phase 4
        LinkedList<Rule> four = new LinkedList<>();
        four.add(new NumberRun(1, 7));
        // phase 5
        LinkedList<Rule> five = new LinkedList<>();
        five.add(new NumberRun(1, 8));
        // phase 6
        LinkedList<Rule> six = new LinkedList<>();
        six.add(new NumberRun(1, 9));
        // phase 7
        LinkedList<Rule> seven = new LinkedList<>();
        seven.add(new NumberSet(2, 4));
        // phase 8
        LinkedList<Rule> eight = new LinkedList<>();
        eight.add(new ColorSet(7));
        // phase 9
        LinkedList<Rule> nine = new LinkedList<>();
        nine.add(new NumberSet(1, 5));
        nine.add(new NumberSet(1, 2));

        phases = new PhaseCollection();
        phases.addPhase(one);
        phases.addPhase(two);
        phases.addPhase(three);
        phases.addPhase(four);
        phases.addPhase(five);
        phases.addPhase(six);
        phases.addPhase(seven);
        phases.addPhase(eight);
        phases.addPhase(nine);

        System.out.println("The Phases Are: ");
        System.out.println(phases.toString());
    }

    private void initPiles() {
        drawPile = new CardPile(true);
        for (int num = 1; num <= 12; num++) {
            for (String color: new String[] {"RED", "BLUE", "YELLOW", "GREEN"}) {
                for (int i = 0; i < 2; i++) {
                    drawPile.addCard(new Card(num, color));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            drawPile.addCard(new Card("SKIP"));
        }
        for (int i = 0; i < 8; i++) {
            drawPile.addCard(new Card("WILD"));
        }
        discardPile = new CardPile(false);
    }

    private void initPlayers() {
        PlayerDeck playerDeck = new PlayerDeck(); // TODO instead of a player class wait for the discard pile to be added to
        CPUDeck cpuDeck1 = new CPUDeck();
        CPUDeck cpuDeck2 = new CPUDeck();
        CPUDeck cpuDeck3 = new CPUDeck();

        deckManager = new DeckManager();
        deckManager.put("Player 1", playerDeck);
        deckManager.put("CPU 1", cpuDeck1);
        deckManager.put("CPU 2", cpuDeck2);
        deckManager.put("CPU 3", cpuDeck3);

        playerManager = new PlayerManager();
        playerManager.add("Player 1");
        playerManager.add("CPU 1");
        playerManager.add("CPU 2");
        playerManager.add("CPU 3");

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < playerManager.getNumPlayers(); j++) {
                String player = playerManager.getNextPlayer();
                PlayerDeck playersDeck = deckManager.get(player);
                playersDeck.addCard(drawPile.pop());
            }
        }
        if (DEBUGGING) {
            System.out.println(deckManager.toString());
        }
        discardPile.addCard(drawPile.pop());
    }

    private void startGame() {
        while (true) {
            play();
            if (playerManager.checkPhases()) {
                System.out.println("WINNER");
                break;
            }
            updateScores();
            if (DEBUGGING) {
                System.out.println(getScoreboard());
            }
        }
    }

    /**
     * Plays rounds until a deck is found to be empty.
     */
    private void play() {
        while (!deckManager.checkDecks()) { // while none are empty for the round
            for (int i = 0; i < playerManager.getNumPlayers(); i++) {
                String player = playerManager.getNextPlayer();
                // TODO if piles are bad
                if (player.contains("CPU")) {
                    // TODO cpu artificial moves and integrate with GUI
                }
                else {
                    // TODO integrate with GUI
                    // TODO check if it's the correct phase
                }
            }
        }
        // TODO scoring and fixing piles?
    }

    /**
     * Updates scores when a round ends.
     */
    private void updateScores() {
        boolean hasWinner = false; // temporary check where one should have added 0
        for (String player: playerManager.getPlayers()) {
            int score = deckManager.get(player).getScore();
            if (score == 0) hasWinner = true;
            scoreboard.put(player, scoreboard.get(player) + score);
        }
        if (!hasWinner) throw new IllegalStateException();
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
}
