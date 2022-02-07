import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class cardButton extends JButton {
    private boolean selected;

    public cardButton()
    {
        super();
        selected = false;
    }

    public cardButton(String s)
    {
        super(s);
        selected = false;
    }

    public cardButton(ImageIcon icon)
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
