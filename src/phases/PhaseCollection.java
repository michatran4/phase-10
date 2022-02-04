package phases;

import java.util.ArrayList;

/**
 * This is the collection of phase rules.
 */
public class PhaseCollection {
    private final ArrayList<Phase> phases;

    public PhaseCollection() {
        phases = new ArrayList<>();
        // phase 1
        Phase one = new Phase();
        one.add(new NumberSet(2, 3));
        // phase 2
        Phase two = new Phase();
        two.add(new NumberSet(1, 3));
        two.add(new NumberRun(1, 4));
        // phase 3
        Phase three = new Phase();
        three.add(new NumberSet(1, 4));
        three.add(new NumberRun(1, 4));
        // phase 4
        Phase four = new Phase();
        four.add(new NumberRun(1, 7));
        // phase 5
        Phase five = new Phase();
        five.add(new NumberRun(1, 8));
        // phase 6
        Phase six = new Phase();
        six.add(new NumberRun(1, 9));
        // phase 7
        Phase seven = new Phase();
        seven.add(new NumberSet(2, 4));
        // phase 8
        Phase eight = new Phase();
        eight.add(new ColorSet(7));
        // phase 9
        Phase nine = new Phase();
        nine.add(new NumberSet(1, 5));
        nine.add(new NumberSet(1, 2));

        phases.add(one);
        phases.add(two);
        phases.add(three);
        phases.add(four);
        phases.add(five);
        phases.add(six);
        phases.add(seven);
        phases.add(eight);
        phases.add(nine);
    }

    /**
     * @param phase the rule number
     * @return the rules for a phase
     */
    public Phase getPhase(int phase) {
        return phases.get(phase - 1);
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
