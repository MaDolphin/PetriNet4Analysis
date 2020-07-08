package petrinet._ast;

import petrinet.analysis.CoverabilityTree;

import java.util.List;
import java.util.Optional;

public class ASTPetrinet extends ASTPetrinetTOP {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<CoverabilityTree> coverabilityTree = Optional.empty();

    private boolean dirty = true;

    public ASTPetrinet() {
        super();
    }

//    public ASTPetrinet(String name,
//                       List<ASTAssertion> assertions,
//                       List<ASTPlace> places,
//                       List<ASTTransition> transitions) {
//        super(name, assertions, places, transitions);
//    }

    /**
     * Compute the coverability tree for this petrinet (cf. [Mur89, V.A])
     * @return A {@link CoverabilityTree} derived from this petrinet (and initial marking)
     */
    public CoverabilityTree getCoverabilityTree() {
        deriveGraphInfo();
        if (!coverabilityTree.isPresent()) {
            coverabilityTree = Optional.of(new CoverabilityTree(this));
        }

        return coverabilityTree.get();
    }

    /**
     * Mark that there has been a change to the petrinet structure.
     * Place/transition references and all derived properties (e.g., the {@link CoverabilityTree} need to be recomputed
     * upon the next call of {@link #deriveGraphInfo()}
     */
    public void setDirty() {
        dirty = true;
    }

    /**
     * Create reverse references from places to edges and transitions and derive other structures
     * (e.g., the {@link CoverabilityTree}) from this petrinet
     * @implNote This method can safely be called whenever the structures are requested, without performance impact.
     * Unless {@link #setDirty()} has been called, computations are cached.
     */
    public void deriveGraphInfo() {
        if (!dirty) {
            return;
        }

        forEachPlaces(ASTPlace::clearInEdges);
        forEachPlaces(ASTPlace::clearOutEdges);

        for (ASTTransition t : getTransitionList()) {
            for (ASTFromEdge e : t.getFromEdgeList()) {
                e.setTransition(t);
                e.getPlaceDefinition().addOutEdge(e);
            }
            for (ASTToEdge e : t.getToEdgeList()) {
                e.setTransition(t);
                e.getPlaceDefinition().addInEdge(e);
            }
        }

        coverabilityTree = Optional.empty();

        dirty = false;
    }

    public boolean deepEqualsWithReferences(Object other, boolean order) {
        deriveGraphInfo();
        if (other instanceof ASTPetrinet) {
            ((ASTPetrinet) other).deriveGraphInfo();
        }
        return super.deepEquals(other, order);
    }
}
