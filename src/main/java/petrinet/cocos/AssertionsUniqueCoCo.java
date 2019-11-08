package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTAssertion;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTRequirement;
import petrinet._cocos.PetrinetASTPetrinetCoCo;

import java.util.HashSet;
import java.util.Set;

/**
 * Check that no {@link ASTAssertion} appears repeatedly
 */
class AssertionsUniqueCoCo implements PetrinetASTPetrinetCoCo {

    @VisibleForTesting
    static final String error_code = "0xP1001";

    @Override
    public void check(ASTPetrinet petrinetNode) {
        Set<ASTRequirement> assertions = new HashSet<>();
        for (ASTAssertion a : petrinetNode.getAssertionList()) {
            if (assertions.stream().anyMatch(r -> r.deepEquals(a.getRequirement()))) {
                Log.error(error_code + " The petrinet assertions must be unique, duplicate " +
                        a.getRequirement().toString(), a.get_SourcePositionStart());
            }
            assertions.add(a.getRequirement());
        }
    }
}
