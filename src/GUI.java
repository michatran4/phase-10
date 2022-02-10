import cards.Card;
import cards.MiddlePile;
import cards.MiddlePileManager;
import phases.Phase;
import phases.Rule;
import turns.Turn;
import turns.TurnValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;

public class GUI {
    private final Color bgColor = new Color(2, 48, 32); //Dark green background for board

    private final int cardWidth = 120; //Default card width for sizing
    private final int cardHeight = 160; //Default card height for sizing
    private final ImageIcon cardBack0 = new ImageIcon("back0.png"); //Card Back Default
    private final ImageIcon cardBack90 = new ImageIcon("back90.png"); //Card Back rotated 90
    private final ImageIcon cardBack180 = new ImageIcon("back180.png"); //Card Back rotated 180
    private final ImageIcon cardBack270 = new ImageIcon("back270.png"); //Card Back rotated 270
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //Dimensions of user's screen
    private final Dimension boardSize = new Dimension((int) ((7) * (screenSize.getWidth() / 8)), (int) screenSize.getHeight()); // Adjusted size of board
    private final Dimension menuSize = new Dimension((int) (screenSize.getWidth() / 8), screenSize.height); // Adjusted size of menu

    private JFrame frame; //the container of everything.
    private JPanel boardPanel, menuPanel; //main parent panels
    private JPanel topPanel, leftPanel, rightPanel, botPanel, playerCardPanel; //player panels
    private JPanel centerPanel, centerLeftPanel, centerRightPanel; //center panel
    private CardButton drawPile, discardCard; //cardButton is a class that extends JButton. Anything that shows as a card is a cardButton
    private JButton setButton, hitButton; //buttons in botPanel of player usage
    private final LinkedList<CardButton> playerCards;
    private final LinkedList<CardButton> selectedCards; //cards that currently selected by the user
    private final LinkedList<JPanel> completedSetPanels; // added to centerRightPanel
    private final HashMap<String, String> components;// dynamic text components of the gui
    private String pile, move;
    private final TurnValidator turnValidator;

    //Basic setup of the frame container, panels, card piles (buttons)
    public GUI(HashMap<String, String> vars) {
        components = new HashMap<>(vars);
        playerCards = new LinkedList<>();
        selectedCards = new LinkedList<>();
        centerRightPanel = new JPanel();
        completedSetPanels = new LinkedList<>();
        setupFrame(); //Set up main container frame of all panels
        setupParentPanels(); //Set up 2 parent panels to separate the physical board game and
        // side menu for score and instructions.
        refreshMenu();
        setupPlayerPanels(); //Setup up 5 panels, 3 cpu, 1 player, 1 center panel for card piles
        setupCenterPanel(); //Setup centerPanel discard and draw card piles
        updateSetsPanel();
        frame.setVisible(true);
        turnValidator = new TurnValidator(false);
    }

    // fix text for jlabels and wrapping improperly
    private String fixText(String text) {
        return "<html>" + text.replaceAll("<","&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\n", "<br/>") + "</html>";
    }

    public void updateStatus(String status) {
        components.put("status", status);
        refreshMenu();
    }

    public void updateScoreboard(String scoreboard) {
        components.put("scoreboard", scoreboard);
        refreshMenu();
    }

    public void updatePhases(String phases) {
        components.put("phases", phases);
        refreshMenu();
    }

    //Set up main container frame of all panels; utilizes borderLayout manager
    private void setupFrame() {
        frame = new JFrame("Phase 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());
    }

    //Set up parent panels; Left = Board, Right = Menu for scoreboard and rules
    private void setupParentPanels() {
        //boardPanel is where game takes place, parent panel for player panels which hold cards
        boardPanel = new JPanel();
        boardPanel.setBackground(bgColor);
        boardPanel.setLayout(new BorderLayout());
        boardPanel.setPreferredSize(boardSize);

        //menuPanel is for the scoreboard and instructions
        menuPanel = new JPanel();
        menuPanel.setBackground(Color.white);
        menuPanel.setPreferredSize(menuSize);
        menuPanel.setLayout(new GridLayout(2, 1));

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.EAST);
    }

