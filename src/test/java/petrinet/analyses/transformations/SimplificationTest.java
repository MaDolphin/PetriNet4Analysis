package petrinet.analyses.transformations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTTransition;
import petrinet._symboltable.TransitionSymbol;
import petrinet.analyses.AbstractAnalysisTest;
import petrinet.analyses.Boundedness;
import petrinet.analyses.Liveness;
import petrinet.prettyprint.PetrinetPrettyPrinter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimplificationTest extends AbstractAnalysisTest {

    @ParameterizedTest
    @CsvSource({
            "transformations/Fig9.pn,transformations/Fig9_Transformed.pn",
            "transformations/CookieMachine_modified.pn,transformations/CookieMachine_modified_transformed.pn",
    })
    void testSimplification(String oldPath, String newPath) {
        ASTPetrinet oldNet = parseAndRunCoCos(oldPath);
        ASTPetrinet newNet = Transformation.simplify(oldNet);
        ASTPetrinet expected = parseAndRunCoCos(newPath);
        System.out.println(PetrinetPrettyPrinter.print(expected));
        System.out.println(PetrinetPrettyPrinter.print(newNet));
        assertTrue(expected.deepEqualsWithReferences(newNet, false), "Transformed net not equal to expected");
    }

    @Test
    void testPreservation() {
        for (ASTPetrinet petrinet : parseAllAndRunCoCos()) {
            ASTPetrinet simplified = Transformation.simplify(petrinet);
            String message = petrinet.getName() + " does not preserve properties for ";

            System.out.println(PetrinetPrettyPrinter.print(petrinet));
            System.out.println(PetrinetPrettyPrinter.print(simplified));
            for (ASTTransition t : simplified.getTransitionList()) {
                Optional<TransitionSymbol> old = petrinet.getSpannedScope().resolve(t.getName(), TransitionSymbol.KIND);
                assertTrue(old.isPresent());
                Optional<ASTTransition> oldNode = old.get().getTransitionNode();
                assertTrue(oldNode.isPresent());
                assertEquals(
                        Liveness.isL1Live(petrinet.getCoverabilityTree(), oldNode.get()),
                        Liveness.isL1Live(simplified.getCoverabilityTree(), t),
                        message + "liveness: " + t.getName());
            }

            assertEquals(Boundedness.isBounded(petrinet), Boundedness.isBounded(simplified), message + "boundedness");
            assertEquals(Boundedness.isSafe(petrinet), Boundedness.isSafe(simplified), message + "safeness");
        }
    }
}
