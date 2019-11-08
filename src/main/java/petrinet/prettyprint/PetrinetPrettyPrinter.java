package petrinet.prettyprint;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import petrinet._ast.*;
import petrinet._visitor.PetrinetInheritanceVisitor;

import java.util.stream.Collectors;

public class PetrinetPrettyPrinter extends IndentPrinter implements PetrinetInheritanceVisitor {

    private PetrinetPrettyPrinter() {
        super();
        setIndentLength(4);
    }

    /**
     * Pretty-print the given petrinet
     * @param petrinetNode The root of the AST
     * @return A textual representation of the AST that parses to the same object, sorted and in a readable format
     */
    public static String print(ASTPetrinet petrinetNode) {
        PetrinetPrettyPrinter printer = new PetrinetPrettyPrinter();
        petrinetNode.accept(printer);
        return printer.getContent();
    }

    @Override
    public void visit(ASTPetrinetNode node) {
        CommentPrettyPrinter.printPreComments(node, this);
    }

    @Override
    public void endVisit(ASTPetrinetNode node) {
        CommentPrettyPrinter.printPostComments(node, this);
    }

    @Override
    public void visit(ASTPetrinet petrinetNode) {
        print("petrinet ");
        print(petrinetNode.getName());
        println(" {");
        println();
        indent();
    }

    @Override
    public void endVisit(ASTPetrinet petrinetNode) {
        unindent();
        println("}");
    }

    @Override
    public void visit(ASTAssertion assertionNode) {
        print("assert ");
    }

    @Override
    public void endVisit(ASTAssertion assertionNode) {
        println(";");
        println();
    }

    @Override
    public void visit(ASTGlobalRequirement globalRequirementNode) {
        print(globalRequirementNode.getGlobalFeature().toString().toLowerCase());
    }

    @Override
    public void visit(ASTSubclassRequirement subclassRequirementNode) {
        print(subclassRequirementNode.getSubclass().toString().toLowerCase());
    }

    @Override
    public void handle(ASTLiveness livenessNode) {
        print(livenessNode.getLivenessLevel().toString().toLowerCase());
        print("(");
        if (livenessNode.isEmptyTransitionReferences()) {
            print("*");
        } else {
            print(livenessNode.streamTransitionReferences().map(ASTTransitionReference::getTransition).collect(Collectors.joining(", ")));
        }
        print(")");
    }

    @Override
    public void visit(ASTPlace placeNode) {
        print("place ");
        print(placeNode.getName());
        if (placeNode.isPresentInitial()) {
            print(" initial ");
        }
    }

    @Override
    public void traverse(ASTPlace placeNode) {
        if (placeNode.isPresentInitial()) {
            placeNode.getInitial().accept(getRealThis());
        }
    }

    @Override
    public void endVisit(ASTPlace placeNode) {
        println(";");
    }

    @Override
    public void visit(ASTTransition transitionNode) {
        println();
        print("transition ");
        print(transitionNode.getName());
        println(":");
        indent();
    }

    @Override
    public void endVisit(ASTTransition transitionNode) {
        unindent();
    }

    @Override
    public void endVisit(ASTFromEdge fromEdgeNode) {
        print(" <- ");
        println(fromEdgeNode.getPlace());
    }

    @Override
    public void endVisit(ASTToEdge toEdgeNode) {
        print(" -> ");
        println(toEdgeNode.getPlace());
    }

    @Override
    public void visit(ASTIntLiteral intLiteralNode) {
        print(intLiteralNode.getSource());
    }
}
