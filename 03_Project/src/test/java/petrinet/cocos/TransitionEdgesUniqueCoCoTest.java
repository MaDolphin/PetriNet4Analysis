package petrinet.cocos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

class TransitionEdgesUniqueCoCoTest extends AbstractCoCoTest {
    @Test
    void checkInvalidFromEdges() throws IOException {
        super.checkInvalid(invalid_path + "DuplicateFromEdge.pn",
                TransitionEdgesUniqueCoCo.error_code_from);
    }

    @Test
    void checkInvalidToEdges() throws IOException {
        super.checkInvalid(invalid_path + "DuplicateToEdge.pn",
                TransitionEdgesUniqueCoCo.error_code_to);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn"})
    void checkValid(String pn_name) throws IOException {
        super.checkValid(valid_path + pn_name);
    }
}
