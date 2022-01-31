import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class GUI {
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Dimension boardSize = new Dimension((int)((7)*(screenSize.getWidth()/8)), (int)screenSize.getHeight());
    private final Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height);
    private final Color bgColor = new Color(2, 48, 32);
    private final int cardWidth = 120;
    private final int cardHeight = 160;

    private Icon cardBack;
    private JFrame frame;
    private JPanel boardPanel, menuPanel; //main parent panels
    private JPanel topPanel, leftPanel, rightPanel, botPanel; //player panels
    private JPanel centerPanel, centerLeftPanel, centerRightPanel; //center panel
    private JButton drawPile, discardPile;
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
        //FUTURE NOTE: "00" needs to be replaced with actual scores.
        scoreboard = new JTextPane();
        Font font = new Font("Dialog", Font.PLAIN, 20);
        scoreboard.setFont(font);

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        scoreboard.setCharacterAttributes(attributeSet, true);
        Document doc = scoreboard.getStyledDocument();
        try
        {
            StyleConstants.setFontSize(attributeSet, 28);
            StyleConstants.setBold(attributeSet, true);
            StyleConstants.setItalic(attributeSet, true);
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), ("Scoreboard\n\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(attributeSet, 18);
            doc.insertString(doc.getLength(), ("Player 1: " + "00\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 2: " + "00\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 3: " + "00\n\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 4: " + "00\n\n"), attributeSet);
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

        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (int)((1.75)*top_botPanelHeight)));
        botPanel.setLayout(new GridLayout(1,10));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        rightPanel.setLayout(new GridLayout(1,10));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, top_botPanelHeight));
        topPanel.setLayout(new GridLayout(1,10));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));
        leftPanel.setLayout(new GridLayout(1,10));

        centerPanel = new JPanel(); //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(bgColor);

        boardPanel.add(botPanel, BorderLayout.SOUTH);
        boardPanel.add(rightPanel, BorderLayout.EAST);
        boardPanel.add(topPanel, BorderLayout.NORTH);
        boardPanel.add(leftPanel, BorderLayout.WEST);
        boardPanel.add(centerPanel, BorderLayout.CENTER);
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

        ImageIcon img = new ImageIcon("back.png");
        cardBack = new ImageIcon(img.getImage().getScaledInstance(cardWidth, cardHeight, Image.SCALE_DEFAULT));
        drawPile = new JButton(cardBack);
        drawPile.setPreferredSize(new Dimension(cardWidth,cardHeight));
        centerLeftPanel.add(drawPile);

        //FUTURE NOTE: actual card displayed should be determined after integration
        Icon card = new ImageIcon("yellow three.png");
        discardPile = new JButton(card);
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
        deal();
    }

    //at the start of each set 10 cards (buttons) get added to the player panels
    public void deal()
    {

    }
}
