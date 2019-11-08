package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTTransition;
import petrinet._cocos.PetrinetASTTransitionCoCo;

import java.util.HashSet;
import java.util.Set;

/**
 * Check that no place is mentioned in two outgoing or two incoming edges for a single transition
 */
class TransitionEdgesUniqueCoCo implements PetrinetASTTransitionCoCo {

    @VisibleForTesting
    static final String error_code_from = "0xP1004";
    @VisibleForTesting
    static final String error_code_to = "0xP1005";

    @Override
    public void check(ASTTransition transitionNode) {
        Set<String> fromPlaces = new HashSet<>();
        transitionNode.forEachFromEdges(e -> {
            if (!fromPlaces.add(e.getPlace())) {
                Log.error(error_code_from + " The place names must be unique, duplicate " + e.getPlace() +
                        "\nUse \"2 <- " + e.getPlace() + "\" to take multiple tokens", e.get_SourcePositionStart());
            }
        });

        Set<String> toPlaces = new HashSet<>();
        transitionNode.forEachToEdges(e -> {
            if (!toPlaces.add(e.getPlace())) {
                Log.error(error_code_to + " The place names must be unique, duplicate " + e.getPlace() +
                        "\nUse \"2 -> " + e.getPlace() + "\" to produce multiple tokens", e.get_SourcePositionStart());
            }
        });
    }
}