    //Setup menu panel on the side of the screen adding instructions (JLabel Image) and scoreboard (JTextPane)
    private void refreshMenu() {
        menuPanel.removeAll();

        JPanel scoreboard = new JPanel();
        scoreboard.setLayout(new GridLayout(6, 1));

        JLabel label = new JLabel("Phase 10");
        label.setFont(new Font("Magneto", Font.BOLD, 32));
        label.setForeground(Color.BLUE);
        label.setBackground(null);
        scoreboard.add(label);

        label = new JLabel(fixText(components.get("status")));
        label.setFont(new Font("Dialog", Font.PLAIN, 18));
        label.setBackground(null);
        scoreboard.add(label);

        label = new JLabel("Scoreboard");
        label.setFont(new Font("Dialog", Font.BOLD, 20));
        scoreboard.add(label);

        label = new JLabel(fixText(components.get("scoreboard")));
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        scoreboard.add(label);

        label = new JLabel("Phase Levels");
        label.setFont(new Font("Dialog", Font.BOLD, 20));
        scoreboard.add(label);

        label = new JLabel(fixText(components.get("phases")));
        label.setFont(new Font("Dialog", Font.BOLD, 14));
        scoreboard.add(label);

        JLabel instructions = new JLabel();
        ImageIcon icon = new ImageIcon("instructions.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance((int) menuSize.getWidth(), (int) (menuSize.getHeight() / 3), Image.SCALE_DEFAULT));
        instructions.setIcon(icon);

        menuPanel.add(scoreboard);
        menuPanel.add(instructions);
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    /**
     * Add a played phase/set of cards to the GUI for the player.
     * @param cards the cards
     */
    public void setCards(LinkedList<Card> cards) {
        playerCards.clear();
        playerCardPanel.removeAll();
        for (Card card: cards) {
            CardButton cardButton = new CardButton(card);
            cardButton.setBorder(BorderFactory.createEmptyBorder());
            cardButton.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            playerCardPanel.add(cardButton);
            playerCards.add(cardButton);
            // do not add button functionality, it is toggled when necessary
        }
        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

    /**
     * Set a CPU's deck visually.
     * @param side the cpu
     * @param num how mayn cards
     */
    public void setCards(String side, int num) {
        //CPU CARDS, all should be card backs, player should not be able to see other cards
        JPanel panel;
        ImageIcon icon;
        Dimension dimension;
        int sideWidth = (int) (cardHeight * .4);
        int sideHeight = (int) (cardWidth * .4);
        switch (side) {
            case "top":
                int topWidth = (int) (cardWidth * .5);
                int topHeight = (int) (cardHeight * .5);
                icon = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(topWidth, topHeight);
                panel = topPanel;
                break;
            case "left":
                icon = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(sideWidth, sideHeight);
                panel = leftPanel;
                break;
            case "right":
                icon = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(sideWidth, sideHeight);
                panel = rightPanel;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (panel.getComponentCount() > num) {
            for (int i = panel.getComponentCount(); i != num; i--) {
                panel.remove(0);
            }
        }
        else if (panel.getComponentCount() < num) {
            for (int i = panel.getComponentCount(); i != num; i++) {
                JLabel card = new JLabel(icon);
                card.setBorder(BorderFactory.createEmptyBorder());
                card.setPreferredSize(dimension);
                panel.add(card);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    //Set up panels that will hold cards of players using GridLayout manager
    private void setupPlayerPanels() {
        int top_botPanelWidth = (int) (5 * (boardSize.getWidth() / 6));
        int top_botPanelHeight = (int) (boardSize.getHeight() / 6);
        int sidePanelsWidth = (int) (boardSize.getHeight() / 6);
        int sidePanelsHeight = (int) (5 * (boardSize.getWidth() / 6));

        // BOTTOM PANEL
        //NOTE: botPanel consists of 2 panels, top = player buttons for creating sets, bot = cards
        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight + 40)));
        botPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        botPanel.setLayout(new BorderLayout());
        setupBotPanel(top_botPanelWidth, (top_botPanelHeight + 40));

        // TOP PANEL
        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight - 40)));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 350, 0, 350));
        topPanel.setLayout(new GridLayout(1, 11, 20, 0));

