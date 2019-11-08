package petrinet.cocos;

import petrinet._cocos.PetrinetCoCoChecker;

public class PetrinetCoCos {

    public static PetrinetCoCoChecker getCheckerForAllCoCos() {
        final PetrinetCoCoChecker checker = new PetrinetCoCoChecker();
        checker.addCoCo(new AssertionsUniqueCoCo());
        checker.addCoCo(new NamesUniqueCoCo());
        checker.addCoCo(new TransitionEdgesUniqueCoCo());
        checker.addCoCo(new EdgeTokenCountCoCo());
        checker.addCoCo(new EdgePlaceSymbolsResolveCoCo());
        checker.addCoCo(new TransitionReferenceSymbolsResolveCoCo());
        checker.addCoCo(new AssertionSatisfiedCoCo());

        return checker;
    }
}
