package petrinet.analyses;

import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTTransition;

import java.util.Optional;

public class Liveness {

    /**
     * Determine whether the given transition is l1-live.
     * @param tree The coverability tree of a petrinet.
     * @param transition A transition in the petrinet
     * @return {@code true} if there is a firing sequence from the current marking that contains {@code transition},
     * i.e. {@code transition} is l1-live. {@code false} otherwise, if {@code transition} is dead.
     */
    public static boolean isL1Live(CoverabilityTree tree, ASTTransition transition) {
        Optional<CoverabilityTree> result = tree.findTransition(transition.getName());
        if (result.isPresent()) {
            Log.info("[L1-Live] Transition " + transition.getName() + " can be fired by:", Liveness.class.getName());
            Log.info("[L1-Live] Initial marking " + tree.marking, Liveness.class.getName());

            for (String tr : result.get().path) {
                CoverabilityTree child = tree.children.get(tr);
                Log.info("[L1-Live] " + tree.marking + " --" + tr + "--> " + child.marking,  Liveness.class.getName());
                tree = child;
            }

            Log.info("[L1-Live] " + tree.marking + " --" + transition.getName() + "--> [...]", Liveness.class.getName());
            return true;
        } else {
            Log.info("[L1-Dead] Transition " + transition.getName() + " cannot be fired", Liveness.class.getName());
            return false;
        }
    }
}
