import cards.Card;

import javax.swing.*;

public class CardButton extends JButton {
    private boolean selected;

    public CardButton(String s)
    {
        super(s);
        selected = false;
    }

    public CardButton(ImageIcon icon)
    {
        super(icon);
        selected = false;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void select()
    {
        selected = true;
    }

    public void unselect()
    {
        selected = false;
    }
}
