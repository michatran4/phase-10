import cards.Card;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
    private CardButton drawPile, discardPile; //cardButton is a class that extends JButton. Anything that shows as a card is a cardButton
    private JButton setButton, hitButton; //buttons in botPanel of player usage
    private JTextPane scoreboard; //side menu text displaying scores
    private ArrayList<CardButton> selectedCards; //cards that currently selected by the user
    private ArrayList<JPanel> completedSetPanels; //panels that are added into the completed phase set area (centerRightPanel)
    private int numSetsCompleted, rows, cols; //necessary variables for phase rounds
    private String move; //display game progression, what moves each player is doing.
    // private HashMap<String, > TODO gui components accessible by a string

    //Basic setup of the frame container, panels, card piles (buttons)
    public GUI() {
        move = "";
        setupFrame(); //Set up main container frame of all panels
        setupParentPanels(); //Set up 2 parent panels to separate the physical board game and
        // side menu for score and instructions.
        setupMenu();
        setupPlayerPanels(); //Setup up 5 panels, 3 cpu, 1 player, 1 center panel for card piles
        setupCenterPanel(); //Setup centerPanel discard and draw card piles
        newRound(); //Setup card distribution
        frame.setVisible(true);
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

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.EAST);
    }

    //Setup menu panel on the side of the screen adding instructions (JLabel Image) and scoreboard (JTextPane)
    private void setupMenu() {
        menuPanel.removeAll();

        //Setup scoreboard
        scoreboard = new JTextPane();
        //Replace 0's with actual scores from logic branch
        int p1 = 0;
        int p2 = 0;
        int p3 = 0;
        int p4 = 0;

        Font font = new Font("Dialog", Font.PLAIN, 20);
        scoreboard.setFont(font);

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        scoreboard.setCharacterAttributes(attributeSet, true);
        Document doc = scoreboard.getStyledDocument();
        try {
            StyleConstants.setFontSize(attributeSet, 32);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setUnderline(attributeSet, true);
            StyleConstants.setFontFamily(attributeSet, "Magneto");
            StyleConstants.setForeground(attributeSet, Color.BLUE);
            doc.insertString(doc.getLength(), ("Phase 10" + "\n"), attributeSet);

            //Display what is going on. TODO
            attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 20);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), ("\n" + move + "\n\n"), attributeSet);

            //SCOREBOARD TODO
            attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 16);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), ("Scoreboard\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(attributeSet, 14);
            doc.insertString(doc.getLength(), ("Player 1: " + p1 + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 2: " + p2 + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 3: " + p3 + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 4: " + p4 + "\n"), attributeSet);

            //COMPLETED PHASE SETS

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 16);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), ("\nCompleted Phase Sets\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(attributeSet, 14);
            doc.insertString(doc.getLength(), ("Player 1: " + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 2: " + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 3: " + "\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 4: " + "\n"), attributeSet);

        } catch (BadLocationException e) {
            System.err.println("Could not insert such text into scoreboard");
        }

        //Setup Instructions
        JLabel instructions = new JLabel();
        ImageIcon icon = new ImageIcon("phase10 instructions.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance((int) menuSize.getWidth(), (int) (menuSize.getHeight() / 3), Image.SCALE_DEFAULT));
        instructions.setIcon(icon);

        menuPanel.add(scoreboard);
        menuPanel.add(instructions);
    }

    public void setCards(LinkedList<Card> cards) {
        playerCardPanel.removeAll();
        for (Card card: cards) {
            CardButton cardButton = new CardButton(getIconFromName(card.toString()));
            cardButton.setBorder(BorderFactory.createEmptyBorder());
            cardButton.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            playerCardPanel.add(cardButton); // TODO add back player cards
        }

        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

    private ImageIcon getIconFromName(String value) {
        ImageIcon icon;
        if (value.contains(" ")) {
            String[] name = value.split(" ");
            String color = name[0].toLowerCase();
            String number;
            switch (Integer.parseInt(name[1])) {
                case 1 -> number = "one";
                case 2 -> number = "two";
                case 3 -> number = "three";
                case 4 -> number = "four";
                case 5 -> number = "five";
                case 6 -> number = "six";
                case 7 -> number = "seven";
                case 8 -> number = "eight";
                case 9 -> number = "nine";
                case 10 -> number = "ten";
                case 11 -> number = "eleven";
                default -> number = "twelve";
            }
            icon = new ImageIcon(color +  " " + number);
        }
        else {
            if (value.equals("WILD")) {
                icon = new ImageIcon("wild.png");
            }
            else {
                icon = new ImageIcon("skip.png");
            }
        }
        return icon;
    }

    public void setCards(String side, int num) {
        //CPU CARDS, all should be card backs, player should not be able to see other cards
        JPanel panel;
        ImageIcon icon;
        Dimension dimension;
        switch (side) {
            case "top" -> {
                int topWidth = (int) (cardWidth * .5);
                int topHeight = (int) (cardHeight * .5);
                icon = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(topWidth, topHeight);
                panel = topPanel;
            }
            case "left" -> {
                int sideWidth = (int) (cardHeight * .3);
                int sideHeight = (int) (cardWidth * .3);
                icon = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(sideWidth, sideHeight);
                panel = leftPanel;
            }
            case "right" -> {
                int sideWidth = (int) (cardHeight * .3);
                int sideHeight = (int) (cardWidth * .3);
                icon = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight,
                        Image.SCALE_DEFAULT));
                dimension = new Dimension(sideWidth, sideHeight);
                panel = rightPanel;
            }
            default -> throw new IllegalArgumentException();
        }
        panel.removeAll();
        for (int i = 0; i < num; i++) {
            CardButton card = new CardButton(icon);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(dimension);
            panel.add(card);
        }
        panel.revalidate();
        panel.repaint();
    }

    public void removeCard(String side, int num) { // remove a specified number of cards
        JPanel panel;
        switch (side) {
            case "top" -> panel = topPanel;
            case "left" -> panel = leftPanel;
            case "right" -> panel = rightPanel;
            default -> throw new IllegalArgumentException();
        }
        for (int i = 0; i < num; i++) {
            panel.remove(0);
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

    //NOTE: centerPanel contains 2 more JPanels; Left = discard and draw piles, Right = completed phase sets
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

        // TODO: actual card displayed
        discardPile = new CardButton("DISCARD");
        discardPile.setBorder(BorderFactory.createEmptyBorder());
        discardPile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        centerLeftPanel.add(discardPile);

        //Set up completed phase sets panel (centerRightPanel)
        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(bgColor);
        centerRightPanel.setBorder(BorderFactory.createLineBorder(bgColor, 20));
        centerRightPanel.setPreferredSize(new Dimension((int) ((boardSize.getWidth() / 2)), (int) boardSize.getHeight()));

        centerPanel.add(centerLeftPanel, BorderLayout.WEST);
        centerPanel.add(centerRightPanel, BorderLayout.EAST);
    }

    //At beginning of each new round deal new cards and new draw/discard pile
    private void newRound() {
        numSetsCompleted = 0;
        selectedCards = new ArrayList<>();
        topPanel.removeAll();
        leftPanel.removeAll();
        rightPanel.removeAll();

        setupMenu(); // scoreboard
        centerLeftPanel.remove(discardPile); // update discard pile
        ImageIcon icon = new ImageIcon("yellow three.png");
        CardButton dCard = new CardButton(icon);
        dCard.setBorder(BorderFactory.createEmptyBorder());
        dCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
        discardPile = dCard;
        centerLeftPanel.add(discardPile);

        updateSetSettings(); // refresh set area
    }

    // Update necessary changes for each phase
    // Max cards necessary for a set in phase (maxSelect), number of potential completed sets (rows & cols)
    // what phase logic to call when player clicks button(s) (setButton, hitButton)
    private void updateSetSettings() {
        rows = 4;
        cols = 2;

        //Load completedSetPanels with empty panels to be then added to centerRightPanel
        completedSetPanels = new ArrayList<>();
        for (int i = 0; i < (rows * cols); i++) {
            JPanel holder = new JPanel();
            holder.setBackground(bgColor);
            completedSetPanels.add(holder);
        }
        updateCenterRightPanel();
    }

    // Refresh/reset the set area to show completedSetPanels
    private void updateCenterRightPanel() {
        centerRightPanel.removeAll();
        centerRightPanel.setLayout(new GridLayout(rows, cols));

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

    // Refresh/rest CPUCardPanels to show correct cards
    private void updateCPUCardPanels() {
        /*
            topPanel.removeAll();
            //for (cardButton card: p3Cards) {
                card.setPreferredSize(new Dimension((int) (cardWidth * .5), (int) (cardHeight * .5)));
                topPanel.add(card);
            //}
            topPanel.revalidate();
            topPanel.repaint();
         */
    }

    //When player click "Complete" (setButton), method will be called to add it into the completed set area (centerRightPanel)
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
        selectedCards = new ArrayList<>(); //clear selectedCards, because all selectedCards are now a completed set in set area
        completedSetPanels.set(numSetsCompleted, set); //update completedSetPanels to include new set
        numSetsCompleted++;

        updatePlayerCardPanel();
        updateCenterRightPanel();
    }

    // Game starts, manages player turns and determines round
    private void game()
    {
        //playerDraw();
    }

    // 1) Player must draw a card. Allowed to draw from discard and draw pile.
    private void playerDraw()
    {
        //display on menu player is drawing.
        move = "Player 1 is drawing";
        setupMenu();

        //enable player to draw from draw pile
        drawPile.setBorder(BorderFactory.createLineBorder(Color.CYAN, 7)); //highlight pile
        drawPile.addActionListener(e -> {
            System.out.println("Player has drawn a card!\n");
            // TODO add player cards
            CardButton card = new CardButton("drawn card"); //should be set to top card of draw pile
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            updatePlayerCardPanel();

            //Disable drawPile button
            for(ActionListener al : drawPile.getActionListeners()) {
                drawPile.removeActionListener(al);
            }
            //Disable discardPile button
            for(ActionListener al : discardPile.getActionListeners()) {
                discardPile.removeActionListener(al);
            }
            drawPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from drawPile
            discardPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from discardPile
            playerTurn(); //draw step over
        });
        //enable player to draw from discard pile
        discardPile.setBorder(BorderFactory.createLineBorder(Color.CYAN, 7)); //highlight pile
        discardPile.addActionListener(e -> {
            System.out.println("Player has drawn a card!\n");

            CardButton card = new CardButton("drawn card"); //really set it to whatever card is on the discard pile at the moment
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            // TODO add player cards
            updatePlayerCardPanel();

            //Disable drawPile button
            for(ActionListener al : drawPile.getActionListeners()) {
                drawPile.removeActionListener(al);
            }
            //Disable discardPile button
            for(ActionListener al : discardPile.getActionListeners()) {
                discardPile.removeActionListener(al);
            }
            drawPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from drawPile
            discardPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from discardPile
            playerTurn(); //draw step over
        });
        drawPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from drawPile
        playerTurn(); //draw step over
    }

    // 2) Player decides to either discard or add set/hit
    private void playerTurn()
    {
        //update move
        move = "Player 1's move...";
        setupMenu();
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
    private void playerCreateASet() {
        System.out.println("Player can create a set");
        setButton.addActionListener(e -> {
            // TODO currently selected is a list and use turn validator
            System.out.println("\nPlayer has completed a phase set!");
            addCompletedPlayerSet();
            disablePlayerFunctions();
            playerTurn();
        });
    }

    //If player has one card selected & presses discard pile --> discard that card
    private void playerDiscard() {
        System.out.println("Player can discard");
        discardPile.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
        discardPile.addActionListener(e -> {
            if (selectedCards.size() == 1) {
                System.out.println("\nDiscarded");

                for (CardButton card: selectedCards) {
                    updatePlayerCardPanel();

                    card.setBorder(BorderFactory.createEmptyBorder());
                    card.unselect();
                    card.setPreferredSize(new Dimension(cardWidth, cardHeight));
                    ImageIcon icon = new ImageIcon("blue eight.png");

                    centerLeftPanel.remove(discardPile);
                    discardPile = card;
                    centerLeftPanel.add(discardPile);
                    centerLeftPanel.revalidate();
                }
                selectedCards = new ArrayList<>(); //clear selectedCards, because selected card is now discard pile
                playerCardPanel.repaint();

                disablePlayerFunctions();
                System.out.println("Player turn over.\n");
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
        /*for (cardButton card: playerCards) {
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
                        selectedCards = new ArrayList<>(); //clear selectedCards, because all selectedCards are now a completed set in set area

                        updateCenterRightPanel();
                        disablePlayerFunctions();
                        playerTurn();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
            }
        }
    }

    // Disable cards from being able to be selected
    private void disablePlayerFunctions() {
        System.out.println("\nPLAYER FUNCTIONS ARE DISABLED\n");

        //playerCards are unselectable
        /*
        for (cardButton card: playerCards) {
            for (ActionListener al: card.getActionListeners()) {
                card.removeActionListener(al);
            }
        }

         */
        //disable setButton
        for (ActionListener al: setButton.getActionListeners()) {
            setButton.removeActionListener(al);
        }
        //disable discardPile
        for (ActionListener al: discardPile.getActionListeners()) {
            discardPile.removeActionListener(al);
        }
        //disable hitButton
        for (ActionListener al: hitButton.getActionListeners()) {
            hitButton.removeActionListener(al);
        }
        //panels unselectable for hitting
        for (JPanel panel: completedSetPanels) {
            for (MouseListener ml: panel.getMouseListeners()) {
                panel.removeMouseListener(ml);
            }
        }
    }

    // CPU method to draw from either the draw/discard pile
    private void CPUDraw() {
        System.out.println("A CPU has drawn a card!\n");

        int sideWidth = (int) (cardHeight * .3);
        int sideHeight = (int) (cardWidth * .3);
        ImageIcon rightCard = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        ImageIcon leftCard = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        int topWidth = (int) (cardWidth * .5);
        int topHeight = (int) (cardHeight * .5);
        ImageIcon topCard = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight, Image.SCALE_DEFAULT));

        CardButton card = new CardButton("CPU"); // TODO
        card.setBorder(BorderFactory.createEmptyBorder());
        /*
        if (turn == 2) {
            card.setIcon(rightCard);
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            updateCPUCardPanels();
        }
        if (turn == 3) {
            card.setIcon(topCard);
            card.setPreferredSize(new Dimension(topWidth, topHeight));
            updateCPUCardPanels();
        }
        if (turn == 4) {
            card.setIcon(leftCard);
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            updateCPUCardPanels();
        }

         */
        CPUTurn(); //draw step over
    }

    private void CPUTurn() {
        // !MICHAELS LOGIC! TODO
        System.out.println("Player passing...\n");
        CPUDiscard();
    }

    private void CPUDiscard() {
        System.out.println("CPU Discarding");
        CardButton card;

        /*
        if (turn == 2) {
            card = p2Cards.get(0); //MICHAELS LOGIC TODO
            p2Cards.remove(card);
            updateCPUCardPanels();
            ImageIcon icon = new ImageIcon("blue eight.png");
            card.setIcon(icon);
            card.setPreferredSize(new Dimension(cardWidth, cardHeight));
            centerLeftPanel.remove(discardPile);
            discardPile = card;
            centerLeftPanel.add(discardPile);
            centerLeftPanel.revalidate();
            rightPanel.repaint();
        } else if (turn == 3) {
            card = p3Cards.get(0); //MICHAELS LOGIC
            p3Cards.remove(card);
            updateCPUCardPanels();
            ImageIcon icon = new ImageIcon("blue eight.png");
            card.setIcon(icon);
            card.setPreferredSize(new Dimension(cardWidth, cardHeight));
            centerLeftPanel.remove(discardPile);
            discardPile = card;
            centerLeftPanel.add(discardPile);
            centerLeftPanel.revalidate();
            topPanel.repaint();
        } else if (turn == 4) {
            card = p4Cards.get(0); //MICHAELS LOGIC
            p4Cards.remove(card);
            updateCPUCardPanels();
            ImageIcon icon = new ImageIcon("blue eight.png");
            card.setIcon(icon);
            card.setPreferredSize(new Dimension(cardWidth, cardHeight));
            centerLeftPanel.remove(discardPile);
            discardPile = card;
            centerLeftPanel.add(discardPile);
            centerLeftPanel.revalidate();
            leftPanel.repaint();
        }

         */
    }

    private void addCompletedCPUSet() {
        int cWidth = (int) (cardWidth * .4);
        int cHeight = (int) (cardHeight * .4);
        JPanel set = new JPanel();
        set.setBackground(bgColor);
        set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        /* TODO
        if (turn == 2) {
            //michaels logic
            for (CardButton card: selectedCards) {
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }
        if (turn == 3) {
            //michaels logic
            for (CardButton card: selectedCards) {
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }
        if (turn == 4) {
            //michaels logic
            for (CardButton card: selectedCards) {
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }

         */
        updateCPUCardPanels();
        completedSetPanels.set(numSetsCompleted, set); //update completedSetPanels to include new set
        numSetsCompleted++;
        updateCenterRightPanel();
    }
}

