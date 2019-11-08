package petrinet.analyses;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petrinet._ast.ASTPetrinet;

import static org.junit.jupiter.api.Assertions.*;

class BoundednessTest extends AbstractAnalysisTest {

    @ParameterizedTest
    @ValueSource(strings = {"Fig4.pn", "Fig6.pn", "Fig9.pn", "Fig15.pn", "TokenConsumption.pn"})
    void testSafePetrinets(String pn_name) {
        ASTPetrinet petrinet = parseAndRunCoCos(pn_name);
        assertTrue(Boundedness.isBounded(petrinet),
                "Bounded net " + pn_name + " was evaluated unbounded mistakenly.");
        assertTrue(Boundedness.isSafe(petrinet),
                "Safe net " + pn_name + " was evaluated unsafe mistakenly.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Fig2b.pn", "CookieMachine.pn", "VendingMachine.pn", "Fig14.pn"})
    void testBoundedUnsafePetrinets(String pn_name) {
        ASTPetrinet petrinet = parseAndRunCoCos(pn_name);
        assertTrue(Boundedness.isBounded(petrinet),
                "Bounded net " + pn_name + " was evaluated unbounded mistakenly.");
        assertFalse(Boundedness.isSafe(petrinet),
                "Unsafe net " + pn_name + " was evaluated safe mistakenly.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"CookieMachine_modified.pn", "transformations/CookieMachine_modified_transformed.pn",
            "Fig17a.pn", "Fig17b.pn", "Fig17c.pn", "Fig17e.pn", "Unbounded.pn",
            "ProducerConsumerProblem.pn"})
    void testUnboundedPetrinets(String pn_name) {
        ASTPetrinet petrinet = parseAndRunCoCos(pn_name);
        assertFalse(Boundedness.isBounded(petrinet),
                "Unbounded net " + pn_name + " was evaluated bounded mistakenly.");
        assertFalse(Boundedness.isSafe(petrinet),
                "Unsafe net " + pn_name + " was evaluated safe mistakenly.");
    }
}
