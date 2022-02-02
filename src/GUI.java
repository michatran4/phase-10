import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private final Color bgColor = new Color(2, 48, 32);
    private final int cardWidth = 120;
    private final int cardHeight = 160;
    private final ImageIcon cardBack0 = new ImageIcon("back0.png");
    private final ImageIcon cardBack90 = new ImageIcon("back90.png");
    private final ImageIcon cardBack180 = new ImageIcon("back180.png");
    private final ImageIcon cardBack270 = new ImageIcon("back270.png");
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Dimension boardSize = new Dimension((int)((7)*(screenSize.getWidth()/8)), (int)screenSize.getHeight());
    private final Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height);

    private JFrame frame;
    private JPanel boardPanel, menuPanel; //main parent panels
    private JPanel topPanel, leftPanel, rightPanel, botPanel, playerCardPanel; //player panels
    private JPanel centerPanel, centerLeftPanel, centerRightPanel; //center panel
    private cardButton drawPile, discardPile; //cardButton is a class that extends JButton
    private JButton setButton, hitButton;
    private JTextPane scoreboard;

    //Basic setup of the frame container, panels, card piles (buttons)
    public GUI()
    {
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

    private void setupMenu()
    {
        //Setup scoreboard
        scoreboard = new JTextPane();

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

        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight+40)));
        botPanel.setLayout(new BorderLayout());
        setupBotPanel(top_botPanelWidth, (top_botPanelHeight+40));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, (top_botPanelHeight-40)));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 290, 0, 290));
        topPanel.setLayout(new GridLayout(1,10, 20, 0));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 45, 0, 50));
        rightPanel.setLayout(new GridLayout(10,1, 0, 10));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 45));
        leftPanel.setLayout(new GridLayout(10,1, 0, 10));

        centerPanel = new JPanel(); //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
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

        //setup bot row, playerCardPanel
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
        centerLeftPanel.setPreferredSize(new Dimension((int)(boardSize.getWidth()/3),(int) boardSize.getHeight()));
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

        //Set up completed phase sets panel (right)
        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(bgColor);
        centerRightPanel.setPreferredSize(new Dimension((int) boardSize.getWidth(),(int) boardSize.getHeight()));

        centerPanel.add(centerLeftPanel, BorderLayout.WEST);
        centerPanel.add(centerRightPanel, BorderLayout.EAST);
    }

    //at beginning of each new round deal new cards and new draw/discard pile
    public void newRound()
    {
        //Scoreboard
        updateScore();

        //Player cards
        deal();

        //Discard pile
        //NOTE: To be integrated
        Icon card = new ImageIcon("yellow three.png");
        discardPile.setIcon(card);

        //Completed Sets Area
        updateSetArea();

    }

    private void updateScore()
    {
        //Replace 0's with actual scores from logic branch
        int p1 = 0;
        int p2 = 0;
        int p3 = 0;
        int p4 = 0;
        int phase = 1; // <-- phase should be what round, ask michael

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
            doc.insertString(doc.getLength(), ("Phase : " + phase + "\n"), attributeSet);

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
    }

    //at the start of each set 10 cards (buttons) get added to the player panels
    private void deal()
    {
        //CPU CARDS, all should be card backs, player should not be able to see other cards
        int sideWidth = (int)(cardHeight*.4);
        int sideHeight = (int)(cardWidth*.4);
        ImageIcon rightCard = new ImageIcon(cardBack270.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));
        ImageIcon leftCard = new ImageIcon(cardBack90.getImage().getScaledInstance(sideWidth, sideHeight, Image.SCALE_DEFAULT));

        int topWidth = (int)(cardWidth*.6);
        int topHeight = (int)(cardHeight*.6);
        ImageIcon topCard = new ImageIcon(cardBack180.getImage().getScaledInstance(topWidth, topHeight, Image.SCALE_DEFAULT));

        //CPU CARDS, all should be card backs, player should not be able to see other cards
        //Right Panel
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(rightCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            rightPanel.add(card);
        }
        //Top Panel Cards
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(topCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(topWidth, topHeight));
            topPanel.add(card);
        }
        //Left Panel
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton(leftCard);
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(sideWidth, sideHeight));
            leftPanel.add(card);
        }

        //Bot Panel Cards, PLAYER
        dealPlayerCards();

    }

    //Helper method to set up player panel (botPanel)
    //Player panel consists of 2 rows, Top = buttons, bot = cards
    private void dealPlayerCards()
    {
        int cWidth = (int)(cardWidth*.9);
        int cHeight = (int)(cardHeight*.9);
        //Bottom row: cards
        for(int i = 0; i < 10; i++)
        {
            cardButton card = new cardButton();
            card.setBorder(BorderFactory.createEmptyBorder());
            card.setPreferredSize(new Dimension(cWidth, cHeight));
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!card.isSelected())
                    {
                        card.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
                        card.select();
                    }
                    else
                    {
                        card.setBorder(BorderFactory.createEmptyBorder());
                        card.unselect();
                    }
                }
            });
            playerCardPanel.add(card);
        }
    }

    private void updateSetArea()
    {
        centerRightPanel.removeAll();
        centerRightPanel.setLayout(new GridLayout(4, 2));
        //TO BE INTEGRATED, ask michael how to get round
        /*
        if(round == 1)
            centerRightPanel.setLayout(new GridLayout(4, 2));
        else if(round == 2)
            centerRightPanel.setLayout(new GridLayout(4, 2));
        else if(round == 3)
            centerRightPanel.setLayout(new GridLayout(4, 2));
        else if(round == 4)
            centerRightPanel.setLayout(new GridLayout(4, 1));
        else if(round == 5)
            centerRightPanel.setLayout(new GridLayout(4, 1));
        else if(round == 6)
            centerRightPanel.setLayout(new GridLayout(4, 1));
        else if(round == 7)
            centerRightPanel.setLayout(new GridLayout(4, 2));
        else if(round == 8)
            centerRightPanel.setLayout(new GridLayout(4, 1));
        else if(round == 9)
            centerRightPanel.setLayout(new GridLayout(4, 2));
        else
            centerRightPanel.setLayout(new GridLayout(4, 2));
         */

    }




}

