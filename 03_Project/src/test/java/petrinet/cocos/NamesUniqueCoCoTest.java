package petrinet.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

class NamesUniqueCoCoTest extends AbstractCoCoTest {

    @ParameterizedTest
    @ValueSource(strings = {"DuplicatePlace.pn", "DuplicateTransition.pn", "SamePlaceAndTransition.pn"})
    void checkInvalid(String pn_name) throws IOException {
        super.checkInvalid(invalid_path + pn_name, NamesUniqueCoCo.error_code);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn"})
    void checkValid(String pn_name) throws IOException {
        super.checkValid(valid_path + pn_name);
    }
}
