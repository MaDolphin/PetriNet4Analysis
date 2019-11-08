package petrinet._ast;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTTransition extends ASTTransitionTOP {

    public ASTTransition() {
        super();
    }

    public ASTTransition(String name, List<ASTFromEdge> fromEdges, List<ASTToEdge> toEdges) {
        super(name, fromEdges, toEdges);
    }

    /**
     * All edges (in- and outgoing) of this transition
     * @return A {@link Stream} object enumerating all edges in the transition
     *
     * @apiNote Relative edge order from {@link #streamFromEdges()} and {@link #streamToEdges()} is preserved
     */
    public Stream<ASTEdge> streamAllEdges() {
        return Stream.concat(streamFromEdges(), streamToEdges());
    }

    /**
     * The places with edges leading into this transition
     *
     * @return A {@link Set} of places at incoming edges
     */
    public Set<ASTPlace> getFromPlaces() {
        return streamFromEdges().map(ASTFromEdge::getPlaceDefinition).collect(Collectors.toSet());
    }

    /**
     * The places with edges originating from this transition
     *
     * @return A {@link Set} of places at outgoing edges
     */
    public Set<ASTPlace> getToPlaces() {
        return streamToEdges().map(ASTToEdge::getPlaceDefinition).collect(Collectors.toSet());
    }
}
