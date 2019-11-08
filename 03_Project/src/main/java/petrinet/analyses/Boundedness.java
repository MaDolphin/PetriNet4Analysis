package petrinet.analyses;

import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTPetrinet;

import java.util.*;

public class Boundedness {

    /**
     * Test whether the petrinet is bounded. Log the analysis results with a maximum-token example
     * @param petrinet An {@link ASTPetrinet} to check for boundedness
     * @return {@code true} if the petrinet is bounded; otherwise {@code false}
     */
    public static boolean isBounded(ASTPetrinet petrinet) {
        CoverabilityTree tree = petrinet.getCoverabilityTree();
        Map.Entry<CoverabilityTree, TokenCount> result = tree.max();

        if (result.getValue().compareTo(new InfiniteTokenCount()) >= 0) {

            Log.info("[Unbounded] Petrinet is not bounded! Maximum token count " + result.getValue() + " reached by:", Boundedness.class.getName());
            Log.info("[Unbounded] Initial marking " + tree.marking, Boundedness.class.getName());

            List<String> key = result.getKey().path;
            for (String transition : key) {
                CoverabilityTree child = tree.children.get(transition);
                Log.info("[Unbounded] " + tree.marking + " --" + transition + "--> " + child.marking,  Boundedness.class.getName());
                tree = child;
            }

            String place = tree.marking.max().getKey();
            Log.info("[Unbounded] Final marking: " + tree.marking + ", place " + place + " can obtain arbitrarily many tokens",  Boundedness.class.getName());
            return false;

        } else {
            Log.info("[Bounded] Maximum token count achievable is " + result.getValue(), Boundedness.class.getName());
            return true;
        }
    }

    /**
     * Test whether the petrinet is safe. Log the analysis results with a maximum-token example
     * @param petrinet An {@link ASTPetrinet} to check for safety
     * @return {@code true} if the petrinet is safe; otherwise {@code false}
     */
    public static boolean isSafe(ASTPetrinet petrinet) {
        CoverabilityTree tree = petrinet.getCoverabilityTree();
        Map.Entry<CoverabilityTree, TokenCount> result = tree.max();

        if (result.getValue().compareTo(2) >= 0) {

            Log.info("[Unsafe] Petrinet is not safe! Collision (of " + result.getValue() + " tokens) reached by:",  Boundedness.class.getName());
            Log.info("[Unsafe] Initial marking " + tree.marking, Boundedness.class.getName());

            List<String> key = result.getKey().path;
            for (String transition : key) {
                CoverabilityTree child = tree.children.get(transition);
                Log.info("[Unsafe] " + tree.marking + " --" + transition + "--> " + child.marking,  Boundedness.class.getName());
                tree = child;
            }

            String place = tree.marking.max().getKey();
            Log.info("[Unsafe] Final marking: " + tree.marking + ", collision in place " + place,  Boundedness.class.getName());
            return false;

        } else {
            Log.info("[Safe] Maximum token count achievable is " + result.getValue(), Boundedness.class.getName());
            return true;
        }
    }
}
