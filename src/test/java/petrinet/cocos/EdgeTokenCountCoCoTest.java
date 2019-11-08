package petrinet.cocos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

class EdgeTokenCountCoCoTest extends AbstractCoCoTest {
    @Test
    void checkInvalid() throws IOException {
        super.checkInvalid(invalid_path + "ZeroEdgeWeight.pn",
                EdgeTokenCountCoCo.error_code);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExampleNet.pn", "H2O.pn"})
    void checkValid(String pn_name) throws IOException {
        super.checkValid(valid_path + pn_name);
    }
}
