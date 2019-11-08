package petrinet.analyses;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import petrinet.StringArrayConverter;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTTransition;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LivenessTest extends AbstractAnalysisTest {

    @ParameterizedTest
    @ValueSource(strings = {"Fig2b.pn", "Fig4.pn", "Fig6.pn", "Fig9.pn", "Fig15.pn", "Fig17c.pn", "Fig17e.pn",
            "ProducerConsumerProblem.pn", "Fig14.pn"})
    void testAllLive(String pn_name) {
        ASTPetrinet net = parseAndRunCoCos(pn_name);
        for (ASTTransition transition : net.getTransitionList()) {
            assertTrue(Liveness.isL1Live(net.getCoverabilityTree(), transition));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Fig16.pn,'T1,T2,T3'",
            "Fig17a.pn,'t2,t3,t4'",
            "Fig17b.pn,'t2,t3,t4,t5'"
    })
    void testSomeLive(String pn_name, @ConvertWith(StringArrayConverter.class) String[] lives) {
        ASTPetrinet net = parseAndRunCoCos(pn_name);
        for (ASTTransition transition : net.getTransitionList()) {
            assertEquals(
                    Arrays.asList(lives).contains(transition.getName()),
                    Liveness.isL1Live(net.getCoverabilityTree(), transition),
                    transition.getName() + " not correctly analysed"
            );
        }
    }
}
