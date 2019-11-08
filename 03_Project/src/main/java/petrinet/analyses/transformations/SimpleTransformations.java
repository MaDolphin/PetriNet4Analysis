package petrinet.analyses.transformations;

import de.monticore.io.paths.ModelPath;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import petrinet._ast.*;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;

import java.util.Collections;
import java.util.Iterator;

class SimpleTransformations {

    private static final PetrinetLanguage language;
    private static final ResolvingConfiguration resolve;
    private static final GlobalScope globalScope;

    static {
        language = new PetrinetLanguage();
        resolve = new ResolvingConfiguration();
        resolve.addDefaultFilters(language.getResolvingFilters());
        globalScope = new GlobalScope(new ModelPath(), language, resolve);
    }

    /**
     * Deep clones the given {@link ASTPetrinet}, creates the symbol table and checks context conditions
     *
     * @param petrinet The {@link ASTPetrinet} to copy.
     * @return A copy of the given {@link ASTPetrinet}.
     */
    private static ASTPetrinet copyPetrinet(ASTPetrinet petrinet) {
        ASTPetrinet copy = petrinet.deepClone();
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        PetrinetSymbolTableCreator creator = language.getSymbolTableCreator(resolve, globalScope).get();
        creator.createFromAST(copy);
        copy.deriveGraphInfo();
        return copy;
    }

    /**
     * Adds the initial token count of both given {@link ASTPlace ASTPlaces}.
     *
     * @param toPlace   The {@link ASTPlace} to update.
     * @param fromPlace The other {@link ASTPlace}.
     */
    private static void mergePlaces(ASTPlace toPlace, ASTPlace fromPlace) {
        if (fromPlace.isPresentInitial() && !toPlace.isPresentInitial()) {
            toPlace.setInitial(fromPlace.getInitial().deepClone());
        } else if (fromPlace.isPresentInitial() && toPlace.isPresentInitial()) {
            toPlace.getInitial().setSource(String.valueOf(
                    toPlace.getInitial().getValue() + fromPlace.getInitial().getValue()));
        }
    }

    /**
     * Removes series of places in a given {@link ASTPetrinet}. Series of places are places that are connected by
     * exactly one {@link ASTTransition} with exactly one {@link petrinet._ast.ASTFromEdge} and
     * {@link petrinet._ast.ASTToEdge}.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without series of places.
     */
    static ASTPetrinet fusionOfSeriesPlaces(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTTransition> iterator = transformationNet.iteratorTransitions(); iterator.hasNext(); ) {
            ASTTransition t = iterator.next();
            if (t.sizeFromEdges() == 1
                    && t.sizeToEdges() == 1
                    && t.getFromEdge(0).getCount().getValue() == 1
                    && t.getToEdge(0).getCount().getValue() == 1) {
                ASTPlace fromPlace = t.getFromEdge(0).getPlaceDefinition();
                ASTPlace toPlace = t.getToEdge(0).getPlaceDefinition();
                if (fromPlace.sizeOutEdges() == 1 && Collections.disjoint(fromPlace.getInTransitions(), toPlace.getInTransitions())) {
                    mergePlaces(toPlace, fromPlace);

                    // Bend transitions from the from-place to the to-place
                    for (ASTToEdge toEdge : fromPlace.getInEdgeList()) {
                        toEdge.setPlace(toPlace.getName());
                        toPlace.addInEdge(toEdge);
                    }

                    transformationNet.removePlace(fromPlace);
                    iterator.remove();
                    transformationNet.setDirty();
                }
            }
        }

