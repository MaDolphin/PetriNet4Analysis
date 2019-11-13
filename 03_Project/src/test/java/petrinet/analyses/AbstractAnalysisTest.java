package petrinet.analysis;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import petrinet._ast.ASTPetrinet;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._parser.PetrinetParser;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;
import petrinet.cocos.PetrinetCoCos;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractAnalysisTest {

    private static final String modelPath = "src/test/resources/analyses/";

    private static PetrinetLanguage language;
    private static ResolvingConfiguration resolve;
    private static GlobalScope globalScope;

    @BeforeAll
    protected static void setGlobalScope() {
        language = new PetrinetLanguage();
        resolve = new ResolvingConfiguration();
        resolve.addDefaultFilters(language.getResolvingFilters());
        globalScope = new GlobalScope(new ModelPath(), language, resolve);
    }

    @AfterEach
    void clearLog(){
        Log.getFindings().clear();
    }

    @BeforeAll
    static void disableFailQuick() {
        LogStub.init();
    }

    protected ASTPetrinet parseAndRunCoCos(String path) {
        try {
            Optional<ASTPetrinet> petrinet = new PetrinetParser().parse(modelPath + path);
            assertTrue(petrinet.isPresent());

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            PetrinetSymbolTableCreator creator = language.getSymbolTableCreator(resolve, globalScope).get();
            creator.createFromAST(petrinet.get());

            PetrinetCoCoChecker checker = PetrinetCoCos.getCheckerForAllCoCos();
            checker.checkAll(petrinet.get());

            assertTrue(Log.getFindings().isEmpty());
            return petrinet.get();
        } catch (IOException ex) {
            fail("Should not throw exception (" + path + ")");
        }
        assert false;
        return null;
    }

    protected Set<ASTPetrinet> parseAllAndRunCoCos() {
        Set<ASTPetrinet> result = new HashSet<>();
        File[] files = new File(modelPath).listFiles();
        assertNotNull(files);
        for (File file : files) {
            if (!file.isDirectory()) {
                result.add(parseAndRunCoCos(file.getName()));
            }
        }
        return result;
    }
}
