package petrinet.analysis;

import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTTransition;

import java.util.*;
import java.util.function.Predicate;

public class CoverabilityTree {

    /**
     * The {@link Marking} of the current coverability tree node. It contains a mapping of each place name to a token count.
     */
    final Marking marking;

    /**
     * The tree node from which the current marking was derived. If {@code null}, this is the initial marking (root node)
     */
    final CoverabilityTree parent;

    /**
     * The children of this tree node. Each edge is labeled with the name of the fired transition to get to the child's
     * marking. <br>
     * {@code children.keys()} is the set of enabled transitions at {@link #marking}
     */
    final Map<String, CoverabilityTree> children;

    /**
     * The path of transitions used to reach this node's marking from the root, i.e. initial marking
     */
    final List<String> path;

    /**
     * Produce a new, complete coverability tree for the given petrinet
     *
     * @param petrinet An {@link ASTPetrinet} for which to compute the coverability tree
     */
    public CoverabilityTree(ASTPetrinet petrinet) {
        // STEP 1 - Label the initial marking as the root (parent = null).
        this(petrinet, new Marking(petrinet), null, new ArrayList<>());
    }

    /**
     * Compute a coverability (sub-)tree as described in [Mur89, V.A] with the node currently constructed and all its children.
     * The list of "new" markings is maintained in the call stack.
     *
     * @param petrinet The petrinet AST node.
     * @param marking  The {@link Marking} to start the tree with.
     * @param parent   The already produced parent of this node in the tree, to detect repetitions
     * @param path     The path of transitions needed to get to this node from the initial marking
     */
    private CoverabilityTree(ASTPetrinet petrinet, Marking marking, CoverabilityTree parent, List<String> path) {
        this.marking = marking;
        this.parent = parent;
        this.children = new HashMap<>();
        this.path = Collections.unmodifiableList(path);

        if (parent != null && parent.findInPath(marking::equals).isPresent()) {
            // STEP 2.2 - If there is an equal marking in the tree already, stop here.
            return;
        }

        for (ASTTransition transition : petrinet.getTransitionList()) {
            if (!marking.enabled(transition)) {
                // STEP 2.4 - Only consider transitions that are enabled at this marking
                continue;
            }

            // STEP 2.4.1 - Obtain the marking that results from firing the transition
            Marking copy = marking.copy();
            copy.fire(transition);

            // STEP 2.4.2 - Check whether there is a "coverable" marking among the parents of this node,
            // i.e. the current marking has at least the same number of tokens in each place
            Optional<Marking> subset = findInPath(m ->
                    copy.keys().stream().allMatch(p -> copy.get(p).compareTo(m.get(p)) >= 0)
            );

            // If there is a coverable marking, replace all token counts that have strictly increased with ω ("arbitrarily many")
            if (subset.isPresent()) {
                for (String place : copy.keys()) {
                    if (copy.get(place).compareTo(subset.get().get(place)) > 0) {
                        copy.set(place, new InfiniteTokenCount());
                    }
                }
            }

            // STEP 2.4.3 - Introduce the new marking as a new node in the tree, with the transition name
            // as the arc label. Here: Recursively compute the children of this new node.
            children.put(transition.getName(), new CoverabilityTree(petrinet, copy, this, new ArrayList<String>(path) {{
                add(transition.getName());
            }}));
        }
    }

    /**
     * Tries to find a {@link Marking} for which the given predicate applies in the current node or any of its predecessors.
     *
     * @param predicate A {@link Predicate} that should be satisfied by the desired marking
     * @return The first {@link Marking}, from leaf to root, in the sequence of parents for which the given predicate applies,
     * or {@code Optional.empty()} if there is no such marking.
     */
    private Optional<Marking> findInPath(Predicate<Marking> predicate) {
        if (predicate.test(marking)) {
            return Optional.of(marking);
        }
        return parent == null ? Optional.empty() : parent.findInPath(predicate);
    }

    /**
     * Computes the child that contains the maximal token count.
     *
     * @return A {@link Map.Entry} with a {@link TokenCount} as {@link Map.Entry#getValue() value}, which represents the
     * maximal number of tokens in a single place that can be reached from the current {@link #marking} through any
     * transition sequence. <br>
     * The {@link Map.Entry#getKey() key} is the sequence of transitions needed to reach that marking.
     * @see Marking#max()
     */
    Map.Entry<CoverabilityTree, TokenCount> max() {
        TokenCount currentMax = marking.max().getValue();

        Optional<Map.Entry<CoverabilityTree, TokenCount>> childMax = children.values().stream()
                .map(CoverabilityTree::max)
                .max(Comparator.comparing(Map.Entry::getValue));

        if (childMax.isPresent() && childMax.get().getValue().compareTo(currentMax) > 0) {
            return childMax.get();
        } else {
            return new AbstractMap.SimpleEntry<>(this, currentMax);
        }
    }

    Optional<CoverabilityTree> findTransition(String transition) {
        if (children.containsKey(transition)) {
            return Optional.of(this);
        }

        for (CoverabilityTree child : children.values()) {
            Optional<CoverabilityTree> result = child.findTransition(transition);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
