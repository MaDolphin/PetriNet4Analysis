# Petrinets4Analysis

### Introduction

This tool can be used for supporting development and use of petrinet
models. It allows the description of models and the verification of
essential properties such as safeness and transition liveness.

Furthermore, subclasses of petrinets can be recognised and certain
simplifying transformations are applied upon request. _[[not yet completed]]_ 

### The Petrinets4Analysis Language

##### Example

The basic structure of a model in the domain-specific language
is as follows:

```
petrinet ExamplePetrinet {

    /* C-style block comments
     * may be used anywhere
     * and span multiple lines.
     */
     
    place P1 initial 1;
    
    transition t1: // this is a transition followed by a line-comment
        1 <- P1
        1 -> P2
        
    place P2; // places may also come after transitions referencing them
}
```

##### Description

A petrinet must have an (arbitrarily chosen) name following the keyword
`petrinet`. Within braces, it can contain any number of _places_ and
_transitions_ (in any order).

A place is introduced with the keyword `place`, has a unique name and
can---optionally---contain tokens in the initial marking (default: 0).

A transition also has a name (distinct from all other transitions and
places) following the keyword `transition`. It is described by a set of
incoming and outgoing edges specified below; each edge has a _weight_
indicating the number of tokens moved (usually 1, but any positive integer
can be used), and references a place that must be defined somewhere
in the petrinet. `n <- P` (read: _n from P_) is an incoming edge,
i.e. the tokens are taken from P upon firing the transition. Conversely,
`n -> P` (read: _n to P_) is an outgoing edge, and n tokens are added to
P once the transition is fired.

##### Assertions

Additionally, the petrinet may include _assertions_ of the form
```
petrinet Assertions {

    assert safe;
    assert l1live(*);
    assert extended_free_choice;
    assert l0live(t);
    
    place P;
    transition t:
        1 <- P
}
```

If the assertion is not satisfied, the petrinet will not be processed
and an error will be reported. This is useful for modeling where certain
domain requirements should hold at all times and not need to be verified
manually.

The following is a list of currently supported assertions and their meaning.

| Assertion | Meaning |
|-----------|---------|
| `assert safe;` | The petrinet is _safe_, i.e. no transition sequence results in more than one token per place. |
| `assert bounded;` | The petrinet is _bounded_, i.e. it is not possible to produce arbitrarily many tokens. |
| `assert l0live;`<br>`assert l0live(*);`<br>`assert l0live(t1,t2);` | The specified transitions are _l0-live_, i.e. can never be fired in any sequence. If `*` or no argument, applies to all transitions. |
| `assert l1live;`<br>`assert l1live(*);`<br>`assert l1live(t1,t2);` | The specified transitions are _l1-live_, i.e. there is a transition sequence where they are fired. If `*` or no argument, applies to all transitions. |
| `assert state_machine;` | The petrinet is a _state machine_, i.e. each transition has a unique in- and out-place. |
| `assert marked_graph;` | The petrinet is a _marked graph_, i.e. each place has a unique incoming and outgoing transition. |
| `assert free_choice;` | The petrinet is _free-choice_, i.e. if a place has multiple outgoing edges, each of these must be the only from-edge of their transition. |
| `assert extended_free_choice;` | The petrinet is an _extended free-choice net_, i.e. for any two places the sets of outgoing transitions are either the same, or disjoint. |
| `assert asymmetric_choice;` | The petrinet is _asymmetric-choice_ (_simple_), i.e. for any two places the sets of outgoing transitions are either disjoint or one is contained within the other. |

### The Petrinets4Analysis Application
 
#### Building

Have maven installed and run `mvn install`.

Tested with JDK 12; compiled for JDK 8.

#### Running the Tool

The maven configuration produces a compiled jar, including all
dependencies, at `[project]/target/petrinets-[version]-jar-with-dependencies.jar`,
which can be run as a normal command-line tool with the Java interpreter
(JRE required), i.e. `java -jar /path/to/petrinets.jar inputfile.pn`.

In normal use, the program will attempt to parse and verify a given
petrinet model file. Additionally, certain arguments can modify the
behaviour:

| Flag | Behaviour |
|------|-----------|
| `--debug` | Enables debug output, if it has been disabled (`--pretty`, `--dot`). |
| `--no-debug` | Disables debug output, e.g. if the output should be fed into another program or a file. |
| `--safe` or `--unsafe` | Checks whether `assert safe;` would be successful, including a (counter-)example. |
| `--bounded` or `--unbounded` | Checks whether `assert bounded;` would be successful, including a (counter-)example. |
| `--l0-live *` or `--l1-live *` | For each transition, checks whether it is l0-live or l1-live. |
| `--l0-live t1,t2` | Checks whether `assert l0live(t1,t2);` would be successful, including a counterexample. |
| `--l1-live t1,t2` | Checks whether `assert l1live(t1,t2);` would be successful, including an example. |
| `--type` | Checks which of the subclasses the net belongs to, i.e. which `assert subclass;` would be successful. |
| `--pretty` | Pretty-prints the given petrinet in a more readable format. Implies `--no-debug`. |
| `--dot` | Prints the petrinet in [dot-format](https://www.graphviz.org/doc/info/lang.html) that can be visualised, e.g. with [graphviz](https://www.graphviz.org/) using <code>java -jar petrinets.jar model.pn --dot &#124; dot -Tpng > graph.png</code> |
| `--simplify` | Applies simple transformations to reduce petrinet complexity. |

Arguments are order-sensitive: `petrinets.jar input.pn --pretty --simplify` will give the *original* petrinet and *then*
simplify. `petrinets.jar input.pn --simplify --pretty` will *first* simplify and *then* output the result.

Similarly, `petrinet.jar input.pn --bounded --simplify --bounded` could be used to verify that the transformation
preserves boundedness: duplicate arguments will be evaluated multiple times in order.  