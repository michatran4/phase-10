package cards;

import phases.Rule;

import java.util.LinkedList;

public class MiddlePile {
    private LinkedList<Card> cards;
    private LinkedList<Rule> rules;
    public MiddlePile(LinkedList<Card> dropped, LinkedList<Rule> list) {
        cards = new LinkedList<>(dropped);
        rules = new LinkedList<>(list);
    }
}
