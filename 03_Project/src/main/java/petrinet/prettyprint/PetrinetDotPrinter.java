package petrinet.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import petrinet._ast.*;
import petrinet._visitor.PetrinetVisitor;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PetrinetDotPrinter extends IndentPrinter implements PetrinetVisitor {

    private PetrinetDotPrinter() {
        super();
        setIndentLength(4);
    }

    /**
     * Produce a dot-file that represents a graph for this petrinet. This file can then be converted to an image, e.g.
     * using <a href="https://www.graphviz.org/">graphviz</a> and the command <br>
     * {@code <out.dot dot -Tpng >img.png}
     * @param petrinetNode The root of the AST
     * @return A textual representation of the graph in dot format
     */
    public static String print(ASTPetrinet petrinetNode) {
        petrinetNode.deriveGraphInfo();
        PetrinetDotPrinter printer = new PetrinetDotPrinter();
        petrinetNode.accept(printer);
        return printer.getContent();
    }

    @Override
    public void visit(ASTPetrinet petrinetNode) {
        print("strict digraph ");
        print(petrinetNode.getName());
        println("{");
        println();
        indent();
    }

    @Override
    public void endVisit(ASTPetrinet petrinetNode) {
        unindent();
        println("}");
    }

    @Override
    public void visit(ASTPlace placeNode) {
        print(placeNode.getName());
        print(" [label=\"");
        print(placeNode.getName());
        if (placeNode.isPresentInitial() && placeNode.getInitial().getValue() != 0) {
            print("\\n");
            print(IntStream.range(0, placeNode.getInitial().getValue()).mapToObj(x -> "•").collect(Collectors.joining("")));
        }
        println("\"];");
    }

    @Override
    public void visit(ASTTransition transitionNode) {
        print(transitionNode.getName());
        print(" [shape=box,label=\"");
        print(transitionNode.getName());
        println("\"];");
    }

    @Override
    public void visit(ASTFromEdge fromEdgeNode) {
        print(fromEdgeNode.getPlace());
        print(" -> ");
        print(fromEdgeNode.getTransition().getName());
        if (fromEdgeNode.getCount().getValue() != 1) {
            print(" [label=\"");
            print(fromEdgeNode.getCount().getValue());
            print("\"]");
        }
        println(";");
    }

    @Override
    public void visit(ASTToEdge toEdgeNode) {
        print(toEdgeNode.getTransition().getName());
        print(" -> ");
        print(toEdgeNode.getPlace());
        if (toEdgeNode.getCount().getValue() != 1) {
            print(" [label=\"");
            print(toEdgeNode.getCount().getValue());
            print("\"]");
        }
        println(";");
    }
}
