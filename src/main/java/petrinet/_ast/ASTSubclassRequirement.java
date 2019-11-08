package petrinet._ast;

import petrinet.analyses.Subclass;

import java.util.Optional;

public class ASTSubclassRequirement extends ASTSubclassRequirementTOP {

    public ASTSubclassRequirement() {
        super();
    }

    public ASTSubclassRequirement(ASTSubclass subclass) {
        super(subclass);
    }

    /**
     * Check whether this requirement is satisfied by the given petrinet.
     * @param petrinet A petrinet for which the requirement should hold
     * @return {@code Optional.empty()} if the result is unknown (does not belong to a verifiable special case); {@code Optional.of(result)} otherwise.
     */
    @Override
    public Optional<Boolean> verify(ASTPetrinet petrinet) {
        return Optional.of(Subclass.belongsToClass(getSubclass(), petrinet));
    }
}
