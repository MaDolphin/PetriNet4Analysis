package petrinet.cocos;

import de.monticore.io.paths.ModelPath;
//import de.monticore.symboltable.GlobalScope;
//import de.monticore.symboltable.ResolvingConfiguration;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import petrinet._ast.ASTPetrinet;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._parser.PetrinetParser;
import petrinet._symboltable.PetrinetGlobalScope;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;
import petrinet._symboltable.PetrinetSymbolTableCreatorDelegator;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractCoCoTest {

    static final String valid_path = "src/test/resources/cocos/valid/";
    static final String invalid_path = "src/test/resources/cocos/invalid/";

    private static PetrinetLanguage language;
//    private static ResolvingConfiguration resolve;
    private static PetrinetGlobalScope globalScope;

    @BeforeAll
    protected static void setGlobalScope() {
        language = new PetrinetLanguage();
//        resolve = new ResolvingConfiguration();
//        resolve.addDefaultFilters(language.getResolvingFilters());
        globalScope = new PetrinetGlobalScope(new ModelPath(), language);
    }

    @BeforeAll
    static void initLog() {
        LogStub.init();
    }

    @AfterEach
    void clearLog(){
        Log.getFindings().clear();
    }

    private void parseAndRunCoCos(String pn_path) throws IOException {
        Optional<ASTPetrinet> petrinet = new PetrinetParser().parse(pn_path);
        assertTrue(petrinet.isPresent());

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        PetrinetSymbolTableCreatorDelegator creator = language.getSymbolTableCreator(globalScope);
        creator.createFromAST(petrinet.get());

        PetrinetCoCoChecker checker = PetrinetCoCos.getCheckerForAllCoCos();
        checker.checkAll(petrinet.get());
    }

    void checkInvalid(String pn_path, String expected_error_code) throws IOException {
        parseAndRunCoCos(pn_path);
        assertNotEquals(0, Log.getFindings().size(),
                "The invalid model " + pn_path + " did not produce any error: " +
                        Log.getFindings().toString());
        assertTrue(Log.getFindings().stream().anyMatch(f -> f.getMsg().split("\\s")[0].equals(expected_error_code)));
    }

    void checkValid(String pn_path) throws IOException {
        parseAndRunCoCos(pn_path);
        assertTrue(Log.getFindings().isEmpty(),
                "The model "+ pn_path + " produced the following errors:" + Log.getFindings().toString());
    }
}