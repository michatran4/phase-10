import cards.*;
import phases.Phase;
import phases.PhaseCollection;
import phases.Rule;
import turns.Turn;
import turns.TurnValidator;

import javax.swing.*;
import java.util.*;

public class PhaseTen {
    private final Set<String> skipped; // names of players that are currently skipping
    private final Set<String> hitting; // players that have completed the phase
    private final boolean DEBUGGING;
    private final TurnValidator turnValidator;
    private final GUI gui;
    private PhaseCollection phases;
    private CardPile drawPile;
    private CardPile discardPile;
    private DeckManager deckManager;
    private PlayerManager playerManager;
    private MiddlePileManager middlePileManager;

    public PhaseTen(boolean b) {
        DEBUGGING = b;
        skipped = new HashSet<>();
        hitting = new HashSet<>();
        initPhaseCollection();
        initPiles();
        drawPile.shuffle();
        initPlayers();
        gui = new GUI(getVariables());
        initDecks(); // updates GUI
        turnValidator = new TurnValidator(DEBUGGING);
        startGame();
    }

    private HashMap<String, String> getVariables() {
        HashMap<String, String> vars = new HashMap<>();
        vars.put("status", "New game!");
        vars.put("scoreboard", playerManager.getScoreboard());
        vars.put("phases", playerManager.getPhases());
        return vars;
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
    }

    private void initDecks() {
        dealCards();
        updateCPUDeckGUI("CPU 1");
        updateCPUDeckGUI("CPU 2");
        updateCPUDeckGUI("CPU 3");
        updatePlayerDeckGUI();
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

    private void updateStatus(String status) {
        gui.updateStatus(status);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ignored) {}
    }

