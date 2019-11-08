package petrinet.cocos;

import com.google.common.annotations.VisibleForTesting;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTPetrinet;
import petrinet._cocos.PetrinetASTPetrinetCoCo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Check that no place or transition name appears twice
 */
class NamesUniqueCoCo implements PetrinetASTPetrinetCoCo {

    @VisibleForTesting
    static final String error_code = "0xP1002";

    @Override
    public void check(ASTPetrinet petrinetNode) {
        Set<String> names = new HashSet<>();
        Stream.concat(
                petrinetNode
                        .streamTransitions()
                        .map(t -> new NamedSourceElement(t.getName(), t.get_SourcePositionStart())),
                petrinetNode
                        .streamPlaces()
                        .map(p -> new NamedSourceElement(p.getName(), p.get_SourcePositionStart()))
        ).forEach(nse -> {
            if (!names.add(nse.name)) {
                Log.error(error_code + " Names must be unique (across places and transitions), duplicate " + nse.name,
                        nse.source);
            }
        });
    }

    private static class NamedSourceElement {
        private final String name;
        private final SourcePosition source;

        private NamedSourceElement(String name, SourcePosition source) {
            this.name = name;
            this.source = source;
        }
    }
}