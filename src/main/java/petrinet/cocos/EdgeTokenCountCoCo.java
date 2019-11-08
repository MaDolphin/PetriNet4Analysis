package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTEdge;
import petrinet._cocos.PetrinetASTEdgeCoCo;

/**
 * Test that no edge adds/removes 0 tokens, but positive numbers
 */
class EdgeTokenCountCoCo implements PetrinetASTEdgeCoCo {

    @VisibleForTesting
    static final String error_code = "0xP1006";

    @Override
    public void check(ASTEdge edgeNode) {
        if (edgeNode.getCount().getValue() == 0) {
            Log.error(error_code + " Tokens moved by an edge must be positive, is " +
                    edgeNode.getCount().getValue(), edgeNode.getCount().get_SourcePositionStart());
        }
    }
}
