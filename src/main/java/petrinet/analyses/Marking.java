package petrinet.analyses;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

import java.util.*;

/**
 * A marking of the places in a petrinet with numbers of tokens.
 */
class Marking {

    private final Map<String, TokenCount> marking = new HashMap<>();

    private Marking() {
    }

    /**
     * Uses the initial marking to populate token counts
     * @param petrinet A petrinet with an initial marking of places
     */
    Marking(ASTPetrinet petrinet) {
        for (ASTPlace place : petrinet.getPlaceList()) {
            set(place.getName(), new TokenCount(place.getInitialOpt().map(ASTIntLiteral::getValue).orElse(0)));
        }
    }

    /**
     * Update the number of tokens
     * @param place The place name of the petrinet for which to change the number of tokens
     * @param value The new number of tokens
     */
    void set(String place, TokenCount value) {
        marking.put(place, value);
    }

    /**
     * Obtain the current number of tokens
     * @param place The place name of the petrinet for which to retrieve the number of tokens
     * @return The number of tokens in this place
     */
    TokenCount get(String place) {
        return marking.get(place);
    }

    /**
     * Retrieve all places for which a number of tokens has been stored
     * @return A {@link Set} containing the names of stored places
     */
    Set<String> keys() {
        return marking.keySet();
    }

    /**
     * Find the place with the highest number of tokens
     * @return A {@link Map.Entry} whose {@link Map.Entry#getKey() key} is the name of the place with most tokens,
     * and whose {@link Map.Entry#getValue() value} is the number of tokens there.
     * @see TokenCount#compareTo(TokenCount)
     */
    Map.Entry<String, TokenCount> max() {
        if (marking.isEmpty()) {
            return new AbstractMap.SimpleEntry<>("", new TokenCount(0));
        }
        return marking.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();
    }

    /**
     * Produce a new marking with exactly the same values that can be modified independently
     * @return A new marking with the same values
     */
    Marking copy() {
        Marking other = new Marking();
        keys().forEach(place -> other.set(place, get(place).copy()));
        return other;
    }

    /**
     * Test whether a given {@link ASTTransition} is enabled in the current marking
     * @param transition An {@link ASTTransition} in the petrinet
     * @return {@code true} if the transition can be fired, and {@code false} otherwise
     * @see TokenCount#compareTo(int)
     */
    boolean enabled(ASTTransition transition) {
        return transition.getFromEdgeList().stream().allMatch(
                edge -> get(edge.getPlace()).compareTo(edge.getCount().getValue()) >= 0
        );
    }

    /**
     * Fire the given transition, updating all token counts in place
     * @param transition A transition in the petrinet, whose edges determine removed and added tokens
     */
    void fire(ASTTransition transition) {
        transition.forEachFromEdges(
                edge -> get(edge.getPlace()).subtract(edge.getCount().getValue())
        );
        transition.forEachToEdges(
                edge -> get(edge.getPlace()).add(edge.getCount().getValue())
        );
    }

    /**
     * Test whether the current marking and another marking have the same number of tokens everywhere
     * @param other Another marking to compare to
     * @return {@code true} if both markings match everywhere, and {@code false} if they are different in at least
     * one place.
     */
    boolean equals(Marking other) {
        if (marking.size() != other.marking.size()) {
            return false;
        }

        for (String place : keys()) {
            if (!other.marking.containsKey(place) || !get(place).equals(other.get(place))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(");
        for (String place : new TreeSet<>(keys())) {
            result.append(place);
            result.append(":");
            result.append(get(place));
            result.append(" ");
        }
        if (keys().size() != 0) {
            result.replace(result.length() - 1, result.length(), ")");
        } else {
            result.append(")");
        }
        return result.toString();
    }
}
