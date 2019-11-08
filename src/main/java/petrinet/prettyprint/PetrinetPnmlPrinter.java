package petrinet.prettyprint;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import fr.lip6.move.pnml.framework.general.PnmlExport;
import fr.lip6.move.pnml.framework.utils.ModelRepository;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.VoidRepositoryException;
import fr.lip6.move.pnml.ptnet.hlapi.*;
import petrinet._ast.*;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class PetrinetPnmlPrinter {

    private final Map<ASTPetriNode, NodeHLAPI> nodeMap = Maps.newHashMap();

    private final ASTPetrinet petrinet;
    private final Map<ASTPlace, Long> initialMarking;

    public PetrinetPnmlPrinter(final ASTPetrinet petrinet, final Map<ASTPlace, Long> initialMarking) {
        this.petrinet = petrinet;
        this.initialMarking = initialMarking;
    }

    public void export(final Path outputPath) throws InvalidIDException, VoidRepositoryException {
        final PetrinetLanguage lang = new PetrinetLanguage();
        // symboltable must be present to resolve places referenced within transitions
        createSymbolTable(lang, petrinet);

        final String name = petrinet.getName().toLowerCase();

        ModelRepository.getInstance().createDocumentWorkspace("void");

        final PetriNetDocHLAPI doc = new PetriNetDocHLAPI();
        final PetriNetHLAPI net = new PetriNetHLAPI("net",PNTypeHLAPI.COREMODEL, new NameHLAPI(name), doc);

        final PageHLAPI page = new PageHLAPI("page", new NameHLAPI("main"), null, net);

        petrinet.getPlaceList().forEach(p -> addPlace(p, page));

        petrinet.getTransitionList().forEach(t -> {
            addTransition(t, page);
            t.getFromPlaces()
                    .forEach(p -> addArc(p, t, page));
            t.getToPlaces()
                    .forEach(p -> addArc(t, p, page));
        });

        if (initialMarking != null) {
            initialMarking.forEach(this::addInitialMarking);
        }

        final PnmlExport exporter = new PnmlExport();
        try {
            final String fileName = outputPath.resolve(petrinet.getName().toLowerCase() + ".pnml").toString();
            exporter.exportObject(doc, fileName);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        ModelRepository.getInstance().destroyCurrentWorkspace();
    }

    private void addPlace(final ASTPlace place, final PageHLAPI page) {
        try {
            final PlaceHLAPI pPNML = new PlaceHLAPI(place.getName(), page);
            nodeMap.put(place, pPNML);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void addTransition(final ASTTransition transition, final PageHLAPI page) {
        try {
            final TransitionHLAPI tPNML = new TransitionHLAPI(transition.getName(), page);
            nodeMap.put(transition, tPNML);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void addArc(final ASTPetriNode from, final ASTPetriNode to, final PageHLAPI page) {
        try {
            new ArcHLAPI(getArcLabel(from, to), nodeMap.get(from), nodeMap.get(to), page);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void addInitialMarking(final ASTPlace place, final Long count) {
        try {
            new PTMarkingHLAPI(count, (PlaceHLAPI) nodeMap.get(place));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private String getArcLabel(final ASTPetriNode from, final ASTPetriNode to) {
        return Joiner.on("_").join("arc", from.getName(), to.getName());
    }

    private static void createSymbolTable(final PetrinetLanguage lang, final ASTPetrinet ast) {
        final ResolvingConfiguration resolverConfiguration = new ResolvingConfiguration();
        resolverConfiguration.addDefaultFilters(lang.getResolvingFilters());

        final GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, resolverConfiguration);

        final Optional<PetrinetSymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
                resolverConfiguration, globalScope);

        symbolTable.get().createFromAST(ast);
    }

}
