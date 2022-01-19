package phases;

import java.util.LinkedList;

/**
 * This is the collection of phase rules.
 */
public class PhaseCollection {
    private final LinkedList<Rule> phases;
    public PhaseCollection(LinkedList<Rule> toAdd) {
        phases = new LinkedList<>();
        phases.addAll(toAdd);
    }

    /**
     * Returns a phase rule.
     * @param phase the rule number
     * @return a Rule, which states the amount and how many in the run/set.
     */
    public Rule getPhase(int phase) {
        return phases.get(phase);
    }

    /**
     * @return the list of rules
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        int index = 1;
        for (Rule rule: phases) {
            output.append(index).append(". ").append(rule.toString()).append("\n");
        }
        return output.toString();
    }
}
