package phases;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is the collection of phase rules.
 */
public class PhaseCollection {
    private final ArrayList<LinkedList<Rule>> phases;
    public PhaseCollection() {
        phases = new ArrayList<>();
    }

    public void addPhase(LinkedList<Rule> phase) {
        phases.add(phase);
    }

    /**
     * @param phase the rule number
     * @return the rules for a phase
     */
    public LinkedList<Rule> getPhase(int phase) {
        return phases.get(phase);
    }

    /**
     * @return the list of rules
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        int index = 1;
        for (LinkedList<Rule> rules: phases) {
            output.append(index++).append(". ");
            for (int i = 0; i < rules.size(); i++) {
                if (i != 0) {
                    output.append(" + ");
                }
                output.append(rules.get(i).toString());
            }
            output.append("\n");
        }
        return output.toString();
    }
}
