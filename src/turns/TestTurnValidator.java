package turns;

import cards.Card;
import org.junit.Test;
import phases.PhaseCollection;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

public class TestTurnValidator {
    private final PhaseCollection phases;
    private final TurnValidator validator;

    public TestTurnValidator() {
        phases = new PhaseCollection();
        validator = new TurnValidator(true);
    }

    @Test
    public void testPhaseOne() { // two number sets of three
        LinkedList<Card> droppedCards = new LinkedList<>();
        droppedCards.add(new Card(3, "RED"));
        droppedCards.add(new Card(3, "GREEN"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(4, "RED"));
        droppedCards.add(new Card(4, "GREEN"));
        droppedCards.add(new Card(4, "BLUE"));

        Turn t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(1)));
        System.out.println();

        droppedCards.pop();
        droppedCards.add(new Card("WILD"));
        t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(1)));
    }

    @Test
    public void testPhaseTwo() { // 1 set of 3 and 1 run of 4
        LinkedList<Card> droppedCards = new LinkedList<>();
        droppedCards.add(new Card(1, "RED"));
        droppedCards.add(new Card(2, "GREEN"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(4, "YELLOW"));
        droppedCards.add(new Card(3, "GREEN"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(3, "YELLOW"));
        Turn t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(2)));
        System.out.println();

        droppedCards.pop();
        droppedCards.add(new Card("WILD"));
        t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(2)));
    }

    @Test
    public void testPhaseThree() { // 1 set of 4 and 1 run of 4
        LinkedList<Card> droppedCards = new LinkedList<>();
        droppedCards.add(new Card(1, "RED"));
        droppedCards.add(new Card(2, "GREEN"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(3, "RED"));
        droppedCards.add(new Card(4, "YELLOW"));
        droppedCards.add(new Card(3, "GREEN"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(3, "YELLOW"));
        Turn t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(3)));
        System.out.println();

        droppedCards.pop();
        droppedCards.add(new Card("WILD"));
        t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(3)));
    }

    @Test
    public void testPhaseFour() { // 1 run of 7
        LinkedList<Card> droppedCards = new LinkedList<>();
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(4, "YELLOW"));
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card(6, "YELLOW"));
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card(8, "RED"));

        Turn t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(4)));
    }

    // yes I am super confident after testing phase 4 with multiple wilds
    @Test
    public void testPhaseEight() { // 7 cards of one color
        LinkedList<Card> droppedCards = new LinkedList<>();
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card(3, "BLUE"));
        droppedCards.add(new Card(4, "BLUE"));
        droppedCards.add(new Card("WILD"));
        droppedCards.add(new Card(5, "BLUE"));
        droppedCards.add(new Card("WILD"));

        Turn t = new Turn(droppedCards, null, new LinkedList<>());
        assertTrue(validator.validate(t, phases.getPhase(8)));
    }
}
