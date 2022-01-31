package cards;

import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TreeMap;

public class TestPrune {
    private final Map<Integer, Integer> map;
    public TestPrune() {
        map = new TreeMap<>();
        map.put(1, 0);
        map.put(2, 1);
        map.put(3, 0);
    }
    @Test
    public void test() {
        try {
            for (Integer key: map.keySet()) {
                if (map.get(key) == 0) {
                    map.remove(key);
                }
            }
            throw new IllegalStateException("Should have one value of 0.");
        }
        catch (ConcurrentModificationException ignored) {}
        // while (map.values().remove(0));
    }
}
