package petrinet._symboltable;

public class PetrinetLanguage extends PetrinetLanguageTOP {

    public PetrinetLanguage() {
        super("Petrinet Language", "pn");
    }

    @Override
    protected PetrinetModelLoader provideModelLoader() {
        return new PetrinetModelLoader(this);
    }
}
