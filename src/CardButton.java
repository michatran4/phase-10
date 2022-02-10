import cards.Card;

import javax.swing.*;
import java.util.Objects;

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
        String path;
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
            path = color + " " + number + ".png";
        } else {
            if (value.equals("WILD")) {
                path = "wild.png";
            } else {
                path = "skip.png";
            }
        }
        return new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
    }

    public Card getCard() {
        return card;
    }
}