        return transformationNet;
    }

    /**
     * Removes series of transitions in a given {@link ASTPetrinet}. Series of transitions are transitions that are
     * connected by exactly one {@link ASTPlace} with exactly one ingoing {@link ASTTransition} and one outgoing
     * {@link ASTTransition}.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without series of transitions.
     */
    static ASTPetrinet fusionOfSeriesTransitions(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTPlace> iterator = transformationNet.iteratorPlaces(); iterator.hasNext(); ) {
            ASTPlace p = iterator.next();

            // would change semantics
            if (p.isPresentInitial() && p.getInitial().getValue() > 0) {
                continue;
            }

            if (p.sizeInEdges() == 1
                    && p.sizeOutEdges() == 1
                    && p.getInEdge(0).getCount().getValue() == 1
                    && p.getOutEdge(0).getCount().getValue() == 1) {
                ASTTransition inTransition = p.getInEdge(0).getTransition();
                ASTTransition outTransition = p.getOutEdge(0).getTransition();
                if (outTransition.sizeFromEdges() == 1 && Collections.disjoint(inTransition.getToPlaces(), outTransition.getToPlaces())) {

                    // special case: |→O→| violates boundedness after FST
                    if (inTransition.sizeFromEdges() == 0) {
                        continue;
                    }

                    // Transform out-edges from the second transition to out-edges of the first transition
                    outTransition.forEachToEdges(e -> e.setTransition(inTransition));
                    inTransition.addAllToEdges(outTransition.getToEdgeList());

                    ASTToEdge inEdge = p.getInEdge(0);
                    inTransition.removeToEdge(inEdge);

                    transformationNet.removeTransition(outTransition);
                    iterator.remove();

                    transformationNet.setDirty();
                }
            }
        }

        return transformationNet;
    }

    /**
     * Removes one transition of parallel transitions in a given {@link ASTPetrinet}. Two parallel transitions are
     * {@link ASTTransition}s that both have exactly one incoming {@link ASTFromEdge} and one outgoing {@link ASTToEdge},
     * as well as the same {@link ASTPlace}s in front of and behind them.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without parallel transitions.
     */
    static ASTPetrinet fusionOfParallelTransitions(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTTransition> iterator = transformationNet.iteratorTransitions(); iterator.hasNext(); ) {
            ASTTransition t1 = iterator.next();
            for (ASTTransition t2 : transformationNet.getTransitionList()) {
                if (t1 != t2
                        && t1.sizeFromEdges() == t2.sizeFromEdges()
                        && t1.sizeToEdges() == t2.sizeToEdges() && t1.streamFromEdges().allMatch(from -> t2.streamFromEdges().anyMatch(from::equalExceptTransition))
                        && t1.streamToEdges().allMatch(to -> t2.streamToEdges().anyMatch(to::equalExceptTransition))) {
                    t1.forEachFromEdges(e -> e.getPlaceDefinition().removeOutEdge(e));
                    t1.forEachToEdges(e -> e.getPlaceDefinition().removeInEdge(e));
                    iterator.remove();
                    transformationNet.setDirty();
                    break;
                }
            }
        }

        return transformationNet;
    }

    /**
     * Removes one place of parallel places in a given {@link ASTPetrinet}. Two parallel places are
     * {@link ASTPlace}s that both have exactly one incoming {@link ASTToEdge} and one outgoing {@link ASTFromEdge},
     * as well as the same {@link ASTTransition}s in front of and behind them.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without parallel places.
     */
    static ASTPetrinet fusionOfParallelPlaces(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTPlace> iterator = transformationNet.iteratorPlaces(); iterator.hasNext(); ) {
            ASTPlace p1 = iterator.next();
            for (ASTPlace p2 : transformationNet.getPlaceList()) {
                if (p1 != p2
                        && p1.sizeInEdges() == p2.sizeInEdges()
                        && p1.sizeOutEdges() == p2.sizeOutEdges()
                        && p1.streamInEdges().allMatch(in -> p2.streamInEdges().anyMatch(in::equalExceptPlace))
                        && p1.streamOutEdges().allMatch(out -> p2.streamOutEdges().anyMatch(out::equalExceptPlace))) {
                    if (p1.getInitialOpt().map(ASTIntLiteral::getValue).orElse(0).intValue() ==
                            p2.getInitialOpt().map(ASTIntLiteral::getValue).orElse(0).intValue()) {
                        p1.forEachInEdges(e -> e.getTransition().removeToEdge(e));
                        p1.forEachOutEdges(e -> e.getTransition().removeFromEdge(e));
                        iterator.remove();
                        transformationNet.setDirty();
                        break;
                    }
                }
            }
        }

        return transformationNet;
    }

    /**
     * Removes self-loops of {@link ASTPlace}s in a given {@link ASTPetrinet}. A self-loop of places is an
     * {@link ASTPlace} that has a connection from and to an {@link ASTTransition} and no other connections.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without self-looping places.
     */
    static ASTPetrinet eliminationOfSelfLoopPlaces(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTPlace> iterator = transformationNet.iteratorPlaces(); iterator.hasNext(); ) {
            ASTPlace p = iterator.next();
            if (p.sizeInEdges() == p.sizeOutEdges()
                    && p.streamInEdges().allMatch(in -> p.streamOutEdges().anyMatch(in::equalExceptDirection))
                    && p.streamOutEdges().map(ASTEdge::getCount).mapToInt(ASTIntLiteral::getValue).max().orElse(0)
                        <= p.getInitialOpt().map(ASTIntLiteral::getValue).orElse(0)) {
                // special case: could become safe by removing "unsafe" place
                if (!p.isPresentInitial() || p.getInitial().getValue() <= 1) {
                    /* if the following holds, unsafeness is also preserved: could be used for more detections
                     transformationNet
                        .streamPlaces()
                        .anyMatch(x -> x != p && x.isPresentInitial() && x.getInitial().getValue() > 1)
                     */
                    p.streamInEdges().forEach(e -> e.getTransition().removeToEdge(e));
                    p.streamOutEdges().forEach(e -> e.getTransition().removeFromEdge(e));
                    iterator.remove();
                    transformationNet.setDirty();
                }
            }
        }

        return transformationNet;
    }

    /**
     * Removes self-loops of {@link ASTTransition}s in a given {@link ASTPetrinet}. A self-loop of transitions is an
     * {@link ASTTransition} that has a connection from and to an {@link ASTPlace} and no other connections.
     *
     * @param petrinet The {@link ASTPetrinet} to transform.
     * @return A transformed copy of the given {@link ASTPetrinet} without self-looping transitions.
     */
    static ASTPetrinet eliminationOfSelfLoopTransitions(ASTPetrinet petrinet) {
        ASTPetrinet transformationNet = copyPetrinet(petrinet);

        for (Iterator<ASTTransition> iterator = transformationNet.iteratorTransitions(); iterator.hasNext(); ) {
            ASTTransition t = iterator.next();
            if (t.sizeFromEdges() == t.sizeToEdges()
                    && t.streamFromEdges().allMatch(from -> t.streamToEdges().anyMatch(from::equalExceptDirection))) {
                t.forEachToEdges(e -> e.getPlaceDefinition().removeInEdge(e));
                t.forEachFromEdges(e -> e.getPlaceDefinition().removeOutEdge(e));
                iterator.remove();
                transformationNet.setDirty();
            }
        }

        return transformationNet;
    }

}
