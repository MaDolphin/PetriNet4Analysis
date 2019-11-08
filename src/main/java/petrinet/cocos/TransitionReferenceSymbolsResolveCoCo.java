package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTTransitionReference;
import petrinet._cocos.PetrinetASTTransitionReferenceCoCo;

/**
 * Check that all references to transition symbols (e.g. in assertions) have been resolved successfully
 */
class TransitionReferenceSymbolsResolveCoCo implements PetrinetASTTransitionReferenceCoCo {

    @VisibleForTesting
    static final String error_code_none = "0xP1010";
    private static final String error_code_several = "0xP1011";

    @Override
    public void check(ASTTransitionReference transitionReference) {
        try {
            if (!transitionReference.isPresentTransitionDefinition()) {
                Log.error(error_code_none + " Transition \"" + transitionReference.getTransition() + "\" not defined in this petrinet",
                        transitionReference.get_SourcePositionStart());
            }
        } catch (ResolvedSeveralEntriesException ex) {
            // should be caught by NamesUniqueCoCo beforehand, but in tests we don't fail early
            Log.error(error_code_several + " Transition \"" + transitionReference.getTransition() + "\" is ambiguous due to duplicate definition",
                    transitionReference.get_SourcePositionStart());
        }
    }
}
