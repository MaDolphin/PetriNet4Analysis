package petrinet;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PetrinetParserTest {

    private static final String valid_path = "src/test/resources/parser/valid/";
    private static final String invalid_path = "src/test/resources/parser/invalid/";

    @BeforeAll
    static void init() {
        Log.enableFailQuick(false);
    }

    @BeforeEach
    void setUp() {
        Log.getFindings().clear();
    }

    @ParameterizedTest
    @ValueSource(strings = {"H2O.pn", "ExampleNet.pn", "DocExample.pn", "Fig15.pn"})
    void checkValid(String modelName) throws IOException {
        Optional<ASTPetrinet> model = new PetrinetParser().parse(valid_path + modelName);
        assertTrue(model.isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "AssertAfterCode.pn",
            "MissingSemicolon.pn",
            "MissingTransitionCount.pn",
            "MissingTransitionName.pn"
    })
    void checkInvalid(String modelName) throws IOException {
        Optional<ASTPetrinet> model = new PetrinetParser().parse(invalid_path + modelName);
        assertFalse(model.isPresent());
    }

}
