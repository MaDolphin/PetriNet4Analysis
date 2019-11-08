package petrinet.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

class ResolutionTest extends AbstractCoCoTest {

    @ParameterizedTest
    @ValueSource(strings = {"MissingPlace.pn"})
    void checkInvalidPlace(String pn_name) throws IOException {
        super.checkInvalid(invalid_path + pn_name, EdgePlaceSymbolsResolveCoCo.error_code_none);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MissingTransition.pn"})
    void checkInvalidTransition(String pn_name) throws IOException {
        super.checkInvalid(invalid_path + pn_name, TransitionReferenceSymbolsResolveCoCo.error_code_none);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn"})
    void checkValid(String pn_name) throws IOException {
        super.checkValid(valid_path + pn_name);
    }
}
