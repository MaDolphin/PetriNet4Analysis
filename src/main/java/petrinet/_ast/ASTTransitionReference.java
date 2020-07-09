package petrinet._ast;

import petrinet._symboltable.TransitionSymbol;

import java.util.Optional;

public class ASTTransitionReference extends ASTTransitionReferenceTOP {

    public ASTTransitionReference() {
        super();
    }

//    public ASTTransitionReference(String transition) {
//        super(transition);
//    }

//    @Override
//    public Optional<ASTTransition> getTransitionDefinitionOpt() {
//        // Monticore is broken, fix symbol resolution. See ASTEdge comment.
//        if (!transitionDefinition.isPresent()) {
//            if ((transition != null) && isPresentEnclosingScope()) {
//                Optional<petrinet._symboltable.TransitionSymbol> symbol = enclosingScope.flatMap(scope -> scope.resolve(transition, TransitionSymbol.KIND));
//                // flatMap instead of get; symbol might be empty.
//                transitionDefinition = symbol.flatMap(TransitionSymbol::getTransitionNode);
//            }
//        }
//        return transitionDefinition;
//    }
}
