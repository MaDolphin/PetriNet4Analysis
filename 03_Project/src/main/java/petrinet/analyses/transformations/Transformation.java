package petrinet.analysis.transformations;

import petrinet._ast.ASTPetrinet;

public class Transformation {

    /**
     * Applies the six semantic-preservation transitions in [Mur89] until simplification is no longer possible.
     * @param petrinet A petrinet that should be simplified
     * @return A new petrinet with the same semantics as the input
     */
    public static ASTPetrinet simplify(ASTPetrinet petrinet) {
        ASTPetrinet previous;
        ASTPetrinet result = petrinet;

        do {
            previous = result;
            result = SimpleTransformations.fusionOfSeriesPlaces(result);
            result = SimpleTransformations.fusionOfSeriesTransitions(result);
            result = SimpleTransformations.fusionOfParallelPlaces(result);
            result = SimpleTransformations.fusionOfParallelTransitions(result);
            result = SimpleTransformations.eliminationOfSelfLoopTransitions(result);
            result = SimpleTransformations.eliminationOfSelfLoopPlaces(result);
        } while (!result.deepEquals(previous));

        return result;
    }
}
