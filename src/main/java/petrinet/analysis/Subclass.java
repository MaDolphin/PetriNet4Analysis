package petrinet.analysis;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import petrinet._ast.*;

import java.util.Collections;
import java.util.Set;

public class Subclass {

    /**
     * Tests whether the given petrinet is a member of the given class
     * @param subclass An {@link ASTSubclass} for which membership should be decided
     * @param petrinet The {@link ASTPetrinet} root node
     * @return true, if the petrinet is a member of the given class
     */
    public static boolean belongsToClass(ASTSubclass subclass, ASTPetrinet petrinet) {
        switch (subclass) {
            case MARKED_GRAPH:
                return isMarkedGraph(petrinet);
            case STATE_MACHINE:
                return isStateMachine(petrinet);
            case FREE_CHOICE:
                return isFreeChoice(petrinet);
            case EXTENDED_FREE_CHOICE:
                return isExtendedFreeChoice(petrinet);
            case ASYMMETRIC_CHOICE:
                return isAsymmetricChoice(petrinet);
            default:
                assert false;
                return false;
        }
    }

    /**
     * Test whether the given petrinet is ordinary, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is ordinary, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isOrdinary(ASTPetrinet petrinet) {
        return petrinet.streamTransitions()
                .flatMap(ASTTransition::streamAllEdges)
                .map(ASTEdge::getCount)
                .map(ASTNatLiteral::getValue)
                .allMatch(v -> v == 1);
    }

    /**
     * Test whether the given petrinet is a state machine, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is a state machine, {@code false} otherwise
     */
    static boolean isStateMachine(ASTPetrinet petrinet) {
        if (!isOrdinary(petrinet)) {
            return false;
        }

        for (ASTTransition t : petrinet.getTransitionList()) {
            if (t.sizeFromEdges() != 1 || t.sizeToEdges() != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether the given petrinet is a marked graph, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is a marked graph, {@code false} otherwise
     */
    static boolean isMarkedGraph(ASTPetrinet petrinet) {
        if (!isOrdinary(petrinet)) {
            return false;
        }

        petrinet.deriveGraphInfo();
        for (ASTPlace p : petrinet.getPlaceList()) {
            if (p.sizeInEdges() != 1 || p.sizeOutEdges() != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test whether the given petrinet is a free-choice net, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is free-choice, {@code false} otherwise
     */
    static boolean isFreeChoice(ASTPetrinet petrinet) {
        if (!isOrdinary(petrinet)) {
            return false;
        }

        petrinet.deriveGraphInfo();
        for (ASTPlace p : petrinet.getPlaceList()) {
            if (p.sizeOutEdges() > 1) {
                if (p.streamOutEdges()
                        .map(ASTEdge::getTransition)
                        .map(ASTTransition::sizeFromEdges)
                        .anyMatch(l -> l > 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test whether the given petrinet is an extended free-choice net, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is extended-free-choice, {@code false} otherwise
     */
    static boolean isExtendedFreeChoice(ASTPetrinet petrinet) {
        if (!isOrdinary(petrinet)) {
            return false;
        }

        petrinet.deriveGraphInfo();
        for (ASTPlace p1 : petrinet.getPlaceList()) {
            for (ASTPlace p2 : petrinet.getPlaceList()) {
                if (p1 != p2) {
                    Set<ASTTransition> p1out = p1.getOutTransitions();
                    Set<ASTTransition> p2out = p2.getOutTransitions();
                    if (!Collections.disjoint(p1out, p2out) && !p1out.equals(p2out)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Test whether the given petrinet is an asymmetric choice net, according to [Mur89] definitions
     *
     * @param petrinet A petrinet to check
     * @return {@code true} if the petrinet is asymmetric-choice, {@code false} otherwise
     */
    static boolean isAsymmetricChoice(ASTPetrinet petrinet) {
        if (!isOrdinary(petrinet)) {
            return false;
        }

        petrinet.deriveGraphInfo();
        for (ASTPlace p1 : petrinet.getPlaceList()) {
            for (ASTPlace p2 : petrinet.getPlaceList()) {
                if (p1 != p2) {
                    Set<ASTTransition> p1out = p1.getOutTransitions();
                    Set<ASTTransition> p2out = p2.getOutTransitions();
                    if (!Collections.disjoint(p1out, p2out) && !p1out.containsAll(p2out) && !p2out.containsAll(p1out)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
