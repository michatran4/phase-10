package phases;

import java.util.LinkedList;

/**
 * A phase, which is just a list of rules.
 */
public class Phase {
    private final LinkedList<Rule> phase;

    public Phase() {
        phase = new LinkedList<>();
    }

    public void add(Rule r) {
        phase.add(r);
    }

    public LinkedList<Rule> getRules() {
        return phase;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < phase.size(); i++) {
            if (i != 0) {
                output.append(" + ");
            }
            output.append(phase.get(i).toString());
        }
        return output.toString();
    }
}
