import java.util.*;

import cards.*;
import phases.*;
import turns.Turn;
import turns.TurnValidator;

public class PhaseTen {
    private final Set<String> skipped; // names of players that are currently skipping
    private final Set<String> hitting; // players that have completed the phase
    private final Map<String, Integer> scoreboard;
    private PhaseCollection phases;
    private CardPile drawPile;
    private CardPile discardPile;
    private DeckManager deckManager;
    private PlayerManager playerManager;
    private MiddlePileManager middlePileManager;
    private final boolean DEBUGGING;
    private final TurnValidator turnValidator;
    //TODO make sure piles aren't empty, else flip

    public PhaseTen(boolean b) {
        skipped = new HashSet<>();
        hitting = new HashSet<>();
        scoreboard = new HashMap<>();
        initPhaseCollection();
        initPiles();
        drawPile.shuffle();
        initPlayers();
        turnValidator = new TurnValidator(b);
        //startGame();
        DEBUGGING = b;
    }

    private void initPhaseCollection() {
        phases = new PhaseCollection();
        if (DEBUGGING) {
            System.out.println("The Phases Are: ");
            System.out.println(phases.toString());
        }
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
        middlePileManager = new MiddlePileManager();
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
                if (skipped.contains(player)) {
                    skipped.remove(player);
                    continue;
                }
                if (player.contains("CPU")) {
                    // TODO cpu artificial moves and integrate with GUI
                    CPUDeck deck = (CPUDeck) deckManager.get(player);
                    if (hitting.contains(player)) {
                        Turn turn = deck.getNextTurn(middlePileManager);
                        if (!(turnValidator.validate(turn,
                                phases.getPhase(playerManager.getPhase(player))))) {
                            throw new IllegalStateException();
                        }
                        // TODO add the dropped hit and discard cards
                    }
                    else {
                        Turn turn = deck.getNextTurn(phases.getPhase(playerManager.getPhase(player)));
                        Turn hit;
                        if (turn.getDroppedCards().size() != 0) {
                            hitting.add(player);
                            hit = deck.getNextTurn(middlePileManager);
                        }
                        if (!(turnValidator.validate(turn,
                                phases.getPhase(playerManager.getPhase(player))))) {
                            throw new IllegalStateException();
                        }

                        // TODO add the dropped hit and discard cards
                    }
                }
                else {
                    // TODO integrate with GUI,
                    //  check if it's the correct phase
                }
            }
        }
        // TODO scoring and fixing piles
        // TODO reset sets
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
