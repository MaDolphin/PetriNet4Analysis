package petrinet._ast;

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import petrinet.analysis.Liveness;

import java.util.List;
import java.util.Optional;

public class ASTLiveness extends ASTLivenessTOP {

    public ASTLiveness() {
        super();
    }

    public ASTLiveness(ASTLivenessLevel livenessLevel,
                       List<ASTTransitionReference> transitions) {
        super(livenessLevel, transitions);
    }

    /**
     * Check whether this requirement is satisfied by the given petrinet. Verify that all transitions referenced in
     * the liveness statement are indeed live, according to the specified level.
     * @param petrinet A petrinet for which the requirement should hold
     * @return {@code Optional.empty()} if the result is unknown (does not belong to a verifiable special case); {@code Optional.of(result)} otherwise.
     */
    @Override
    public Optional<Boolean> verify(ASTPetrinet petrinet) {
        for (ASTTransitionReference ref : transitionReferences) {
            if (!ref.isPresentTransitionDefinition()) {
                Log.error("[Liveness] Transition " + ref.getTransition() + " not found; could not verify liveness",
                        ref.get_SourcePositionStart());
                return Optional.of(false);
            }
            try {
                if (!verifyLiveness(petrinet, ref.getTransitionDefinition())) {
                    return Optional.of(false);
                }
            } catch (ResolvedSeveralEntriesException ex) {
                Log.error("[Liveness] Transition " + ref.getTransition() + " is ambiguous; could not verify liveness",
                        ref.get_SourcePositionStart());
            }
        }

        return Optional.of(true);
    }

    /**
     * Checks whether the given {@link #livenessLevel} holds for a specific transition
     * @param ast The petrinet containing the transition, where liveness should be verified
     * @param transition The transition whose liveness should be determined
     * @return {@code true} if the transition is live according to the {@link #livenessLevel}, and {@code false} otherwise
     */
    private boolean verifyLiveness(ASTPetrinet ast, ASTTransition transition) {
        switch (livenessLevel) {
            case L0LIVE:
                return !Liveness.isL1Live(ast.getCoverabilityTree(), transition);
            case L1LIVE:
                return Liveness.isL1Live(ast.getCoverabilityTree(), transition);
            default:
                throw new IllegalArgumentException("Liveness level does not exist");
        }
    }
}
