package petrinet;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.slf4j.LoggerFactory;
import petrinet._ast.*;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._parser.PetrinetParser;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;
import petrinet._symboltable.TransitionSymbol;
import petrinet.analyses.Boundedness;
import petrinet.analyses.Liveness;
import petrinet.analyses.Subclass;
import petrinet.analyses.transformations.Transformation;
import petrinet.cocos.PetrinetCoCos;
import petrinet.prettyprint.PetrinetDotPrinter;
import petrinet.prettyprint.PetrinetPrettyPrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

class PetrinetsTool {

    public static void main(String[] args) {
        // First, determine the log level (debug on or off).
        Level logLevel = Level.DEBUG;
        if (Arrays.asList(args).contains("--no-debug")) {
            if (Arrays.asList(args).contains("--debug")) {
                Log.error("--no-debug and --debug in conflict");
            }
            logLevel = Level.INFO;
        } else if (Arrays.asList(args).contains("--pretty")
                || Arrays.asList(args).contains("--dot")) {
            if (!Arrays.asList(args).contains("--debug")) {
                logLevel = Level.INFO;
            }
        }

        ((Logger) LoggerFactory.getLogger(PetrinetsTool.class.getName())).setLevel(logLevel);

        if (args.length < 1) {
            Log.error("Please specify exactly one input model, or --help.");
            return;
        }

        if (args[0].equals("--help")) {
            System.out.println("java -jar petrinets.jar <model> <command>*");
            System.out.println("    <model>: path to petrinet input file (.pn)");
            System.out.println("    <command>: argument, or test to run");
            System.out.println("        arguments:");
            System.out.println("            --debug (default), --no-debug");
            System.out.println("        implemented functions (order is important):");
            System.out.println("            --simplify (should come before others, e.g. --pretty)");
            System.out.println("            --pretty (implies --no-debug)");
            System.out.println("            --dot (implies --no-debug)");
            System.out.println("            --safe, --unsafe");
            System.out.println("            --bounded, --unbounded");
            System.out.println("            --l0-live, --l1-live < * | t_1,...,t_n >");
            System.out.println("            --type");
            System.exit(0);
        }

        ASTPetrinet ast = parse(args[0]);
        if (ast == null) {
            Log.error("0xP0000 Failed to parse petrinet");
            return;
        }

        Log.debug(args[0] + " parsed successfully!", PetrinetsTool.class.getName());

        final PetrinetLanguage lang = new PetrinetLanguage();
        Scope modelTopScope = createSymbolTable(lang, ast);

        PetrinetCoCoChecker checker = PetrinetCoCos.getCheckerForAllCoCos();
        checker.checkAll(ast);
        if (!Log.getFindings().isEmpty()) {
            Log.error("0xP0001 CoCo verification failed, aborting");
        } else {
            Log.debug(args[0] + " cocos verified successfully!", PetrinetsTool.class.getName());
        }

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--debug":
                case "--no-debug":
                    // ignore logging commands
                    break;
                case "--simplify":
                    Log.debug(args[i] + ": [Transformation] Simplifying AST.", PetrinetsTool.class.getName());
                    ast = Transformation.simplify(ast);
                    if (i == args.length - 1) {
                        Log.warn("Transformation is redundant if it is not used; you might want to do --pretty or a test. Arguments are order-sensitive.");
                    }
                    break;
                case "--pretty":
                    Log.debug(args[i] + ": [Prettyprint] Petrinet follows.", PetrinetsTool.class.getName());
                    System.out.println(PetrinetPrettyPrinter.print(ast));
                    break;
                case "--dot":
                    Log.debug(args[i] + ": [Dotprint] Dot follows.", PetrinetsTool.class.getName());
                    System.out.println(PetrinetDotPrinter.print(ast));
                    break;
                case "--safe":
                case "--unsafe":
                    if (Boundedness.isSafe(ast)) {
                        Log.info(args[i] + ": [Safe] Petrinet is safe.", PetrinetsTool.class.getName());
                    } else {
                        Log.info(args[i] + ": [Unsafe] Petrinet is unsafe.", PetrinetsTool.class.getName());
                    }
                    break;
                case "--bounded":
                case "--unbounded":
                    if (Boundedness.isBounded(ast)) {
                        Log.info(args[i] + ": [Bounded] Petrinet is bounded.", PetrinetsTool.class.getName());
                    } else {
                        Log.info(args[i] + ": [Unbounded] Petrinet is unbounded.", PetrinetsTool.class.getName());
                    }
                    break;
                case "--l0-live":
                case "--l1-live":
                    boolean invert = args[i].equals("--l0-live");
                    String yesMessage;
                    String noMessage;
                    if (invert) {
                        yesMessage = "--l0-live: [Dead %s] Transition is l0-live / dead.";
                        noMessage = "--l0-live: [Live %s] Transition is not l0-live / dead.";
                    } else {
                        yesMessage = "--l1-live: [Live %s] Transition is l1-live.";
                        noMessage = "--l1-live: [Dead %s] Transition is not l1-live.";
                    }

                    if (i == args.length - 1) {
                        Log.error("Missing argument to " + args[i]);
                        break;
                    }

                    if (args[i + 1].equals("*")) {
                        for (ASTTransition transition : ast.getTransitionList()) {
                            if (Liveness.isL1Live(ast.getCoverabilityTree(), transition) ^ invert) {
                                Log.info(String.format(yesMessage, transition.getName()), PetrinetsTool.class.getName());
                            } else {
                                Log.info(String.format(noMessage, transition.getName()), PetrinetsTool.class.getName());
                            }
                        }
                    } else {
                        for (String trans : args[i + 1].split(",")) {
                            Optional<TransitionSymbol> symbol = modelTopScope.resolve(trans, TransitionSymbol.KIND);
                            if (!symbol.isPresent()) {
                                Log.warn(args[i] + ": [Error " + trans + "] Transition not found in petrinet.");
                                continue;
                            }

                            Optional<ASTTransition> transitionNode = symbol.get().getTransitionNode();

                            if (!transitionNode.isPresent()) {
                                Log.warn(args[i] + ": [???? " + trans + "] Transition not found in petrinet, ignoring.");
                            } else if (Liveness.isL1Live(ast.getCoverabilityTree(), transitionNode.get()) ^ invert) {
                                Log.info(String.format(yesMessage, trans), PetrinetsTool.class.getName());
                            } else {
                                Log.info(String.format(noMessage, trans), PetrinetsTool.class.getName());
                            }
                        }
                    }
                    i++;
                    break;
                case "--type":
                    for (ASTSubclass type : ASTSubclass.values()) {
                        boolean result = Subclass.belongsToClass(type, ast);
                        if (result) {
                            Log.info(args[i] + ": [Type " + type.toString() + "] Petrinet is of type \"" + type.toString() + "\"", PetrinetsTool.class.getName());
                        } else {
                            Log.info(args[i] + ": [Type NOT-" + type.toString() + "] Petrinet is not of type \"" + type.toString() + "\"", PetrinetsTool.class.getName());
                        }
                    }
                    break;
                default:
                    Log.error(args[i] + ": function is not implemented.");
                    break;
            }
        }
    }

    private static ASTPetrinet parse(String model) {
        try {
            PetrinetParser parser = new PetrinetParser();
            Optional<ASTPetrinet> optPetrinet = parser.parse(model);

            if (!parser.hasErrors() && optPetrinet.isPresent()) {
                return optPetrinet.get();
            }
            Log.error("Model could not be parsed.");
        } catch (RecognitionException | IOException e) {
            Log.error("Failed to parse " + model, e);
        }
        return null;
    }

    private static Scope createSymbolTable(PetrinetLanguage lang, ASTPetrinet ast) {
        final ResolvingConfiguration resolverConfiguration = new ResolvingConfiguration();
        resolverConfiguration.addDefaultFilters(lang.getResolvingFilters());

        GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, resolverConfiguration);

        Optional<PetrinetSymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
                resolverConfiguration, globalScope);
        //noinspection OptionalGetWithoutIsPresent
        return symbolTable.get().createFromAST(ast);
    }
}
