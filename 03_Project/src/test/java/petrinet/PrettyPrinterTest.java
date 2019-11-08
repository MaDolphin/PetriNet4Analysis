package petrinet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinet.prettyprint.PetrinetPrettyPrinter;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PrettyPrinterTest {

    private static final String valid_path = "src/test/resources/parser/valid/";

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn", "Fig15.pn", "DocExample.pn"})
    void testPrettyPrint(String path) throws IOException {
        Optional<ASTPetrinet> model = new PetrinetParser().parse(valid_path + path);
        assertTrue(model.isPresent());

        String print = PetrinetPrettyPrinter.print(model.get());
        Optional<ASTPetrinet> parsePretty = new PetrinetParser().parse_String(print);
        assertTrue(parsePretty.isPresent());

        assertTrue(model.get().deepEquals(parsePretty.get()));
    }
}
