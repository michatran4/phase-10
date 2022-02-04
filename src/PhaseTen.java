import cards.*;
import phases.Phase;
import phases.PhaseCollection;
import phases.Rule;
import turns.Turn;
import turns.TurnValidator;

import java.util.*;

public class PhaseTen {
    private final Set<String> skipped; // names of players that are currently skipping
    private final Set<String> hitting; // players that have completed the phase
    private final Map<String, Integer> scoreboard;
    private final boolean DEBUGGING;
    private final TurnValidator turnValidator;
    private PhaseCollection phases;
    private CardPile drawPile;
    private CardPile discardPile;
    private DeckManager deckManager;
    private PlayerManager playerManager;
    private MiddlePileManager middlePileManager;

    public PhaseTen(boolean b) {
        skipped = new HashSet<>();
        hitting = new HashSet<>();
        scoreboard = new HashMap<>();
        initPhaseCollection();
        initPiles();
        drawPile.shuffle();
        initPlayers();
        turnValidator = new TurnValidator(b);
        DEBUGGING = b;
        startGame();
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
            for (String color: new String[]{"RED", "BLUE", "YELLOW", "GREEN"}) {
                for (int i = 0; i < 2; i++) {
                    drawPile.add(new Card(num, color));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            drawPile.add(new Card("SKIP"));
        }
        for (int i = 0; i < 8; i++) {
            drawPile.add(new Card("WILD"));
        }
        discardPile = new CardPile(false);
        middlePileManager = new MiddlePileManager();
    }

    private void initPlayers() {
        deckManager = new DeckManager();
        deckManager.put("Player 1", new PlayerDeck());
        deckManager.put("CPU 1", new CPUDeck());
        deckManager.put("CPU 2", new CPUDeck());
        deckManager.put("CPU 3", new CPUDeck());

        playerManager = new PlayerManager();
        playerManager.add("Player 1");
        playerManager.add("CPU 1");
        playerManager.add("CPU 2");
        playerManager.add("CPU 3");

        dealCards();
        if (DEBUGGING) {
            System.out.println(deckManager.toString());
        }
    }

    private void dealCards() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < playerManager.getNumPlayers(); j++) {
                String player = playerManager.getNextPlayer();
                PlayerDeck playersDeck = deckManager.get(player);
                playersDeck.addCard(drawPile.pop());
            }
        }
    }

    private void startGame() {
        discardPile.add(drawPile.pop());
        if (discardPile.peek().equals("SKIP")) {
            skipped.add(playerManager.peek());
        }
        while (true) {
            play();
            updateScores();
            if (playerManager.checkPhases()) {
                System.out.println("WINNER");
                break;
            }
            if (DEBUGGING) {
                System.out.println(getScoreboard());
            }
            playerManager.getNextPlayer(); // dealer rotates clockwise
            // deal new cards
            deckManager.clearDecks();
            initPiles();
            dealCards();
        }
    }

    /**
     * Plays rounds until a deck is found to be empty.
     */
    private void play() {
        while (!deckManager.checkDecks()) { // while none are empty for the round
            for (int num = 0; num < playerManager.getNumPlayers(); num++) {
                String player = playerManager.getNextPlayer();
                if (drawPile.isEmpty()) {
                    drawPile.addAll(discardPile.getPile());
                    discardPile.clear();
                }
                if (skipped.contains(player)) {
                    skipped.remove(player);
                    continue;
                }
                if (player.contains("CPU")) {
                    Phase phase =
                            phases.getPhase(playerManager.getPhase(player));
                    CPUDeck deck = (CPUDeck) deckManager.get(player);
                    // CPU automatically draws from the draw pile
                    deck.addCard(drawPile.pop());
                    // TODO don't do this if you can implement an algorithm
                    Turn turn;
                    if (hitting.contains(player)) {
                        turn = deck.getNextTurn(middlePileManager);
                        if (!(turnValidator.validate(turn, phase))) {
                            throw new IllegalStateException();
                        }
                        addHitsAndDiscard(turn);
                    }
                    else {
                        turn = deck.getNextTurn(phase);
                        Turn hit = null;
                        if (turn.getDroppedCards().size() != 0) {
                            hitting.add(player);
                            hit = deck.getNextTurn(middlePileManager);
                        }
                        if (!(turnValidator.validate(turn, phase))) {
                            throw new IllegalStateException();
                        }

                        if (turn.getDroppedCards() != null) {
                            LinkedList<Card> dropped = turn.getDroppedCards();
                            for (Rule rule: phase.getRules()) {
                                for (int i = 0; i < rule.getCount(); i++) {
                                    // sets can be at least one
                                    LinkedList<Card> middle =
                                            new LinkedList<>();
                                    for (int j = 0; j < rule.getNumCards(); j++) {
                                        middle.add(dropped.poll());
                                        // all are in order
                                    }
                                    MiddlePile pile = new MiddlePile(middle,
                                            rule);
                                    middlePileManager.addMiddlePile(pile);
                                }
                            }
                            if (dropped.size() != 0) {
                                throw new IllegalStateException();
                            }
                        }
                        addHitsAndDiscard(Objects.requireNonNullElse(hit, turn));
                    }
                    if (turn.getDiscardCard().toString().equals("SKIP")) {
                        skipped.add(findSmallestDeck(player));
                    }
                }
                else {
                    // TODO integrate with GUI, check correct phase
                    //  unable to draw skip cards from the discard pile
                    //  validate hits too
                }
            }
        }
        hitting.clear();
    }

    /**
     * @param exclude the current active turn, should not skip this player
     * @return the two players with the smallest decks.
     */
    private String findSmallestDeck(String exclude) {
        TreeMap<Integer, String> map = new TreeMap<>();
        for (String player: playerManager.getPlayers()) {
            if (player.equals(exclude)) continue;
            map.put(deckManager.get(player).getSize(), player);
        }
        String[] sorted = map.values().toArray(new String[0]);
        return sorted[0];
    }

    private void addHitsAndDiscard(Turn turn) {
        for (Card card: turn.getHitCards()) {
            boolean found = false;
            for (MiddlePile pile:
                    middlePileManager.getMiddlePiles()) {
                if (pile.addCard(card, true)) {
                    found = true;
                    break;
                }
            }
            if (!found) throw new IllegalStateException();
        }
        discardPile.add(turn.getDiscardCard());
    }

    /**
     * Updates scores when a round ends.
     */
    private void updateScores() {
        boolean hasWinner = false; // temporary check where one should have added 0
        for (String player: playerManager.getPlayers()) {
            int score = deckManager.get(player).getScore();
            if (score == 0) {
                playerManager.incrementPhase(player);
                hasWinner = true;
            }
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
