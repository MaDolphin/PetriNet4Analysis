package petrinet.analysis.transformations;

import petrinet._ast.ASTPetrinet;
import petrinet.analysis.AbstractAnalysisTest;
import petrinet.prettyprint.PetrinetPrettyPrinter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractTransformationTest extends AbstractAnalysisTest {

    @Override
    protected ASTPetrinet parseAndRunCoCos(String filename) {
        return super.parseAndRunCoCos("transformations/" + filename);
    }

    void testNetMatching(ASTPetrinet transformedNet, String filename) {
        ASTPetrinet expected = parseAndRunCoCos(filename);
        System.out.println(PetrinetPrettyPrinter.print(transformedNet));
        System.out.println(PetrinetPrettyPrinter.print(expected));
        assertTrue(expected.deepEqualsWithReferences(transformedNet, false), "Expected does not match transformation result");
    }
}
