import cards.Card;

import javax.swing.*;

public class CardButton extends JButton {
    private boolean selected;
    private Card card;

    public CardButton(Card c) {
        card = c;
        setIcon(getIconFromName(c.toString()));
    }
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

    private ImageIcon getIconFromName(String value) {
        ImageIcon icon;
        if (value.contains(" ")) {
            String[] name = value.split(" ");
            String color = name[0].toLowerCase();
            String number;
            switch (Integer.parseInt(name[1])) {
                case 1:
                    number = "one";
                    break;
                case 2:
                    number = "two";
                    break;
                case 3:
                    number = "three";
                    break;
                case 4:
                    number = "four";
                    break;
                case 5:
                    number = "five";
                    break;
                case 6:
                    number = "six";
                    break;
                case 7:
                    number = "seven";
                    break;
                case 8:
                    number = "eight";
                    break;
                case 9:
                    number = "nine";
                    break;
                case 10:
                    number = "ten";
                    break;
                case 11:
                    number = "eleven";
                    break;
                default:
                    number = "twelve";
            }
            icon = new ImageIcon(color + " " + number);
        } else {
            if (value.equals("WILD")) {
                icon = new ImageIcon("wild.png");
            } else {
                icon = new ImageIcon("skip.png");
            }
        }
        return icon;
    }
}
