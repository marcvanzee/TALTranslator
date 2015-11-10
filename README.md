### TALTranslator: From Temporal Action Logic to Computer Programs

I have developed the ```TALTranslator``` application that supplements the theoretical work discussed in my master thesis:

> [Implementing Temporal Action Logics using Logic Programming and SMT Solving](http://www.marcvanzee.nl/publications/2013/masterthesis2013_implementing_TAL.pdf) (Marc van Zee), Master's thesis, Utrecht University, 2013.

As the name might suggest, the application automatically translates TAL narratives into a form that is amendable for theorem proving. The following figure shows the workflow that is intended for the application: 

![The intended workflow of TALTranslator](http://www.marcvanzee.nl/tmp/taltranslatorimg/img1.png)

Some entity in the world (the user, or perhaps another application) specifies a narrative in ```TALTranslator```. This narrative is then translated into a form that is recognized by an off-theshelf theorem prover of choice, such as a logic programming implementation or an SMT solver. This compiled narrative can then be sent to the theorem prover which can interact with the outside world.

During the development of the application we have intended to keep this structure in mind. We hope this will allow further researcher to build upon this application easily, because it is relatively straightforward to insert theorem proving modules into the framework. Currently, the only translation that has actually been implemented is the translation into a logic program, which implements the translation that has been given in Chapter 4 of my thesis.

In this manual I will discuss how to use the application. This means I will explain how the user can specify a narrative and how this narrative can be translated into a logic program. The application has been written for Java 1.6. For a more detailed explanation of the application I refer to the sources, which contain more information about the architecture of the software.

## User Manual

### Starting the Application

The TALTranslator application can be started by running the ```Main.gui``` class.
When started, the following GUI is displayed:

![The start screen of TALTranslator](images/img2.png)

The user is able to create a new project (```CTRL+N``` or via the menubar: ```File > New Project```), or open an existing project (```CTRL+O``` or ```File > Open Project```). When starting a new project, the following dialog shows up, giving the user the possibility to choose the type of project:

![Creating a new project that translates a TAL narrative to a Prolog file](images/img3.png)

Currently, only the ```TALToProlog``` project has been implemented, but when translations to other formalisms such as SMT have been implemented, the user is able to specify the corresponding project here.

### Opening and Saving an Object

```TALTranslator``` comes with several examples, which can be found in the directory narratives. To open such an example, press ```CTRL+O``` or navigate to the menubar: ```File > Open File...```. Project specifications have the extension
```.talproj```. The following figure shows part of the project ```Robot Specification```:

![Part of the Robot Specification example scenario](images/img4.png)

Projects can be saved simply by pressing ```CTRL+S``` or navigating to ```File > Save```.

### Creating a New Project

When a new project has been created – because we have only implemented TALToProlog we will consider this case – a new empty narrative will appear in the left navigation bar. The user can specify the narrative here, using the syntax of TAL 2.01. We will summarize the syntax of this language in the next section.

### Specifying a Project: TALTranslator Syntax

We distinguish four different predicate sorts that can be used in the narrative specification:
- Fluents describe how features change over time and should have a temporal term as their first argument.
- Actions have no syntactical constraints.
- Constants are actually nullary predicate symbols but do not require parentheses (although this is allowed).
- ```Occurs``` is a reserved predicate that can be used to denote when an action occurs.

Each predicate used in the narrative will always be in one of these four categories. Therefore we require the user to enter all the predicates that will occur in the narrative in the first dialog screen of the GUI:

![Specifying the alphabet of a narrative](images/img5.png)

Moreover, since the predicates ```Holds``` and ```Occludes``` are used in the reified narrative, they are also not allowed to be used here.

This is the ```EBNF``` specification of the syntax of first order formulas without equality
extended with change context annotations:

```
fof ::= implication ( "<->", implication )* ;
implication ::= disjunction ( "->", disjunction )* ;
disjunction ::= conjunction ( "or", conjunction )* ;
conjunction ::= unary ( "and", unary )* ;
unary ::= quantification | literal ;
quantification ::= quantifier, var, ":", unary ;
quantifier ::= "exists" | "forall" ;
literal ::= atom | negation ;
negation ::= "not", unary ;
atom ::= terminal | "(", fof, ")" | "[" fof "]";
terminal ::= var | tempvar | predicate ;
predicate ::= ident, [ argblock ] ;
argblock ::= "(", [ args ], ")" ;
args ::= arg, ( ",", arg )* ;
arg ::= predicate | ident | var | tempvar | num;
ident ::= ["a"-"z"], ( numchar )* ;
var ::= ( ["A"-"S"] | ["U"-"Z"] ), ( numchar )* ;
tempvar ::= "T", [ num ] ;
numchar ::= ["a"-"z"] | ["A"-"Z"] | ["0"-"9"] ;
num ::= ( ["0"-"9"] )+ ;
```

Note that we have slightly extended the EBNF notation and allow the expression ```["a"-"z"]```, which means all alphabetical characters starting from a and ending at z, similar for numbers. If ```expr``` in ```[expr]``` is not of the form that we just described, we follow the EBNF convention which states that ```expr``` is optional. In short, variables are denoted with a capital letter. Temporal variables start with a ```T``` followed by a number while normal variables start with any capital
letter except ```T```. Predicates and constants start with a lowercase letter. Both can contain numbers as well, but not as a first character. The precedence of the connectives follows from the EBNF, but for clarity we give an overview:

```
symbol    meaning        precedence
not       negation       1
exists    quantification 2
forall    ,,             2
and       and            3
or        or             4
->        implication    5
<->       equivalence    6
```

The GUI supports syntax highlighting and will give syntax errors in real-time using the Code Assistant.

### Compiling a Narrative

Once the narrative has been specified completely (notice that a narrative in ```TALToProlog``` should fall into the class of TALM theories), it can be compiled into a ```Prolog``` file. This consists of two general steps:
1. Translate the specification to the first-order language ```L(FL)``` and circumscribe the theory.
2. Translate the circumscribed theory into a syntax amenable for the theorem prover of choice.

In the case of ```TALToProlog```, this is implemented as follows:
1. Translate the TALM theory into a definitional theory using the proofs given in Section 4.1 of my thesis;
2. Translate the definitional theory into a Prolog program using the ```def2P``` algorithm (Section 4.2 of my thesis).

The first step of this compiling can be applied by going to the menubar item: ```Run > Reify and Circumscribe```. Once this has been execute successfully, a second subproject will appear called ```Compiled narrative```. This subproject contains the item ```Complete compiled narrative```, which contains the entire theory. The following figure shows part of the compiled narrative for the example application ```Robot Specification```:

![Part of the compiled narrative for the Robot Specification application](images/img6.png)

The second step is performed by going to ```Run > Generate Prolog File```. I have included a ```Prolog solver``` into the application as well, which can be used to insert the resulting theory in. The ```Prolog solver``` is called ```tuProlog``` and can be started by going to ```Run > run tuProlog```. The following figure shows part of the Prolog file of the Robot Specification application in tuProlog:

![: tuProlog containing the Prolog file for the narrative Robot Specification application](images/img7.png)
