package cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDeck {
    CPUDeck cpuDeck;
    PlayerDeck playerDeck;

    public TestDeck() {
        cpuDeck = new CPUDeck(true);
        playerDeck = new PlayerDeck(true);
        for (int num = 1; num <= 12; num++) {
            for (String color: new String[]{"RED", "BLUE", "YELLOW", "GREEN"}) {
                for (int i = 0; i < 2; i++) {
                    playerDeck.addCard(new Card(num, color));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            playerDeck.addCard(new Card("SKIP"));
        }
        for (int i = 0; i < 8; i++) {
            playerDeck.addCard(new Card("WILD"));
        }
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
    public void testRemove() {
        assertEquals(108, playerDeck.getSize());
        assertEquals(108, playerDeck.toString().split(",").length);
        for (int i = 1; i <= 12; i++) {
            playerDeck.removeCardsWithNum(i);
            int removed = i * 8;
            assertEquals(108 - removed, playerDeck.getSize());
            assertEquals(108 - removed, playerDeck.toString().split(",").length);
        }
        playerDeck.removeCardsWithNum(13); // skips
        assertEquals(8, playerDeck.getSize());
        playerDeck.removeCardsWithNum(14); // wilds
        assertEquals(0, playerDeck.getSize());
    }

    @Test
    public void testRemoveCount() {
        playerDeck.removeCardsWithNum(12, 1);
        assertEquals(107, playerDeck.getSize());
        playerDeck.removeCardsWithColor("BLUE", 24);
        assertEquals(107 - 24, playerDeck.getSize());
        try {
            playerDeck.removeCardsWithColor("BLUE", 1);
            throw new RuntimeException();
        }
        catch (IllegalStateException ignored){}
    }
}
