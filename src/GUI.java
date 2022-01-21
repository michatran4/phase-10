import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GUI {
    JFrame frame;
    JPanel topPanel, leftPanel, rightPanel, botPanel, centerPanel, centerLeftPanel, centerRightPanel;
    JButton drawPile, discardPile;

    //Basic setup of the frame container, panels, cards piles (buttons)
    public GUI()
    {
        //Set up main frame aka main container that holds all panels and utilizes borderlayout manager
        frame = new JFrame("Phase 10");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());

        //Set up panels that will hold cards of players using __layout manager
        botPanel = new JPanel(); //Contains Player 1, aka person playing
        botPanel.setBackground(Color.RED);
        botPanel.setPreferredSize(new Dimension((int) screenSize.getWidth()-50, 200));

        rightPanel = new JPanel(); //Contains Player 2 cards, CPU
        rightPanel.setBackground(Color.BLUE);
        rightPanel.setPreferredSize(new Dimension(150,(int) screenSize.getHeight()-50));

        topPanel = new JPanel(); //Contains Player 3 cards, CPU
        topPanel.setBackground(Color.GREEN);
        topPanel.setPreferredSize(new Dimension((int) screenSize.getWidth()-50, 100));

        leftPanel = new JPanel(); //Contains Player 4 cards, CPU
        leftPanel.setBackground(Color.MAGENTA);
        leftPanel.setPreferredSize(new Dimension(150,(int) screenSize.getHeight()-50));

        centerPanel = new JPanel(); //Contains the draw and discard piles (centerLeft) AND completed phase sets (centerRight)
        centerPanel.setBackground(Color.DARK_GRAY);

        frame.add(botPanel, BorderLayout.SOUTH);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);

        //Set up draw and discard pile
        //NOTE: centerPanel contains 2 more JPanels; left for the discard and draw piles, right for the completed phase sets
        centerPanel.setLayout(new BorderLayout());

        centerLeftPanel = new JPanel();
        centerLeftPanel.setBackground(Color.DARK_GRAY);
        centerLeftPanel.setPreferredSize(new Dimension((int) screenSize.getWidth()-1100,(int) screenSize.getHeight()-150));
        centerPanel.add(centerLeftPanel, BorderLayout.WEST);

        /*
        drawPile = new JButton("Draw");
        drawPile.setSize(20,40);
        centerLeftPanel.add(drawPile);
         */

        centerRightPanel = new JPanel();
        centerRightPanel.setBackground(Color.GRAY);
        centerRightPanel.setPreferredSize(new Dimension((int) screenSize.getWidth(),(int) screenSize.getHeight()-150));
        centerPanel.add(centerRightPanel, BorderLayout.EAST);


        frame.setVisible(true);
    }
}
