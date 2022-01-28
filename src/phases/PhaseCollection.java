package phases;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is the collection of phase rules.
 */
public class PhaseCollection {
    private final ArrayList<Phase> phases;

    public PhaseCollection() {
        phases = new ArrayList<>();
    }

    public void addPhase(Phase phase) {
        phases.add(phase);
    }

    /**
     * @param phase the rule number
     * @return the rules for a phase
     */
    public Phase getPhase(int phase) {
        return phases.get(phase);
    }

    /**
     * @return the list of rules
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < phases.size(); i++) {
            output.append(i + 1).append(". ");
            output.append(phases.get(i).toString());
            output.append("\n");
        }
        return output.toString();
    }
}
