import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GUI {
    private final Color bgColor = new Color(2, 48, 32); //Dark green background for board

    private final int cardWidth = 120; //Default card width for sizing
    private final int cardHeight = 160; //Default card height for sizing
    private final ImageIcon cardBack0 = new ImageIcon("back0.png"); //Card Back Default
    private final ImageIcon cardBack90 = new ImageIcon("back90.png"); //Card Back rotated 90
    private final ImageIcon cardBack180 = new ImageIcon("back180.png"); //Card Back rotated 180
    private final ImageIcon cardBack270 = new ImageIcon("back270.png"); //Card Back rotated 270
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //Dimensions of user's screen
    private final Dimension boardSize = new Dimension((int)((7)*(screenSize.getWidth()/8)), (int)screenSize.getHeight()); // Adjusted size of board
    private final Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height); // Adjusted size of menu
    private final Color[] selectionColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.PINK};

    private JFrame frame; //the container of everything.
    private JPanel boardPanel, menuPanel; //main parent panels
    private JPanel topPanel, leftPanel, rightPanel, botPanel, playerCardPanel; //player panels
    private JPanel centerPanel, centerLeftPanel, centerRightPanel; //center panel
    private cardButton drawPile, discardPile; //cardButton is a class that extends JButton. Anything that shows as a card is a cardButton
    private JButton setButton, hitButton; //buttons in botPanel of player usage
    private JTextPane scoreboard; //side menu text displaying scores
    private ArrayList<cardButton> selectedCards; //cards that currently selected by the user
    private ArrayList<cardButton> playerCards, p2Cards, p3Cards, p4Cards; //cards that currently in the players hand
    private ArrayList<JPanel> completedSetPanels; //panels that are added into the completed phase set area (centerRightPanel)
    private int[] maxSelect; //Number of cards necessary for a completed set(s) in a certain phase
    private int round, numSelected, numSetsNeeded, numSetsCompleted, rows, cols; //necessary variables for phase rounds
    private int colorIndex; // index tracker of selectionColors
    private int turn; //keep track of whose turn it is. order goes counterclockwise.

    //Basic setup of the frame container, panels, card piles (buttons)
    public GUI()
    {
        round = 0;

        //Set up main container frame of all panels
        setupFrame();

        //Set up 2 parent panels to separate the physical board game and side menu for score and instructions.
        setupParentPanels();

        //Setup menu
        setupMenu();

        //Setup up 5 panels, 3 cpu, 1 player, 1 center panel for card piles
        setupPlayerPanels();

        //Setup centerPanel discard and draw card piles
        setupCenterPanel();

        //Setup card distribution
        newRound();

        frame.setVisible(true);
    }

    //Set up main container frame of all panels; utilizes borderLayout manager
    private void setupFrame()
    {
        frame = new JFrame("Phase 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());
    }

    //Set up parent panels; Left = Board, Right = Menu for scoreboard and rules
    private void setupParentPanels()
    {
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
    private void setupMenu()
    {
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
        try
        {
            StyleConstants.setFontSize(attributeSet, 40);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setUnderline(attributeSet, true);
            StyleConstants.setFontFamily(attributeSet, "Magneto");
            StyleConstants.setForeground(attributeSet, Color.BLUE);
            doc.insertString(doc.getLength(), ("Phase : " + round + "\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 28);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), ("Scoreboard\n\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(attributeSet, 18);
            doc.insertString(doc.getLength(), ("Player 1: " + p1 + "\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 2: " + p2 + "\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 3: " + p3 + "\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 4: " + p4 + "\n\n"), attributeSet);
        }
        catch (BadLocationException e)
        {
            System.err.println("Could not insert such text into scoreboard");
        }

        //Setup Instructions
        JLabel instructions = new JLabel();
        ImageIcon icon = new ImageIcon("phase10 instructions.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance((int)menuSize.getWidth(), (int)(menuSize.getHeight()/3), Image.SCALE_DEFAULT));
        instructions.setIcon(icon);

        menuPanel.add(scoreboard);
        menuPanel.add(instructions);
    }

    //Set up panels that will hold cards of players using GridLayout manager
    private void setupPlayerPanels()
    {
        int top_botPanelWidth = (int)(5*(boardSize.getWidth()/6));
        int top_botPanelHeight = (int)(boardSize.getHeight()/6);
        int sidePanelsWidth = (int)(boardSize.getHeight()/6);
        int sidePanelsHeight = (int)(5*(boardSize.getWidth()/6));

        //NOTE: botPanel consists of 2 panels, top = player buttons for creating sets, bot = cards
        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight+40)));
        botPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        botPanel.setLayout(new BorderLayout());
        setupBotPanel(top_botPanelWidth, (top_botPanelHeight+40));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight-40)));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 350, 0, 350));
        topPanel.setLayout(new GridLayout(1,11, 20, 0));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 30, 50));
        rightPanel.setLayout(new GridLayout(11,1, 0, 10));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 60));
        leftPanel.setLayout(new GridLayout(11,1, 0, 10));

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
    private void setupBotPanel(int panelWidth, int panelHeight)
    {
        //setup top row, playerButtonPanel
        JPanel playerButtonPanel = new JPanel();
        playerButtonPanel.setPreferredSize(new Dimension(panelWidth, (int)(panelHeight*.1)));
        playerButtonPanel.setBackground(bgColor);
        playerButtonPanel.setLayout(new GridLayout(1,10));

        for(int i = 0; i < 3; i++)
        {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(bgColor);
            playerButtonPanel.add(emptyCell);
        }
        setButton = new JButton("Complete");
        playerButtonPanel.add(setButton);
        for(int i = 0; i < 2; i++)
        {
            JPanel emptyCell = new JPanel();
            emptyCell.setBackground(bgColor);
            playerButtonPanel.add(emptyCell);
        }
        hitButton = new JButton("Hit");
        playerButtonPanel.add(hitButton);
        for(int i = 0; i < 3; i++)
        {
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
    private void setupCenterPanel()
    {
        //Set up draw & discard pile panel (left)
        centerLeftPanel = new JPanel();
        centerLeftPanel.setBackground(bgColor);
        centerLeftPanel.setPreferredSize(new Dimension((int)(boardSize.getWidth()/4),(int) boardSize.getHeight()));
        int vgap = (int)(boardSize.getHeight()/6); //distance from top and bottom of centerPanel
        centerLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, vgap));

        drawPile = new cardButton(cardBack0);
        drawPile.setBorder(BorderFactory.createEmptyBorder());
        drawPile.setPreferredSize(new Dimension(cardWidth,cardHeight));
        centerLeftPanel.add(drawPile);

        //FUTURE NOTE: actual card displayed should be determined after integration
        discardPile = new cardButton();
        discardPile.setBorder(BorderFactory.createEmptyBorder());
        discardPile.setPreferredSize(new Dimension(cardWidth,cardHeight));
        centerLeftPanel.add(discardPile);

        //Set up completed phase sets panel (centerRightPanel)
        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(bgColor);
        centerRightPanel.setBorder(BorderFactory.createLineBorder(bgColor, 20));
        centerRightPanel.setPreferredSize(new Dimension((int) ((boardSize.getWidth()/2)),(int) boardSize.getHeight()));

        centerPanel.add(centerLeftPanel, BorderLayout.WEST);
        centerPanel.add(centerRightPanel, BorderLayout.EAST);
    }

    //At beginning of each new round deal new cards and new draw/discard pile
    private void newRound()
    {
        round = round + 1;
        turn = 1; //Default is 1. Player 1 = 1, Player 2 = 2,..., PLayer 4 = 4
        numSelected = 0;
        numSetsCompleted = 0;
        selectedCards = new ArrayList<cardButton>();
        playerCards = new ArrayList<cardButton>();
        p2Cards = new ArrayList<cardButton>();
        p3Cards = new ArrayList<cardButton>();
        p4Cards = new ArrayList<cardButton>();
        topPanel.removeAll();
        leftPanel.removeAll();
        rightPanel.removeAll();

        //1) Scoreboard
        setupMenu();

        //2) Deal the cards
        deal();

        //3) Update Discard Pile
        centerLeftPanel.remove(discardPile);
        ImageIcon icon = new ImageIcon("yellow three.png");
        cardButton dCard = new cardButton(icon);
        dCard.setBorder(BorderFactory.createEmptyBorder());
        dCard.setPreferredSize(new Dimension(cardWidth,cardHeight));
        discardPile = dCard;
        centerLeftPanel.add(discardPile);

        //4) Refreshes the set area to appropriate settings for each phase round
        updateSetSettings();

        //5) Start GAME
        game();

    }

    //At the start of each set 10 cards (buttons) get added to the player panels
    private void deal()
    {
        //CPU CARDS, all should be card backs, player should not be able to see other cards
        int sideWidth = (int)(cardHeight*.3);
        int sideHeight = (int)(cardWidth*.3);
        ImageIcon rightCard = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        ImageIcon leftCard = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));

        int topWidth = (int)(cardWidth*.5);
        int topHeight = (int)(cardHeight*.5);
        ImageIcon topCard = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight, Image.SCALE_DEFAULT));

        //CPU CARDS, all should be card backs, player should not be able to see other cards
        //Right Panel
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(rightCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            rightPanel.add(card);
            p2Cards.add(card);
        }
        //Top Panel Cards
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(topCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(topWidth, topHeight));
            topPanel.add(card);
            p3Cards.add(card);
        }
        //Left Panel
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(leftCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            leftPanel.add(card);
            p4Cards.add(card);
        }

        //Bot Panel Cards, PLAYER
        int cWidth = (int)(cardWidth*.9);
        int cHeight = (int)(cardHeight*.9);

        //Bottom row (playerCardPanel) = cards
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton("" + i);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(cWidth, cHeight));
            playerCards.add(card);
        }
        updatePlayerCardPanel();

    }

    // Update necessary changes for each phase
    // Max cards necessary for a set in phase (maxSelect), number of potential completed sets (rows & cols)
    // what phase logic to call when player clicks button(s) (setButton, hitButton)
    private void updateSetSettings()
    {
        if(round == 1)
        {
            System.out.println("== ROUND 1 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 3;
            maxSelect[1] = 0;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 2) {
            System.out.println("== ROUND 2 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 3;
            maxSelect[1] = 4;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 3) {
            System.out.println("== ROUND 3 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 4;
            maxSelect[1] = 0;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 4) {
            System.out.println("== ROUND 4 ==");
            rows = 4;
            cols = 1;
            maxSelect = new int[2];
            maxSelect[0] = 7;
            maxSelect[1] = 0;
            numSetsNeeded = 1;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 5) {
            System.out.println("== ROUND 5 ==");
            rows = 4;
            cols = 1;
            maxSelect = new int[2];
            maxSelect[0] = 8;
            maxSelect[1] = 0;
            numSetsNeeded = 1;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 6) {
            System.out.println("== ROUND 6 ==");
            rows = 4;
            cols = 1;
            maxSelect = new int[2];
            maxSelect[0] = 9;
            maxSelect[1] = 0;
            numSetsNeeded = 1;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 7) {
            System.out.println("== ROUND 7 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 4;
            maxSelect[1] = 0;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 8) {
            System.out.println("== ROUND 8 ==");
            rows = 4;
            cols = 1;
            maxSelect = new int[2];
            maxSelect[0] = 7;
            maxSelect[1] = 0;
            numSetsNeeded = 1;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else if(round == 9) {
            System.out.println("== ROUND 9 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 5;
            maxSelect[1] = 2;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
        else {
            System.out.println("== ROUND 10 ==");
            rows = 4;
            cols = 2;
            maxSelect = new int[2];
            maxSelect[0] = 5;
            maxSelect[1] = 3;
            numSetsNeeded = 2;

            //Load completedSetPanels with empty panels to be then added to centerRightPanel
            completedSetPanels = new ArrayList<JPanel>();
            for(int i = 0; i < (rows*cols); i++)
            {
                JPanel holder = new JPanel();
                holder.setBackground(bgColor);
                completedSetPanels.add(holder);
            }
            updateCenterRightPanel();
        }
    }

    // Refresh/reset the set area to show completedSetPanels
    private void updateCenterRightPanel()
    {
        centerRightPanel.removeAll();
        centerRightPanel.setLayout(new GridLayout(rows, cols));

        for(JPanel set : completedSetPanels)
        {
            centerRightPanel.add(set);
        }
        centerRightPanel.revalidate();
    }

    // Refresh/reset playerCardPanel to show correct cards
    private void updatePlayerCardPanel()
    {
        playerCardPanel.removeAll();
        for(cardButton card : playerCards)
        {
            card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
            playerCardPanel.add(card);
        }
        playerCardPanel.revalidate();
        playerCardPanel.repaint();
    }

    // Refresh/rest CPUCardPanels to show correct cards
    private void updateCPUCardPanels()
    {
        if(turn == 2) {
            rightPanel.removeAll();
            for (cardButton card : p2Cards) {
                card.setPreferredSize(new Dimension((int) (cardWidth * .3), (int) (cardHeight * .3)));
                rightPanel.add(card);
            }
            rightPanel.revalidate();
            rightPanel.repaint();
        }
        if(turn == 3) {
            topPanel.removeAll();
            for (cardButton card : p3Cards) {
                card.setPreferredSize(new Dimension((int) (cardWidth * .5), (int) (cardHeight * .5)));
                topPanel.add(card);
            }
            topPanel.revalidate();
            topPanel.repaint();
        }
        if(turn == 4) {
            leftPanel.removeAll();
            for (cardButton card : p4Cards) {
                card.setPreferredSize(new Dimension((int) (cardWidth * .3), (int) (cardHeight * .3)));
                leftPanel.add(card);
            }
            leftPanel.revalidate();
            leftPanel.repaint();
        }

    }

    //When player click "Complete" (setButton), method will be called to add it into the completed set area (centerRightPanel)
    private void addCompletedPlayerSet()
    {
        int cWidth = (int)(cardWidth*.4);
        int cHeight = (int)(cardHeight*.4);
        JPanel set = new JPanel();
        set.setBackground(bgColor);
        set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        for(cardButton card : selectedCards)
        {
            playerCards.remove(card);
            //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
            card.setBorder(BorderFactory.createEmptyBorder());
            card.unselect();
            card.setPreferredSize(new Dimension(cWidth, cHeight));
            set.add(card);
        }
        numSelected = 0;
        selectedCards = new ArrayList<cardButton>(); //clear selectedCards, because all selectedCards are now a completed set in set area
        completedSetPanels.set(numSetsCompleted, set); //update completedSetPanels to include new set
        numSetsCompleted++;

        updatePlayerCardPanel();
        updateCenterRightPanel();
    }

    // Game starts, manages player turns and determines round
    private void game()
    {
        if(round <= 10) {
            System.out.println("Player 1's turn");
            playerDraw();
        }
        else{
            System.out.println("GAME OVER");
        }
    }

    // 1) Player must draw a card.
    private void playerDraw()
    {
        drawPile.setBorder(BorderFactory.createLineBorder(Color.yellow, 5));
        drawPile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Player has drawn a card!\n");

                cardButton card = new cardButton("drawn card");
                card.setBorder(BorderFactory.createEmptyBorder());
                card.setPreferredSize(new Dimension((int) (cardWidth * .9), (int) (cardHeight * .9)));
                playerCards.add(card);
                updatePlayerCardPanel();

                //Disable drawPile button
                for(ActionListener al : drawPile.getActionListeners()) {
                    drawPile.removeActionListener(al);
                }
                drawPile.setBorder(BorderFactory.createEmptyBorder()); //remove highlight from drawPile

                playerTurn(); //draw step over
            }
        });
    }

    // 2) Player decides to either discard or add set/hit
    private void playerTurn()
    {
        toggleCardSelection(); //make cards selectable

        //Enables player buttons as options for player
        if(numSetsCompleted < numSetsNeeded)
            playerCreateASet();
        else if(playerCards.size() > 1) //if player only has one card left must discard.
            playerHit();
        playerDiscard();
    }

    // If player still needs to complete sets --> allow player to attempt to create phase set
    private void playerCreateASet()
    {
        System.out.println("Player can create a set");
        setButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                //NOTE: Call michael's logic if()
                if((numSelected == maxSelect[0]) || ((maxSelect[1] != 0) && (numSelected == maxSelect[1])))
                {
                    System.out.println("\nPlayer has completed a phase set!");
                    addCompletedPlayerSet();
                    disablePlayerFunctions();
                    playerTurn();
                }
            }
        });
    }

    //If player has one card selected & presses discard pile --> discard that card
    private void playerDiscard()
    {
        System.out.println("Player can discard");
        discardPile.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
        discardPile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedCards.size() == 1) {
                    System.out.println("\nDiscarded");

                    for(cardButton card : selectedCards)
                    {
                        playerCards.remove(card);
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
                    numSelected = 0;
                    selectedCards = new ArrayList<cardButton>(); //clear selectedCards, because selected card is now discard pile
                    playerCardPanel.repaint();

                    disablePlayerFunctions();
                    System.out.println("Player turn over.\n");
                    nextPlayer();
                }
            }
        });
    }

    private void playerHit()
    {
        System.out.println("\nPlayer can hit");
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedCards.size() == 1)
                {
                    System.out.println("\nHitting card");
                    toggleSetSelection();
                }
            }
        });
    }

    //Helper Method to make player cards selectable
    private void toggleCardSelection()
    {
        colorIndex = 0;
        for(cardButton card : playerCards)
        {
            // Selecting a card will highlight it & add it to selectedCards (ArrayList)
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(!(card.isSelected()))
                    {
                        System.out.println("a card has been selected");
                        card.setBorder(BorderFactory.createLineBorder(selectionColors[colorIndex], 5));
                        card.select();
                        numSelected++;
                        colorIndex++;
                        if(colorIndex == selectionColors.length)
                            colorIndex = 0;
                        selectedCards.add(card);
                    }
                    else if(card.isSelected())
                    {
                        card.setBorder(BorderFactory.createEmptyBorder());
                        card.unselect();
                        numSelected--;
                        selectedCards.remove(card);
                    }
                }
            });
        }
    }

    private void toggleSetSelection()
    {
        for(JPanel panel : completedSetPanels)
        {
            if(panel.getComponentCount() > 0)
            {
                panel.addMouseListener(new MouseListener()
                {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("a panel has been selected");
                        int cWidth = (int)(cardWidth*.4);
                        int cHeight = (int)(cardHeight*.4);

                        for(cardButton card : selectedCards)
                        {
                            playerCards.remove(card);
                            updatePlayerCardPanel();

                            //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                            card.setBorder(BorderFactory.createEmptyBorder());
                            card.unselect();
                            card.setPreferredSize(new Dimension(cWidth, cHeight));
                            panel.add(card);
                        }
                        numSelected = 0;
                        selectedCards = new ArrayList<cardButton>(); //clear selectedCards, because all selectedCards are now a completed set in set area

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
    private void disablePlayerFunctions()
    {
        System.out.println("\nPLAYER FUNCTIONS ARE DISABLED\n");

        //playerCards are unselectable
        for(cardButton card : playerCards)
        {
            for(ActionListener al : card.getActionListeners()) {
                card.removeActionListener(al);
            }
        }
        //disable setButton
        for(ActionListener al : setButton.getActionListeners()) {
            setButton.removeActionListener(al);
        }
        //disable discardPile
        for(ActionListener al : discardPile.getActionListeners())
        {
            discardPile.removeActionListener(al);
        }
        //disable hitButton
        for(ActionListener al : hitButton.getActionListeners()) {
            hitButton.removeActionListener(al);
        }
        //panels unselectable for hitting
        for(JPanel panel : completedSetPanels)
        {
            for(MouseListener ml : panel.getMouseListeners())
            {
                panel.removeMouseListener(ml);
            }
        }
    }

    private void CPUDraw()
    {
        System.out.println("A CPU has drawn a card!\n");

        int sideWidth = (int)(cardHeight*.3);
        int sideHeight = (int)(cardWidth*.3);
        ImageIcon rightCard = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        ImageIcon leftCard = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        int topWidth = (int)(cardWidth*.5);
        int topHeight = (int)(cardHeight*.5);
        ImageIcon topCard = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight, Image.SCALE_DEFAULT));

        cardButton card = new cardButton();
        card.setBorder(BorderFactory.createEmptyBorder());
        if(turn == 2) {
            card.setIcon(rightCard);
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            p2Cards.add(card);
            updateCPUCardPanels();
        }
        if(turn == 3) {
            card.setIcon(topCard);
            card.setPreferredSize(new Dimension(topWidth, topHeight));
            p3Cards.add(card);
            updateCPUCardPanels();
        }
        if(turn == 4) {
            card.setIcon(leftCard);
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            p4Cards.add(card);
            updateCPUCardPanels();
        }

        CPUTurn(); //draw step over
    }

    private void CPUTurn()
    {
        // !MICHAELS LOGIC!
        System.out.println("Player passing...\n");
        CPUDiscard();
    }

    private void CPUDiscard()
    {
        System.out.println("CPU Discarding");
        cardButton card;

        if(turn == 2) {
            card = p2Cards.get(0); //MICHAELS LOGIC
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
        }
        else if(turn == 3) {
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
        }
        else if(turn == 4) {
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
        nextPlayer();
    }

    private void addCompletedCPUSet()
    {
        int cWidth = (int)(cardWidth*.4);
        int cHeight = (int)(cardHeight*.4);
        JPanel set = new JPanel();
        set.setBackground(bgColor);
        set.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        if(turn == 2) {
            //michaels logic
            for (cardButton card : selectedCards)
            {
                p2Cards.remove(card);
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }
        if(turn == 3) {
            //michaels logic
            for (cardButton card : selectedCards)
            {
                p3Cards.remove(card);
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }
        if(turn == 4) {
            //michaels logic
            for (cardButton card : selectedCards)
            {
                p4Cards.remove(card);
                //Deselect and resize cards to place in JPanel (set) to be then added into centerRightPanel
                card.setBorder(BorderFactory.createEmptyBorder());
                card.unselect();
                card.setPreferredSize(new Dimension(cWidth, cHeight));
                set.add(card);
            }
        }
        updateCPUCardPanels();
        completedSetPanels.set(numSetsCompleted, set); //update completedSetPanels to include new set
        numSetsCompleted++;
        updateCenterRightPanel();
    }

    private void nextPlayer()
    {
        turn++;
        if(turn == 5) //If player 4 finishes turn --> 5, so go back to 1 to return to player 1
            turn = 1;

        //If any CPU or the player has no more cards --> new round
        if(playerCards.size() > 0)
        {
            if(turn == 1) {
                System.out.println("Player 1's turn");
                playerDraw();
            }
            else if(turn == 2) {
                System.out.println("Player 2's turn");
                CPUDraw();
            }
            else if(turn == 3) {
                System.out.println("Player 3's turn");
                CPUDraw();
            }
            else {
                System.out.println("Player 4's turn");
                CPUDraw();
            }
        }
        else
            newRound();
    }




}

