package petrinet.analyses.transformations;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import petrinet._ast.ASTPetrinet;

class SimpleTransformationsTest extends AbstractTransformationTest {

    @ParameterizedTest
    @CsvSource({"SeriesPlaces1.pn, SeriesPlaces1_Transformed.pn"})
    void testFusionOfSeriesPlaces(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.fusionOfSeriesPlaces(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }

    @ParameterizedTest
    @CsvSource({"SeriesTransitions1.pn, SeriesTransitions1_Transformed.pn"})
    void testFusionOfSeriesTransitions(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.fusionOfSeriesTransitions(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }

    @ParameterizedTest
    @CsvSource({"ParallelTransitions1.pn, ParallelTransitions1_Transformed.pn"})
    void testFusionOfParallelTransitions(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.fusionOfParallelTransitions(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }

    @ParameterizedTest
    @CsvSource({"ParallelPlaces1.pn, ParallelPlaces1_Transformed.pn"})
    void testFusionOfParallelPlaces(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.fusionOfParallelPlaces(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }

    @ParameterizedTest
    @CsvSource({"SelfLoopPlace1.pn, SelfLoopPlace1_Transformed.pn",
                "SelfLoopPlace2.pn, SelfLoopPlace2.pn"})
    void testEliminationOfSelfLoopPlaces(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.eliminationOfSelfLoopPlaces(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }

    @ParameterizedTest
    @CsvSource({"SelfLoopTransition1.pn, SelfLoopTransition1_Transformed.pn"})
    void testEliminationOfSelfLoopTransitions(String netFilename, String transformedNetFilename) {
        ASTPetrinet petrinet = parseAndRunCoCos(netFilename);
        ASTPetrinet transformedNet = SimpleTransformations.eliminationOfSelfLoopTransitions(petrinet);
        testNetMatching(transformedNet, transformedNetFilename);
    }
}
