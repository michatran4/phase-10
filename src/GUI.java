import cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
    private final ArrayList<CardButton> selectedCards; //cards that currently selected by the user
    private final ArrayList<JPanel> completedSetPanels; //panels that are added into the completed phase set area (centerRightPanel)
    private final HashMap<String, String> components;// text components of the gui that can be changed
    private String pile;

    //Basic setup of the frame container, panels, card piles (buttons)
    public GUI(HashMap<String, String> vars) {
        components = new HashMap<>(vars);
        selectedCards = new ArrayList<>();
        centerRightPanel = new JPanel();
        completedSetPanels = new ArrayList<>();
        setupFrame(); //Set up main container frame of all panels
        setupParentPanels(); //Set up 2 parent panels to separate the physical board game and
        // side menu for score and instructions.
        refreshMenu();
        setupPlayerPanels(); //Setup up 5 panels, 3 cpu, 1 player, 1 center panel for card piles
        setupCenterPanel(); //Setup centerPanel discard and draw card piles
        updateSetsPanel();
        frame.setVisible(true);
    }

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

        label = new JLabel("Completed phase sets");
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

    public void setCards(LinkedList<Card> cards) {
        playerCardPanel.removeAll();
        for (Card card: cards) {
            CardButton cardButton = new CardButton(card);
            cardButton.setBorder(BorderFactory.createEmptyBorder());
            cardButton.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            playerCardPanel.add(cardButton); // TODO add back player cards
        }
        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

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

        //NOTE: botPanel consists of 2 panels, top = player buttons for creating sets, bot = cards
        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight + 40)));
        botPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        botPanel.setLayout(new BorderLayout());
        setupBotPanel(top_botPanelWidth, (top_botPanelHeight + 40));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight - 40)));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 350, 0, 350));
        topPanel.setLayout(new GridLayout(1, 11, 20, 0));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 30, 50));
        rightPanel.setLayout(new GridLayout(11, 1, 0, 10));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 60));
        leftPanel.setLayout(new GridLayout(11, 1, 0, 10));

        //NOTE: 2 Panels, left = centerLeftPanel, right = centerRightPanel.
        //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(bgColor);

        boardPanel.add(botPanel, BorderLayout.SOUTH);
        boardPanel.add(rightPanel, BorderLayout.EAST);
        boardPanel.add(topPanel, BorderLayout.NORTH);
        boardPanel.add(leftPanel, BorderLayout.WEST);
        boardPanel.add(centerPanel, BorderLayout.CENTER);
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

    public void setDiscardCard(Card card) {
        if (discardCard != null) {
            centerLeftPanel.remove(discardCard);
        }
        if (card != null) {
            discardCard = new CardButton(card);
            centerLeftPanel.add(discardCard);
        }
    }

    /**
     * Clears the sets from the GUI.
     */
    public void clearSets() { // TODO
        completedSetPanels.clear();
    }

    private void updateSetsPanel() {
        centerRightPanel.removeAll();
        centerRightPanel.setLayout(new FlowLayout());
        for (JPanel set: completedSetPanels) {
            centerRightPanel.add(set);
        }
        centerRightPanel.revalidate();
    }

    // Refresh/reset playerCardPanel to show correct cards
    private void updatePlayerCardPanel() {
        playerCardPanel.removeAll();
        /* TODO
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            playerCardPanel.add(card);
         */
        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

    // on click
    private void addCompletedPlayerSet() {
        int cWidth = (int) (cardWidth * .4);
        int cHeight = (int) (cardHeight * .4);
        JPanel set = new JPanel();
        set.setBackground(bgColor);
        set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        for (CardButton card: selectedCards) {
            //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
            card.setBorder(BorderFactory.createEmptyBorder());
            card.unselect();
            card.setPreferredSize(new Dimension(cWidth, cHeight));
            set.add(card);
        }
        selectedCards.clear();
        completedSetPanels.add(set); // TODO

        updatePlayerCardPanel();
        updateSetsPanel();
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
            CardButton card = new CardButton("drawn card"); //should be set to top card of draw pile
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            updatePlayerCardPanel();

            disablePiles();
            setSelectedPile("draw");
        });
        while (getSelectedPile().equals("")) {
            try {Thread.sleep(1);}
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
            CardButton card = new CardButton("drawn card"); //really set it to whatever card is on the discard pile at the moment
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            updatePlayerCardPanel();

            disablePiles();
            setSelectedPile("discard");
        });
    }

    public void disablePiles() {
        // disable and remove highlights
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

    // new turn after card is drawn
    public void playerTurn() {
        toggleCardSelection(); //make cards selectable
        //Enables player buttons as options for player
        /*
        if (numSetsCompleted < numSetsNeeded)
            playerCreateASet();
        else if (playerCards.size() > 1) //if player only has one card left must discard.
            playerHit();
        playerDiscard();

         */
    }

    // If player still needs to complete sets --> allow player to attempt to create phase set
    private void toggleSetButton() {
        setButton.addActionListener(e -> {
            // TODO currently selected is a list and use turn validator
            addCompletedPlayerSet();
            disablePlayerFunctions();
            playerTurn();
        });
    }

    //If player has one card selected & presses discard pile --> discard that card
    private void playerDiscard() {
        System.out.println("Player can discard");
        discardCard.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
        discardCard.addActionListener(e -> {
            if (selectedCards.size() == 1) {
                System.out.println("\nDiscarded");

                for (CardButton card: selectedCards) {
                    updatePlayerCardPanel();

                    card.setBorder(BorderFactory.createEmptyBorder());
                    card.unselect();
                    card.setPreferredSize(new Dimension(cardWidth, cardHeight));

                    centerLeftPanel.remove(discardCard);
                    discardCard = card;
                    centerLeftPanel.add(discardCard);
                    centerLeftPanel.revalidate();
                }
                selectedCards.clear();
                playerCardPanel.repaint();

                disablePlayerFunctions();
            }
            else {
                // TODO warn
            }
        });
    }

    private void playerHit() {
        System.out.println("\nPlayer can hit");
        hitButton.addActionListener(e -> {
            if (selectedCards.size() == 1) {
                System.out.println("\nHitting card");
                toggleSetSelection();
            }
        });
    }

    //Helper Method to make player cards selectable
    private void toggleCardSelection() {
        /*TODO for (cardButton card: playerCards) {
            // Selecting a card will highlight it & add it to selectedCards (ArrayList)
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(card.isSelected())) {
                        System.out.println("a card has been selected");
                        card.setBorder(BorderFactory.createLineBorder(selectionColors[colorIndex], 5));
                        card.select();
                        numSelected++;
                        colorIndex++;
                        if (colorIndex == selectionColors.length)
                            colorIndex = 0;
                        selectedCards.add(card);
                    } else if (card.isSelected()) {
                        card.setBorder(BorderFactory.createEmptyBorder());
                        card.unselect();
                        numSelected--;
                        selectedCards.remove(card);
                    }
                }
            });
        }
         */
    }

    private void toggleSetSelection() {
        for (JPanel panel: completedSetPanels) {
            if (panel.getComponentCount() > 0) {
                panel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("a panel has been selected");
                        int cWidth = (int) (cardWidth * .4);
                        int cHeight = (int) (cardHeight * .4);

                        for (CardButton card: selectedCards) {
                            updatePlayerCardPanel();

                            //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                            card.setBorder(BorderFactory.createEmptyBorder());
                            card.unselect();
                            card.setPreferredSize(new Dimension(cWidth, cHeight));
                            panel.add(card);
                        }
                        selectedCards.clear();

                        updateSetsPanel();
                        disablePlayerFunctions();
                        playerTurn();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
            }
        }
    }

    private void disablePlayerFunctions() {
        for (Component c: playerCardPanel.getComponents()) {
            CardButton card = (CardButton) c;
            for (ActionListener al: card.getActionListeners()) {
                card.removeActionListener(al);
            }
        }
        for (ActionListener al: setButton.getActionListeners()) { // set button
            setButton.removeActionListener(al);
        }
        for (ActionListener al: discardCard.getActionListeners()) { // discard pile
            discardCard.removeActionListener(al);
        }
        for (ActionListener al: hitButton.getActionListeners()) { // hit button
            hitButton.removeActionListener(al);
        }
        for (JPanel panel: completedSetPanels) { // no hitting
            for (MouseListener ml: panel.getMouseListeners()) {
                panel.removeMouseListener(ml);
            }
        }
    }

    /**
     * Adds a set of cards to the center, creating a middle pile.
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
