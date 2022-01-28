import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class GUI {
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Dimension boardSize = new Dimension((int)((7)*(screenSize.getWidth()/8)), (int)screenSize.getHeight());
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
        Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height);
        menuPanel.setPreferredSize(menuSize);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.EAST);
    }

    private void setupMenu()
    {
        //Setup scoreboard
        //FUTURE NOTE: "00" needs to be replaced with actual scores.
        scoreboard = new JTextPane();

        /*
        Font font = new Font("Dialog", Font.BOLD, 20);
        scoreboard.setFont(font);
        scoreboard.append("\n\nScoreboard\n\n");
        scoreboard.append(("Player 1: " + "00\n"));
        scoreboard.append(("Player 2: " + "00\n"));
        scoreboard.append(("Player 3: " + "00\n"));
        scoreboard.append(("Player 4: " + "00\n"));

         */
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        scoreboard.setCharacterAttributes(attributeSet, true);
        Document doc = scoreboard.getStyledDocument();

        try
        {
            StyleConstants.setBold(attributeSet, true);
            doc.insertString(doc.getLength(), ("Scoreboard\n"), attributeSet);

            attributeSet = new SimpleAttributeSet();
            doc.insertString(doc.getLength(), ("Player 1: " + "00\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 2: " + "00\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 3: " + "00\n"), attributeSet);
            doc.insertString(doc.getLength(), ("Player 4: " + "00\n"), attributeSet);
        }
        catch (BadLocationException e)
        {
            System.err.println("Could not insert such text into scoreboard");
        }

        menuPanel.add(scoreboard);
    }

    //Set up panels that will hold cards of players using __layout manager
    private void setupPlayerPanels()
    {
        int top_botPanelWidth = (int)(6*(boardSize.getWidth()/7));
        int top_botPanelHeight = (int)(boardSize.getHeight()/7);
        int sidePanelsWidth = (int)(boardSize.getHeight()/7);
        int sidePanelsHeight = (int)(6*(boardSize.getWidth()/7));

        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(bgColor);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (int)((1.75)*top_botPanelHeight)));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(bgColor);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(bgColor);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, top_botPanelHeight));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(bgColor);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));

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
}
