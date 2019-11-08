package petrinet.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

class AssertionsCoCoTest extends AbstractCoCoTest {

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn", "Fig15.pn"})
    void testValid(String path) throws IOException {
        super.checkValid(valid_path + path);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unbounded1.pn", "Fig17e.pn"})
    void testUnbounded(String path) throws IOException {
        super.checkInvalid(invalid_path + path, AssertionSatisfiedCoCo.error_code);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Unsafe1.pn"})
    void testUnsafe(String path) throws IOException {
        super.checkInvalid(invalid_path + path, AssertionSatisfiedCoCo.error_code);
    }
}
