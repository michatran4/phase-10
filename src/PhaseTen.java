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
    private final boolean DEBUGGING;
    private final TurnValidator turnValidator;
    private PhaseCollection phases;
    private CardPile drawPile;
    private CardPile discardPile;
    private DeckManager deckManager;
    private PlayerManager playerManager;
    private MiddlePileManager middlePileManager;
    private GUI gui;

    public PhaseTen(boolean b) {
        gui = new GUI();
        DEBUGGING = b;
        skipped = new HashSet<>();
        hitting = new HashSet<>();
        initPhaseCollection();
        initPiles();
        drawPile.shuffle();
        initPlayers();
        turnValidator = new TurnValidator(DEBUGGING);
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
        deckManager = new DeckManager(DEBUGGING);
        deckManager.put("Player 1", new PlayerDeck(false)); // playerCardPanel
        deckManager.put("CPU 1", new CPUDeck(false)); // leftPanel
        deckManager.put("CPU 2", new CPUDeck(false)); // topPanel
        deckManager.put("CPU 3", new CPUDeck(false)); // rightPanel

        playerManager = new PlayerManager();
        playerManager.add("Player 1");
        playerManager.add("CPU 1");
        playerManager.add("CPU 2");
        playerManager.add("CPU 3");

        dealCards();
    }

    private void dealCards() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < playerManager.getNumPlayers(); j++) {
                String player = playerManager.getNextPlayer();
                if (DEBUGGING) {
                    System.out.println("Dealing cards to " + player);
                }
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
            play(); // play until an empty deck
            updateScores(); // update scores
            // TODO GUI
            System.out.println("Round ended. New scores: ");
            System.out.println(playerManager.getScoreboard());
            if (!playerManager.getWinner().equals("")) {
                System.out.println("WINNER: " + playerManager.getWinner());
                break;
            }
            middlePileManager.clear();
            playerManager.getNextPlayer(); // dealer rotates clockwise
            // deal new cards
            deckManager.clearDecks();
            initPiles();
            drawPile.shuffle();
            dealCards();
        }
    }

    /**
     * Plays rounds until a deck is found to be empty.
     */
    private void play() {
        while (!deckManager.checkDecks()) { // while none are empty for the round
            if (DEBUGGING) {
                System.out.println();
                System.out.println("New loop, current decks:");
                System.out.println(deckManager.toString());
                System.out.println("Current phases: " + playerManager.toString());
            }
            for (int num = 0; num < playerManager.getNumPlayers(); num++) {
                String player = playerManager.getNextPlayer();
                if (DEBUGGING) {
                    System.out.println("Current middle piles: ");
                    System.out.println(middlePileManager.toString());
                    System.out.println("New player: " + player);
                }
                if (drawPile.isEmpty()) { // checking if piles are bad
                    drawPile.addAll(discardPile.getPile());
                    discardPile.clear();
                }
                if (skipped.contains(player)) { // skip the player
                    skipped.remove(player);
                    continue;
                }
                if (player.contains("CPU")) {
                    Phase phase = phases.getPhase(playerManager.getPhase(player));
                    CPUDeck deck = (CPUDeck) deckManager.get(player);
                    // CPU automatically draws from the draw pile
                    Card added = drawPile.pop();
                    deck.addCard(added);
                    if (DEBUGGING) {
                        System.out.println("Card added: " + added.toString());
                        System.out.println(deck);
                        System.out.println(deck.getDeck().toString());
                    }
                    // TODO don't do this if you can implement an algorithm
                    Turn turn;
                    if (hitting.contains(player)) { // can hit, use middle piles to make a turn
                        turn = deck.getNextTurn(middlePileManager);
                        if (DEBUGGING) {
                            System.out.println(turn.toString());
                            System.out.println("Current deck (0): " + deck);
                            System.out.println();
                        }
                        if (!(turnValidator.validate(turn, middlePileManager))) {
                            throw new IllegalStateException();
                        }
                        addHitsAndDiscard(turn, player);
                    }
                    else { // can't hit, do a normal turn
                        if (DEBUGGING) {
                            System.out.println("Current phase: " + phase.toString());
                        }
                        turn = deck.getNextTurn(phase);
                        if (DEBUGGING) {
                            System.out.println("New turn: " + turn.toString());
                            System.out.println("Current deck (1): " + deck);
                            System.out.println("Validating a turn.");
                        }
                        if (!(turnValidator.validate(turn, phase))) {
                            throw new IllegalStateException();
                        }

                        Turn hit = null;
                        if (turn.getDroppedCards().size() != 0) { // valid phase play, create piles
                            LinkedList<Card> dropped = turn.getDroppedCards();
                            for (Rule rule: phase.getRules()) {
                                for (int i = 0; i < rule.getCount(); i++) {
                                    // sets can be at least one
                                    if (DEBUGGING) {
                                        System.out.println("Creating a new middle pile:");
                                        System.out.println(rule);
                                    }
                                    LinkedList<Card> middle = new LinkedList<>();
                                    for (int j = 0; j < rule.getNumCards(); j++) {
                                        Card inOrder = dropped.poll();
                                        if (inOrder == null)
                                            throw new IllegalStateException();
                                        middle.add(inOrder);
                                        if (DEBUGGING) {
                                            System.out.println("Adding: " + inOrder);
                                        }
                                    }
                                    // all are in order, except for number runs
                                    // add wilds to the front
                                    MiddlePile pile = new MiddlePile(middle,
                                            rule, DEBUGGING);
                                    middlePileManager.addMiddlePile(pile);
                                }
                            }
                            if (dropped.size() != 0) {
                                throw new IllegalStateException();
                            }
                            // hit on the same turn as a valid phase play
                            playerManager.incrementPhase(player);
                            hitting.add(player);
                            hit = deck.getNextTurn(middlePileManager);
                            if (DEBUGGING) {
                                System.out.println("Phase completed.");
                                System.out.println("Hitting turn: " +
                                        hit.toString());
                            }
                        }
                        addHitsAndDiscard(Objects.requireNonNullElse(hit,
                                turn), player);
                    }
                }
                else {
                    // TODO integrate with GUI, check correct phase
                    //  unable to draw skip cards from the discard pile
                    //  validate hits too
                    //  player chooses who to skip
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

    /**
     * This adds hit cards to middle piles and discards the discard card.
     * This will always be called after something is validated.
     *
     * @param turn   the turn with the disposed cards
     * @param player the current player, possibly discarding a skip card
     */
    private void addHitsAndDiscard(Turn turn, String player) {
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
        if (turn.getDiscardCard().toString().equals("SKIP")) {
            skipped.add(findSmallestDeck(player));
        }
    }

    /**
     * Updates scores when a round ends.
     */
    private void updateScores() {
        boolean hasWinner = false; // temporary check where one should have added 0
        for (String player: playerManager.getPlayers()) {
            int score = deckManager.get(player).getScore();
            if (score == 0) {
                hasWinner = true;
            }
            playerManager.addScore(player, score);
        }
        if (!hasWinner) throw new IllegalStateException();
    }

    /*
    public static void main(String[] args) {
        new PhaseTen(false);
    }
     */
}