    private void startGame() {
        discardPile.add(drawPile.pop());
        gui.setDiscardCard(discardPile.peek());
        if (discardPile.peek().toString().equals("SKIP")) {
            skipped.add(playerManager.peek());
        }
        while (true) {
            play(); // play until an empty deck
            updateScores(); // update scores
            updateStatus("The round has ended!");
            updateStatus("Tallying scores...");
            gui.updateScoreboard(playerManager.getScoreboard());
            if (!playerManager.getWinner().equals("")) {
                gui.updateStatus("WINNER: " + playerManager.getWinner());
                break;
            }
            middlePileManager.clear();
            gui.updateMiddlePiles(middlePileManager);
            gui.clearSets();
            playerManager.getNextPlayer(); // dealer rotates clockwise
            // deal new cards
            deckManager.clearDecks();
            initPiles();
            drawPile.shuffle();
            initDecks(); // updates GUI
            hitting.clear();
            skipped.clear();
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
                updateStatus("It is now " + player + "'s turn.");
                if (DEBUGGING) {
                    System.out.println("Current middle piles: ");
                    System.out.println(middlePileManager.toString());
                    System.out.println("New player: " + player);
                }
                if (drawPile.isEmpty()) { // checking if piles are bad
                    updateStatus("Piles were flipped to be fixed.");
                    drawPile.addAll(discardPile.getPile());
                    discardPile.clear();
                    discardPile.add(drawPile.pop());
                    gui.setDiscardCard(discardPile.peek());
                }
                if (skipped.contains(player)) { // skip the player
                    updateStatus(player + " was skipped.");
                    skipped.remove(player);
                    continue;
                }
                Phase phase = phases.getPhase(playerManager.getPhase(player));
                if (player.contains("CPU")) {
                    CPUDeck deck = (CPUDeck) deckManager.get(player);
                    // CPU automatically draws from the draw pile
                    Card added = drawPile.pop();
                    deck.addCard(added);
                    updateCPUDeckGUI(player);
                    updateStatus(player + " drew from\nthe draw pile.");
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
                        updateStatus(String.format("%s has discarded a \n%s.", player,
                                turn.getDiscardCard().toString()));
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
                            updateStatus(player + " is laying down the phase.");
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
                                    gui.addSet(middle);
                                }
                            }
                            if (dropped.size() != 0) {
                                throw new IllegalStateException();
                            }
                            // hit on the same turn as a valid phase play
                            playerManager.incrementPhase(player);
                            hitting.add(player);
                            hit = deck.getNextTurn(middlePileManager);
                            gui.updatePhases(playerManager.getPhases());
                            if (DEBUGGING) {
                                System.out.println("Phase completed.");
                                System.out.println("Hitting turn: " +
                                        hit.toString());
                            }
                        }
                        addHitsAndDiscard(Objects.requireNonNullElse(hit,
                                turn), player);
                        if (turn.getDiscardCard() != null) {
                            updateStatus(String.format("%s has discarded a \n%s.", player,
                                    turn.getDiscardCard().toString()));
                        }
                        else if (hit != null) {
                            updateStatus(String.format("%s has discarded a \n%s.", player,
                                    hit.getDiscardCard().toString()));
                        }
                        gui.updateMiddlePiles(middlePileManager);
                    }
                    updateStatus(player + " has finished their turn.");
                }
                else {
                    if (!discardPile.peek().toString().equals("SKIP")) {
                        gui.enableDiscardPile();
                    }
                    String pile = gui.playerDraw();
                    PlayerDeck deck = deckManager.get("Player 1");
                    if (pile.equals("draw")) {
                        Card pop = drawPile.pop();
                        deck.addCard(pop);
                        gui.setCards(deckManager.get("Player 1").getCreatedDeck());
                        updateStatus("You drew a " + pop.toString() + "\n from the draw pile.");
                    }
                    else if (pile.equals("discard")) {
                        Card pop = discardPile.pop();
                        deck.addCard(pop);
                        gui.setCards(deckManager.get("Player 1").getCreatedDeck());
                        if (discardPile.isEmpty()) {
                            gui.setDiscardCard("(discard)");
                        }
                        else {
                            gui.setDiscardCard(discardPile.peek());
                        }
                        updateStatus("You drew a " + pop.toString() + "\n from the draw pile.");
                    }
                    // get the next turn after drawing
                    gui.playerTurn(phase, middlePileManager, hitting.contains(player));
                    while (!gui.getNextMove().equals("discard")) {
                        if (!gui.getNextMove().equals("")) {
                            String nextMove = gui.getNextMove();
                            if (nextMove.equals("set") || nextMove.equals("hit")) {
                                LinkedList<Card> selected = gui.getSelectedCards();
                                for (Card card: selected) {
                                    deck.removeCard(card);
                                }
                                gui.clearSelectedCards();
                                gui.setNextMove(""); // TODO?
                                gui.setCards(deck.getCreatedDeck());
                                gui.toggleCardSelection();
                                if (nextMove.equals("set")) {
                                    playerManager.incrementPhase(player);
                                    hitting.add(player);
                                    gui.updatePhases(playerManager.getPhases());
                                    gui.toggleHitButton(middlePileManager);
                                    updateStatus("Player 1 has laid down the phase.");
                                }
                                else {
                                    gui.updateMiddlePiles(middlePileManager);
                                    updateStatus("Player 1 has hit cards.");
                                }
                            }
                            else {
                                throw new IllegalStateException();
                            }
                        }
                        try {Thread.sleep(100);} catch (InterruptedException ignored) {}
                    }
                    // loop broke
                    Card discard = gui.getSelectedCard();
                    deck.removeCard(discard);
                    discardPile.add(discard);
                    gui.setCards(deck.getCreatedDeck());
                    updateStatus(String.format("%s has discarded a \n%s.", player, discard.toString()));
                    if (discard.toString().equals("SKIP")) {
                        String input = null;
                        while (input == null) {
                            input = JOptionPane.showInputDialog(null, "Enter a player name to " +
                                    "skip (case-sensitive).");
                            if (playerManager.hasPlayer(input) && !input.equals("Player 1")) {
                                if (skipped.contains(input)) {
                                    gui.warn("Player is already skipped.");
                                    input = null;
                                }
                                else {
                                    skipped.add(input);
                                }
                            }
                            else {
                                gui.warn("Invalid player.");
                                input = null;
                            }
                        }
                    }
                    updateStatus(player + " has finished their turn.");
                }
            }
        }
        hitting.clear();
    }

    private void updatePlayerDeckGUI() {
        gui.setCards(deckManager.get("Player 1").getCreatedDeck());
    }

    private void updateCPUDeckGUI(String cpu) {
        switch (cpu) {
            case "CPU 1":
                gui.setCards("left", deckManager.get("CPU 1").getSize());
                break;
            case "CPU 2":
                gui.setCards("top", deckManager.get("CPU 2").getSize());
                break;
            case "CPU 3":
                gui.setCards("right", deckManager.get("CPU 3").getSize());
                break;
        }
    }

    /**
     * @param exclude the current active turn, should not skip this player
     * @return the two players with the smallest decks.
     */
    private String findSmallestDeck(String exclude) {
        TreeMap<Integer, String> map = new TreeMap<>();
        for (String player: playerManager.getPlayers()) {
            if (player.equals(exclude) || skipped.contains(player)) continue;
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
        if (turn.getHitCards().size() > 0) {
            updateStatus(player + " is hitting cards.");
        }
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
        gui.updateMiddlePiles(middlePileManager);
        discardPile.add(turn.getDiscardCard());
        gui.setDiscardCard(discardPile.peek());
        if (turn.getDiscardCard().toString().equals("SKIP")) {
            String found = findSmallestDeck(player);
            updateStatus(String.format("%s chose to skip %s.", player, found));
            skipped.add(found);
        }
        updateCPUDeckGUI(player);
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
}
