package petrinet._ast;

import petrinet.analysis.Boundedness;

import java.util.Optional;

public class ASTGlobalRequirement extends ASTGlobalRequirementTOP {

    public ASTGlobalRequirement() {
        super();
    }

//    public ASTGlobalRequirement(ASTGlobalFeature feature) {
//        super(feature);
//    }

    /**
     * Check whether this requirement is satisfied by the given petrinet.
     * @param petrinet A petrinet for which the requirement should hold
     * @return {@code Optional.empty()} if the result is unknown (does not belong to a verifiable special case); {@code Optional.of(result)} otherwise.
     */
    @Override
    public Optional<Boolean> verify(ASTPetrinet petrinet) {
        switch (globalFeature) {
            case SAFE:
                return Optional.of(Boundedness.isSafe(petrinet));
            case BOUNDED:
                return Optional.of(Boundedness.isBounded(petrinet));
            default:
                throw new IllegalArgumentException("Requirement does not exist");
        }
    }
}
