package petrinet.analysis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import petrinet._ast.ASTPetrinet;

import static org.junit.jupiter.api.Assertions.*;

class SubclassTest extends AbstractAnalysisTest {

    @ParameterizedTest
    @CsvSource({"Fig25a.pn, true, false, true, true, true",
                "Fig25b.pn, false, true, true, true, true",
                "Fig25c.pn, false, false, false, true, true",
                "Fig25d.pn, false, false, false, false, true",
                "Fig25e.pn, false, false, false, false, false",
                "ACN.pn, false, false, false, false, true",
                "FCN.pn, false, false, true, true, true",
                "MarkedGraph.pn, false, true, true, true, true"})
    void testIsType(String pn_name, boolean isStateMachine, boolean isMarkedGraph,
                    boolean isFreeChoice, boolean isExtendedFreeChoice, boolean isAsymmetricChoice) {
        ASTPetrinet petrinet = parseAndRunCoCos(pn_name);
        boolean stateMachineResult = Subclass.isStateMachine(petrinet);
        boolean markedGraphResult = Subclass.isMarkedGraph(petrinet);
        boolean freeChoiceResult = Subclass.isFreeChoice(petrinet);
        boolean extendedFreeChoiceResult = Subclass.isExtendedFreeChoice(petrinet);
        boolean asymmetricChoiceResult = Subclass.isAsymmetricChoice(petrinet);
        String message = pn_name + " was tested as wrong type. ";
        assertEquals(isStateMachine, stateMachineResult, message + "(state machine)");
        assertEquals(isMarkedGraph, markedGraphResult, message + "(marked graph)");
        assertEquals(isFreeChoice, freeChoiceResult, message + "(free choice)");
        assertEquals(isExtendedFreeChoice, extendedFreeChoiceResult, message + "(extended free choice)");
        assertEquals(isAsymmetricChoice, asymmetricChoiceResult, message + "(asymmetric choice)");
    }

    @Test
    void testVennDiagramConstraints() {
        for (ASTPetrinet petrinet : parseAllAndRunCoCos()) {
            String message = petrinet.getName() + " does not satisfy the venn diagram constraints for ";
            if (!Subclass.isAsymmetricChoice(petrinet)) {
                assertFalse(Subclass.isExtendedFreeChoice(petrinet), message + "asymmetric choice nets.");
                assertFalse(Subclass.isFreeChoice(petrinet), message + "asymmetric choice nets.");
                assertFalse(Subclass.isMarkedGraph(petrinet), message + "asymmetric choice nets.");
                assertFalse(Subclass.isStateMachine(petrinet), message + "asymmetric choice nets.");
            } else if (!Subclass.isExtendedFreeChoice(petrinet)) {
                assertFalse(Subclass.isFreeChoice(petrinet), message + "extended free choice nets.");
                assertFalse(Subclass.isMarkedGraph(petrinet), message + "extended free choice nets.");
                assertFalse(Subclass.isStateMachine(petrinet), message + "extended free choice nets.");
            } else if (!Subclass.isFreeChoice(petrinet)) {
                assertFalse(Subclass.isMarkedGraph(petrinet), message + "free choice nets.");
                assertFalse(Subclass.isStateMachine(petrinet), message + "free choice nets.");
            }
        }
    }
}
