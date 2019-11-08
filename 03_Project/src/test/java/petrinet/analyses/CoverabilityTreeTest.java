package petrinet.analyses;

import org.junit.jupiter.api.Test;
import petrinet._ast.ASTPetrinet;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CoverabilityTreeTest extends AbstractAnalysisTest {

    @Test
    void testFig16Coverability() {
        ASTPetrinet net = parseAndRunCoCos("Fig16.pn");

        CoverabilityTree ct = net.getCoverabilityTree();
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), ct.marking.keys());
        assertEquals(new TokenCount(1), ct.marking.get("P1"));
        assertEquals(new TokenCount(0), ct.marking.get("P2"));
        assertEquals(new TokenCount(0), ct.marking.get("P3"));

        assertEquals(2, ct.children.size());

        CoverabilityTree left = ct.children.get("T1");
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), left.marking.keys());
        assertEquals(new TokenCount(0), left.marking.get("P1"));
        assertEquals(new TokenCount(0), left.marking.get("P2"));
        assertEquals(new TokenCount(1), left.marking.get("P3"));
        assertEquals(ct, left.parent);
        assertEquals(0, left.children.size());

        CoverabilityTree right = ct.children.get("T3");
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), right.marking.keys());
        assertEquals(new TokenCount(1), right.marking.get("P1"));
        assertTrue(right.marking.get("P2") instanceof InfiniteTokenCount);
        assertEquals(new TokenCount(0), right.marking.get("P3"));
        assertEquals(ct, right.parent);

        assertEquals(2, right.children.size());

        CoverabilityTree r_right = right.children.get("T3");
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), r_right.marking.keys());
        assertEquals(new TokenCount(1), r_right.marking.get("P1"));
        assertTrue(r_right.marking.get("P2") instanceof InfiniteTokenCount);
        assertEquals(new TokenCount(0), r_right.marking.get("P3"));
        assertEquals(right, r_right.parent);
        assertEquals(0, r_right.children.size());

        CoverabilityTree r_left = right.children.get("T1");
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), r_left.marking.keys());
        assertEquals(new TokenCount(0), r_left.marking.get("P1"));
        assertTrue(r_left.marking.get("P2") instanceof InfiniteTokenCount);
        assertEquals(new TokenCount(1), r_left.marking.get("P3"));
        assertEquals(right, r_left.parent);

        assertEquals(1, r_left.children.size());

        CoverabilityTree r_l_child = r_left.children.get("T2");
        assertEquals(new HashSet<>(Arrays.asList("P1", "P2", "P3")), r_l_child.marking.keys());
        assertEquals(new TokenCount(0), r_l_child.marking.get("P1"));
        assertTrue(r_l_child.marking.get("P2") instanceof InfiniteTokenCount);
        assertEquals(new TokenCount(1), r_l_child.marking.get("P3"));
        assertEquals(r_left, r_l_child.parent);
        assertEquals(0, r_l_child.children.size());
    }
}
