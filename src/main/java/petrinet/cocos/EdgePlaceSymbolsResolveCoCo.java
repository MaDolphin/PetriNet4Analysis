package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTEdge;
import petrinet._cocos.PetrinetASTEdgeCoCo;

/**
 * Test that all places mentioned in edges have been successfully resolved
 */
class EdgePlaceSymbolsResolveCoCo implements PetrinetASTEdgeCoCo {

    @VisibleForTesting
    static final String error_code_none = "0xP1007";
    private static final String error_code_several = "0xP1008";

    @Override
    public void check(ASTEdge edgeNode) {
        try {
            if (!edgeNode.isPresentPlaceDefinition()) {
                Log.error(error_code_none + " Place \"" + edgeNode.getPlace() + "\" is not defined in this petrinet",
                        edgeNode.get_SourcePositionStart());
            }
        } catch (ResolvedSeveralEntriesException ex) {
            // should be caught by NamesUniqueCoCo beforehand, but in tests we don't fail early
            Log.error(error_code_several + " Place \"" + edgeNode.getPlace() + "\" is ambiguous due to duplicate definition",
                    edgeNode.get_SourcePositionStart());
        }
    }
}
