package petrinet._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;

public class PetrinetLanguage extends PetrinetLanguageTOP {

    public PetrinetLanguage() {
        super("Petrinet Language", "pn");
    }

    @Override
    protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
        return new PetrinetModelLoader(this);
    }
}
