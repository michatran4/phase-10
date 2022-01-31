package cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDeck {
    CPUDeck cpuDeck;

    public TestDeck() {
        cpuDeck = new CPUDeck();
    }

    @Test
    public void testSizeAndToString() {
        assertEquals(0, cpuDeck.getSize());
        cpuDeck.addCard(new Card(1, "RED"));
        assertEquals(1, cpuDeck.getSize());
        cpuDeck.addCard(new Card(1, "RED"));
        assertEquals("{RED 1=1, RED 1=1}", cpuDeck.deck.toString());
        assertEquals("{RED 1=1, RED 1=1}", cpuDeck.getDeck().toString());
        assertEquals(2, cpuDeck.getSize());
        cpuDeck.clear();
        assertEquals("{}", cpuDeck.deck.toString());
        assertEquals(0, cpuDeck.getSize());
    }

    @Test
    public void testRemoveNum() {
        // TODO
    }
}
