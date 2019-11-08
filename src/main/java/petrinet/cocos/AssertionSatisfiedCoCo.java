package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTAssertion;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTRequirement;
import petrinet._cocos.PetrinetASTPetrinetCoCo;

import java.util.Optional;

/**
 * Test whether claimed assertions are indeed true
 * @see ASTRequirement#verify
 */
class AssertionSatisfiedCoCo implements PetrinetASTPetrinetCoCo {

    @VisibleForTesting
    static final String error_code = "0xP1009";
    private static final String warning_code = "0xP2001";

    @Override
    public void check(ASTPetrinet petrinetNode) {
        for (ASTAssertion assertion : petrinetNode.getAssertionList()) {
            Optional<Boolean> checkResult = assertion.getRequirement().verify(petrinetNode);
            if (checkResult.isPresent()) {
                if (checkResult.get()) {
                    Log.info("Assertion \"" + assertion.getRequirement() + "\" successfully verified", getClass().getName());
                } else {
                    Log.error(error_code + " Assertion \"" + assertion.getRequirement() + "\" not correct", assertion.get_SourcePositionStart());
                }
            } else {
                Log.warn(warning_code + " Assertion \"" + assertion.getRequirement() + "\" could not be verified; result indeterminate", assertion.get_SourcePositionStart());
            }
        }
    }
}
