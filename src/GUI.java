import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GUI {
    private JFrame frame;
    private JPanel boardPanel, menuPanel, topPanel, leftPanel, rightPanel, botPanel, centerPanel, centerLeftPanel, centerRightPanel;
    private JButton drawPile, discardPile;

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Dimension boardSize = new Dimension((int)((7)*(screenSize.getWidth()/8)), (int)screenSize.getHeight());
    private final Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height);

    //Basic setup of the frame container, panels, cards piles (buttons)
    public GUI()
    {
        //Set up main container frame of all panels; utilizes borderLayout manager
        frame = new JFrame("Phase 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());


        //Set up parent panels; Left = Board, Right = Menu for scoreboard and rules
        //boardPanel is where game takes place, parent panel for player panels which hold cards
        boardPanel = new JPanel();
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setLayout(new BorderLayout());
        boardPanel.setPreferredSize(boardSize);

        menuPanel = new JPanel();
        menuPanel.setBackground(Color.white);
        Dimension menuSize = new Dimension((int)(screenSize.getWidth()/8), screenSize.height);
        menuPanel.setPreferredSize(menuSize);

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.EAST);


        //Set up panels that will hold cards of players using __layout manager
        int top_botPanelWidth = (int)(6*(boardSize.getWidth()/7));
        int top_botPanelHeight = (int)(boardSize.getHeight()/7);
        int sidePanelsWidth = (int)(boardSize.getHeight()/7);
        int sidePanelsHeight = (int)(6*(boardSize.getWidth()/7));

        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(Color.RED);
        botPanel.setPreferredSize(new Dimension(top_botPanelWidth, (int)((1.75)*top_botPanelHeight)));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(Color.BLUE);
        rightPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(Color.GREEN);
        topPanel.setPreferredSize(new Dimension(top_botPanelWidth, top_botPanelHeight));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(Color.MAGENTA);
        leftPanel.setPreferredSize(new Dimension(sidePanelsWidth, sidePanelsHeight));

        centerPanel = new JPanel(); //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.DARK_GRAY);

        boardPanel.add(botPanel, BorderLayout.SOUTH);
        boardPanel.add(rightPanel, BorderLayout.EAST);
        boardPanel.add(topPanel, BorderLayout.NORTH);
        boardPanel.add(leftPanel, BorderLayout.WEST);
        boardPanel.add(centerPanel, BorderLayout.CENTER);


        //NOTE: centerPanel contains 2 more JPanels; Left = discard and draw piles, Right = completed phase sets
        //Set up draw & discard pile panel
        centerLeftPanel = new JPanel();
        centerLeftPanel.setBackground(Color.DARK_GRAY);
        centerLeftPanel.setPreferredSize(new Dimension((int)(boardSize.getWidth()/3),(int) boardSize.getHeight()));
        centerLeftPanel.setLayout(new FlowLayout());

        drawPile = new JButton("Draw");
        drawPile.setPreferredSize(new Dimension(90,120));
        centerLeftPanel.add(drawPile);

        //Set up completed phase sets panel
        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(Color.GRAY);
        centerRightPanel.setPreferredSize(new Dimension((int) boardSize.getWidth(),(int) boardSize.getHeight()));

        centerPanel.add(centerLeftPanel, BorderLayout.WEST);
        centerPanel.add(centerRightPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }
}
