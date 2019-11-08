package petrinet._ast;

import petrinet._symboltable.PlaceSymbol;

import java.util.Optional;

public abstract class ASTEdge extends ASTEdgeTOP {

    private ASTTransition transition;

    /**
     * A reference to the enclosing transition.
     *
     * @return The enclosing transition object.
     */
    public ASTTransition getTransition() {
        return transition;
    }

    /**
     * Sets the enclosing transition.
     *
     * @param transition The enclosing transition object.
     */
    public void setTransition(ASTTransition transition) {
        this.transition = transition;
    }

    @Override
    public Optional<ASTPlace> getPlaceDefinitionOpt() {
        // Monticore 5.0.2 is broken, fix symbol resolution.
        // Upgrade breaks other stuff, in particular:
        //   equalAttributes tries to access placeSymbol attribute of ASTEdgeTOP on an
        //   object casted to ASTEdge, but placeSymbol is private.
        if (!placeDefinition.isPresent()) {
            if ((place != null) && isPresentEnclosingScope()) {
                Optional<petrinet._symboltable.PlaceSymbol> symbol = enclosingScope.flatMap(scope -> scope.resolve(place, PlaceSymbol.KIND));
                // flatMap instead of get; symbol might be empty.
                placeDefinition = symbol.flatMap(PlaceSymbol::getPlaceNode);
            }
        }
        return placeDefinition;
    }

    /**
     * Checks whether two edges are the same but potentially a from/to pair
     * @param other The other edge to compare
     * @return True if all attributes are the same
     */
    public boolean equalExceptDirection(ASTEdge other) {
        return place.equals(other.place) && count.getValue() == other.count.getValue() && transition.deepEquals(other.transition);
    }

    /**
     * Check whether two edges are the same, with potentially different transitions.
     * @param other The other edge to compare
     * @return True if all attributes except the transition are the same.
     */
    public boolean equalExceptTransition(ASTEdge other) {
        return place.equals(other.place) && count.getValue() == other.count.getValue();
    }

    /**
     * Check whether two edges are the same, with potentially different places.
     * @param other The other edge to compare
     * @return True if all attributes except the place are the same.
     */
    public boolean equalExceptPlace(ASTEdge other) {
        return transition.deepEquals(other.transition) && count.getValue() == other.count.getValue();
    }
}