        // RIGHT PANEL
        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 30, 50));
        rightPanel.setLayout(new GridLayout(11, 1, 0, 10));

        // LEFT PANEL
        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 60));
        leftPanel.setLayout(new GridLayout(11, 1, 0, 10));

        //NOTE: 2 Panels, left = centerLeftPanel, right = centerRightPanel.
        //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
        JPanel parentCenterPanel = new JPanel();
        parentCenterPanel.setLayout(new BorderLayout());

        JLabel p1Title = new JLabel("PLAYER 1", SwingConstants.CENTER);
        p1Title.setFont(new Font("Dialog", Font.ITALIC, 12));
        p1Title.setOpaque(true);
        p1Title.setBackground(bgColor);
        p1Title.setForeground(Color.WHITE);
        parentCenterPanel.add(p1Title, BorderLayout.SOUTH);

        JLabel p2Title = new JLabel("CPU 3", SwingConstants.CENTER);
        p2Title.setFont(new Font("Dialog", Font.ITALIC, 12));
        p2Title.setOpaque(true);
        p2Title.setBackground(bgColor);
        p2Title.setForeground(Color.WHITE);
        parentCenterPanel.add(p2Title, BorderLayout.EAST);

        JLabel p3Title = new JLabel("CPU 2", SwingConstants.CENTER);
        p3Title.setFont(new Font("Dialog", Font.ITALIC, 12));
        p3Title.setOpaque(true);
        p3Title.setBackground(bgColor);
        p3Title.setForeground(Color.WHITE);
        parentCenterPanel.add(p3Title, BorderLayout.NORTH);

        JLabel p4Title = new JLabel("CPU 1", SwingConstants.CENTER);
        p4Title.setFont(new Font("Dialog", Font.ITALIC, 12));
        p4Title.setOpaque(true);
        p4Title.setBackground(bgColor);
        p4Title.setForeground(Color.WHITE);
        parentCenterPanel.add(p4Title, BorderLayout.WEST);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(bgColor);
        parentCenterPanel.add(centerPanel, BorderLayout.CENTER);

        boardPanel.add(botPanel, BorderLayout.SOUTH);
        boardPanel.add(rightPanel, BorderLayout.EAST);
        boardPanel.add(topPanel, BorderLayout.NORTH);
        boardPanel.add(leftPanel, BorderLayout.WEST);
        boardPanel.add(parentCenterPanel, BorderLayout.CENTER);
    }

    //bottom panel, aka player panel, consists of 2 sub panels: playerButtonPanel = top, playerCardPanel = bot
    private void setupBotPanel(int panelWidth, int panelHeight) {
        //setup top row, playerButtonPanel
        JPanel playerButtonPanel = new JPanel();
        playerButtonPanel.setPreferredSize(new Dimension(panelWidth, (int) (panelHeight * .1)));
        playerButtonPanel.setBackground(bgColor);
        playerButtonPanel.setLayout(new GridLayout(1, 10));

        for (int i = 0; i < 3; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(bgColor);
            playerButtonPanel.add(emptyCell);
        }
        setButton = new JButton("Complete");
        playerButtonPanel.add(setButton);
        for (int i = 0; i < 2; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(bgColor);
            playerButtonPanel.add(emptyCell);
        }
        hitButton = new JButton("Hit");
        playerButtonPanel.add(hitButton);
        for (int i = 0; i < 3; i++) {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(bgColor);
            playerButtonPanel.add(emptyCell);
        }

        //Setup bot row, playerCardPanel --> cards will be added in dealPlayerCards()
        playerCardPanel = new JPanel();
        playerCardPanel.setBackground(bgColor);

        botPanel.add(playerButtonPanel, BorderLayout.NORTH);
        botPanel.add(playerCardPanel, BorderLayout.SOUTH);
    }

    // centerPanel contains JPanels; Left = discard and draw piles, Right = completed phase sets
    private void setupCenterPanel() {
        //Set up draw & discard pile panel (left)
        centerLeftPanel = new JPanel();
        centerLeftPanel.setBackground(bgColor);
        centerLeftPanel.setPreferredSize(new Dimension((int) (boardSize.getWidth() / 4), (int) boardSize.getHeight()));
        int vgap = (int) (boardSize.getHeight() / 6); //distance from top and bottom of centerPanel
        centerLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, vgap));

        drawPile = new CardButton(cardBack0);
        drawPile.setBorder(BorderFactory.createEmptyBorder());
        drawPile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        centerLeftPanel.add(drawPile);

        //Set up completed phase sets panel (centerRightPanel)
        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(bgColor);
        centerRightPanel.setBorder(BorderFactory.createLineBorder(bgColor, 20));
        centerRightPanel.setPreferredSize(new Dimension((int) ((boardSize.getWidth() / 2)), (int) boardSize.getHeight()));

        centerPanel.add(centerLeftPanel, BorderLayout.WEST);
        centerPanel.add(centerRightPanel, BorderLayout.EAST);
    }

    /**
     * Sets the discard card graphically.
     * @param card the card to set it to
     */
    public void setDiscardCard(Card card) {
        if (discardCard != null) {
            centerLeftPanel.remove(discardCard);
        }
        if (card != null) {
            discardCard = new CardButton(card);
            centerLeftPanel.add(discardCard);
        }
        centerLeftPanel.revalidate();
        centerLeftPanel.repaint();
    }

    /**
     * Sets the discard card graphically, for a placeholder.
     * @param card the string to set it to
     */
    public void setDiscardCard(String card) {
        if (discardCard != null) {
            centerLeftPanel.remove(discardCard);
        }
        if (card != null) {
            discardCard = new CardButton(card);
            centerLeftPanel.add(discardCard);
        }
        centerLeftPanel.revalidate();
        centerLeftPanel.repaint();
    }

    /**
     * Clears the sets from the GUI.
     */
    public void clearSets() {
        completedSetPanels.clear();
    }

    private void updateSetsPanel() {
        centerRightPanel.removeAll();
        centerRightPanel.setLayout(new FlowLayout());
        for (JPanel set: completedSetPanels) {
            centerRightPanel.add(set);
        }
        centerRightPanel.revalidate();
        centerRightPanel.repaint();
    }

    /**
     * Adds the player's selected cards as a played set.
     * @param phase the phase to check
     */
    private void addCompletedPlayerSet(Phase phase, MiddlePileManager middlePileManager) {
        int cWidth = (int) (cardWidth * .4);
        int cHeight = (int) (cardHeight * .4);
        LinkedList<CardButton> temp = new LinkedList<>(selectedCards);
        // need to keep selected cards, so they can be accessed by the game
        for (Rule rule: phase.getRules()) {
            for (int i = 0; i < rule.getCount(); i++) {
                JPanel set = new JPanel();
                set.setBackground(bgColor);
                set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                LinkedList<Card> current = new LinkedList<>();
                for (int j = 0; j < rule.getNumCards(); j++) {
                    CardButton card = temp.poll();
                    if (card == null) throw new IllegalStateException();
                    current.add(card.getCard());
                    //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                    card.setBorder(BorderFactory.createEmptyBorder());
                    card.unselect();
                    card.setPreferredSize(new Dimension(cWidth, cHeight));
                    set.add(card);
                }
                MiddlePile middlePile = new MiddlePile(current, rule, false);
                middlePileManager.addMiddlePile(middlePile);
                completedSetPanels.add(set);
            }
        }
        updateSetsPanel();
        setNextMove("set");
    }

    /**
     * Returns the pile that the player draws from.
     * Interactions go back and forth. Variables allow for waiting.
     */
    public String playerDraw() {
        updateStatus("Player 1 is drawing.");
        pile = "";
        drawPile.setBorder(BorderFactory.createLineBorder(Color.CYAN, 7));
        drawPile.addActionListener(e -> {
            disablePiles();
            setSelectedPile("draw");
        });
        while (getSelectedPile().equals("")) {
            try {Thread.sleep(10);}
            catch (InterruptedException ignored) {}
        }
        return pile;
    }

    /**
     * Discard piles are only enabled if skips aren't at the top.
     */
    public void enableDiscardPile() {
        discardCard.setBorder(BorderFactory.createLineBorder(Color.CYAN, 7));
        discardCard.addActionListener(e -> {
            disablePiles();
            setSelectedPile("discard");
        });
    }

    /**
     * This disables and removes highlights from the draw and discard piles.
     */
    public void disablePiles() {
        for (ActionListener al: drawPile.getActionListeners()) {
            drawPile.removeActionListener(al);
        }
        for (ActionListener al: discardCard.getActionListeners()) {
            discardCard.removeActionListener(al);
        }
        drawPile.setBorder(BorderFactory.createEmptyBorder());
        discardCard.setBorder(BorderFactory.createEmptyBorder());
    }

    private void setSelectedPile(String p) {
        pile = p;
    }

    private String getSelectedPile() {
        return pile;
    }

    public void setNextMove(String m) {
        move = m;
    }

    public String getNextMove() {
        return move;
    }

    // new turn, after card is drawn
    public void playerTurn(Phase phase, MiddlePileManager middlePileManager, boolean hittable) {
        move = "";
        toggleCardSelection(); //make cards selectable
        toggleDiscardButton();
        if (hittable) {
            toggleHitButton(middlePileManager);
        }
        else {
            toggleSetButton(phase, middlePileManager);
        }
    }

    public void warn(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    // If player still needs to complete sets --> allow player to attempt to create phase set
    private void toggleSetButton(Phase phase, MiddlePileManager middlePileManager) {
        setButton.addActionListener(e -> {
            LinkedList<Card> middle = getSelectedCards();
            if (middle.size() == 0) {
                warn("You have not selected any cards.");
                return;
            }
            for (Card card: middle) {
                if (card.toString().equals("SKIP")) {
                    warn("You cannot play a skip.");
                    return;
                }
            }
            Turn t = new Turn(middle, null, new LinkedList<>());
            if (turnValidator.validate(t, phase)) {
                addCompletedPlayerSet(phase, middlePileManager);
            }
            else {
                warn("Invalid phase play.");
            }
        });
    }

    /**
     * @return a single selected card
     */
    public Card getSelectedCard() {
        if (selectedCards.size() == 0) throw new IllegalStateException();
        return selectedCards.poll().getCard();
    }

    /**
     * @return all selected cards
     */
    public LinkedList<Card> getSelectedCards() {
        LinkedList<Card> selected = new LinkedList<>();
        for (CardButton cardButton: selectedCards) {
            selected.add(cardButton.getCard());
        }
        return selected;
    }

    public void clearSelectedCards() {
        selectedCards.clear();
    }

    //If player has one card selected & presses discard pile --> discard that card
    private void toggleDiscardButton() {
        discardCard.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
        discardCard.addActionListener(e -> {
            if (selectedCards.size() == 1) {
                for (CardButton card: selectedCards) {
                    card.setBorder(BorderFactory.createEmptyBorder());
                    card.unselect();
                    card.setPreferredSize(new Dimension(cardWidth, cardHeight));
                    centerLeftPanel.remove(discardCard);
                    discardCard = card;
                    centerLeftPanel.add(discardCard);
                    centerLeftPanel.revalidate();
                    centerLeftPanel.repaint();
                }
                disablePlayerFunctions();
                setNextMove("discard");
            }
            else {
                warn("You must discard with one card selected.");
            }
        });
    }

    /**
     * Toggle from the main game after the player completes a phase.
     */
    public void toggleHitButton(MiddlePileManager middlePileManager) {
        hitButton.addActionListener(e -> {
            if (playerCards.size() - selectedCards.size() < 1) {
                warn("You must have one remaining card to discard.");
                return;
            }
            if (selectedCards.size() == 0) {
                warn("You must choose cards to hit.");
                return;
            }

            // search first before an actual add
            for (CardButton card: selectedCards) {
                if (card.getCard().toString().equals("SKIP")) {
                    warn("You cannot add a skip.");
                    return;
                }
                boolean found = false;
                for (MiddlePile pile: middlePileManager.getMiddlePiles()) {
                    if (pile.addCard(card.getCard(), false)) {
                        found = true;
                    }
                }
                if (!found) {
                    warn("Invalid hit card selected.");
                    return;
                }
            }

            for (CardButton cardButton: selectedCards) {
                int cWidth = (int) (cardWidth * .4);
                int cHeight = (int) (cardHeight * .4);
                cardButton.setBorder(BorderFactory.createEmptyBorder());
                cardButton.unselect();
                cardButton.setPreferredSize(new Dimension(cWidth, cHeight));
                for (MiddlePile pile: middlePileManager.getMiddlePiles()) {
                    if (pile.addCard(cardButton.getCard(), true)) {
                        break;
                    }
                }
            }
            setNextMove("hit");
        });
    }

    /**
     * Allow for the player to choose cards.
     */
    public void toggleCardSelection() {
        for (CardButton card: playerCards) {
            // Selecting a card will highlight it & add it to selectedCards
            card.addActionListener(e -> {
                if (!(card.isSelected())) {
                    card.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
                    card.select();
                    selectedCards.add(card);
                }
                else {
                    card.setBorder(BorderFactory.createEmptyBorder());
                    card.unselect();
                    selectedCards.remove(card);
                }
            });
        }
    }

    /**
     * Disable player functions. They are enabled when appropriate.
     */
    private void disablePlayerFunctions() {
        for (Component c: playerCardPanel.getComponents()) {
            CardButton card = (CardButton) c;
            for (ActionListener al: card.getActionListeners())
                card.removeActionListener(al);
        }
        for (ActionListener al: setButton.getActionListeners()) // set button
            setButton.removeActionListener(al);
        for (ActionListener al: discardCard.getActionListeners()) // discard pile
            discardCard.removeActionListener(al);
        for (ActionListener al: hitButton.getActionListeners()) // hit button
            hitButton.removeActionListener(al);
        for (JPanel panel: completedSetPanels) { // no hitting
            for (MouseListener ml: panel.getMouseListeners())
                panel.removeMouseListener(ml);
        }
    }

    /**
     * Adds a set of cards to the center, creating a middle pile.
     */
    public void updateMiddlePiles(MiddlePileManager mpm) {
	// bug if not including the parameter???
        completedSetPanels.clear();
        for (MiddlePile pile: mpm.getMiddlePiles()) {
            JPanel set = new JPanel();
            set.setBackground(bgColor);
            set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            for (Card card: pile.getCards()) {
                CardButton cardButton = new CardButton(card);
                JLabel label = new JLabel(cardButton.getIcon());
                label.setBorder(BorderFactory.createEmptyBorder());
                label.setPreferredSize(new Dimension((int) (cardWidth * 0.4), (int) (cardHeight *
                        0.4)));
                set.add(label);
            }
            completedSetPanels.add(set);
        }
        updateSetsPanel();
    }
    /**
     * Adds a set of cards to the center, creating a middle pile (graphically, only).
     * @param cards the list of cards
     */
    public void addSet(LinkedList<Card> cards) {
        JPanel set = new JPanel();
        set.setBackground(bgColor);
        set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        for (Card card: cards) {
            CardButton cardButton = new CardButton(card);
            JLabel label = new JLabel(cardButton.getIcon());
            label.setBorder(BorderFactory.createEmptyBorder());
            label.setPreferredSize(new Dimension((int) (cardWidth * 0.4), (int) (cardHeight *
                    0.4)));
            set.add(label);
        }
        completedSetPanels.add(set);
        updateSetsPanel();
    }
}
