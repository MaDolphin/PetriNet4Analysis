package petrinet._ast;

import de.monticore.literals.literals._ast.ASTIntLiteral;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ASTPlace extends ASTPlaceTOP {

    public ASTPlace() {
        super();
    }

    public ASTPlace(List<ASTFromEdge> outEdges, List<ASTToEdge> inEdges, String name, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTIntLiteral> initial) {
        super(outEdges, inEdges, name, initial);
    }

    /**
     * The set of all transitions at edges leading into this place
     * @return A {@link Set} of transitions producing tokens for this place
     */
    public Set<ASTTransition> getInTransitions() {
        return streamInEdges().map(ASTEdge::getTransition).collect(Collectors.toSet());
    }

    /**
     * The set of all transitions at edges leading out of this place
     * @return A {@link Set} of transitions taking tokens from this place
     */
    public Set<ASTTransition> getOutTransitions() {
        return streamOutEdges().map(ASTEdge::getTransition).collect(Collectors.toSet());
    }
}
