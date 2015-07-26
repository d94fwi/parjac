package org.khelekore.parjac;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.MalformedInputException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.khelekore.parjac.grammar.Grammar;
import org.khelekore.parjac.lexer.CharBufferLexer;
import org.khelekore.parjac.lexer.Lexer;
import org.khelekore.parjac.parser.EarleyParser;
import org.khelekore.parjac.parser.JavaTreeBuilder;
import org.khelekore.parjac.parser.PredictCache;
import org.khelekore.parjac.semantics.ClassResourceHolder;
import org.khelekore.parjac.semantics.ClassSetter;
import org.khelekore.parjac.semantics.CompiledTypesHolder;
import org.khelekore.parjac.semantics.NameModifierChecker;
import org.khelekore.parjac.tree.CompilationUnit;
import org.khelekore.parjac.tree.DottedName;
import org.khelekore.parjac.tree.SyntaxTree;

/** The actual compiler
 */
public class Compiler {
    private final CompilerDiagnosticCollector diagnostics;
    private final Grammar g;
    private final PredictCache predictCache;
    private final JavaTreeBuilder treeBuilder;
    private final CompilationArguments settings;

    private CompiledTypesHolder cth;
    private ClassResourceHolder crh;

    public Compiler (CompilerDiagnosticCollector diagnostics, Grammar g,
		     CompilationArguments settings) {
	this.diagnostics = diagnostics;
	this.g = g;
	this.predictCache = new PredictCache (g);
	this.treeBuilder = new JavaTreeBuilder (g);
	this.settings = settings;
    }

    public void compile () {
	SourceProvider sourceProvider = settings.getSourceProvider ();
	runTimed (() -> setupSourceProvider (sourceProvider), "Setting up sources");
	if (diagnostics.hasError ())
	    return;

	List<SyntaxTree> trees = runTimed (() -> parse (sourceProvider), "Parsing");
	if (diagnostics.hasError ())
	    return;

	runTimed (() -> scanClassPaths (), "Scanning classpath");
	checkSemantics (trees);
	if (diagnostics.hasError ())
	    return;
	optimize (trees);

	runTimed (() -> createOutputDirectories (trees, settings.getClassWriter()),
		  "Creating output directories");
	if (diagnostics.hasError ())
	    return;

	runTimed (() -> writeClasses (trees), "Writing classes");
    }

    private void setupSourceProvider (SourceProvider sourceProvider) {
	try {
	    sourceProvider.setup (diagnostics);
	} catch (IOException e) {
	    diagnostics.report (new NoSourceDiagnostics ("Failed to setup SourceProvider: ", sourceProvider));
	}
    }

    private List<SyntaxTree> parse (SourceProvider sourceProvider) {
	return
	    sourceProvider.getSourcePaths ().parallelStream ().
	    map (p -> parse (sourceProvider, p)).
	    filter (p -> p != null).
	    collect (Collectors.toList ());
    }

    private SyntaxTree parse (SourceProvider sourceProvider, Path path) {
	try {
	    long start = System.nanoTime ();
	    if (settings.getDebug ())
		System.out.println ("parsing: " + path);
	    CharBuffer charBuf = sourceProvider.getInput (path);
	    Lexer lexer = new CharBufferLexer (charBuf);
	    EarleyParser parser = new EarleyParser (g, path, lexer, predictCache, treeBuilder,
						    diagnostics, settings.getDebug ());
	    SyntaxTree tree = parser.parse ();
	    long end = System.nanoTime ();
	    if (settings.getDebug ())
		reportTime ("Parsing " + path, start, end);
	    return tree;
	} catch (MalformedInputException e) {
	    diagnostics.report (new NoSourceDiagnostics ("Failed to decode text: %s, wrong encoding?", path));
	    return null;
	} catch (IOException e) {
	    diagnostics.report (new NoSourceDiagnostics ("Failed to read: %s: %s", path, e));
	    return null;
	}
    }

    private void scanClassPaths () {
	try {
	    crh = new ClassResourceHolder (settings.getClassPathEntries (), diagnostics);
	    crh.scanClassPath ();
	} catch (IOException e) {
	    diagnostics.report (new NoSourceDiagnostics ("Failed to scan classpath: %s", e));
	}
    }

    private void checkSemantics (List<SyntaxTree> trees) {
	cth = new CompiledTypesHolder ();
	trees.parallelStream ().forEach (t -> cth.addTypes (t));

	runTimed (() -> ClassSetter.fillInClasses (cth, crh, trees, diagnostics), "Setting classes");
	runTimed (() -> checkNamesAndModifiers (trees), "Checking names and modifiers");
	// check that there is at least one constructor
	// check that there are returns, even in void methods
	// Check types of fields and assignments
	// Check matching methods
	// Check generics
    }

    private void checkNamesAndModifiers (List<SyntaxTree> trees) {
	// Check file names / class names matching and modifiers
	trees.parallelStream ().forEach (t -> checkNamesAndModifiers (t, diagnostics));
    }

    private void checkNamesAndModifiers (SyntaxTree tree, CompilerDiagnosticCollector diagnostics) {
	NameModifierChecker nmc = new NameModifierChecker (cth, crh, tree, diagnostics);
	nmc.check ();
    }

    private void optimize (List<SyntaxTree> trees) {
	// merge constant expressions "1 + 2" => "3"
    }

    private void createOutputDirectories (List<SyntaxTree> trees,
					  BytecodeWriter classWriter) {
	Set<Path> dirs = new HashSet<> ();
	trees.stream ().
	    forEach (t -> dirs.add (getPath (t)));
	dirs.forEach (p -> createDirectory (classWriter, p));
    }

    private Path getPath (SyntaxTree t) {
	CompilationUnit cu = t.getCompilationUnit ();
	DottedName packageName = cu.getPackage ();
	if (packageName == null)
	    return Paths.get (".");
	return Paths.get (packageName.getPathName ());
    }

    private void createDirectory (BytecodeWriter bw, Path p) {
	try {
	    bw.createDirectory (p);
	} catch (IOException e) {
	    diagnostics.report (new NoSourceDiagnostics ("Failed to create output directory: %s", p));
	}
    }

    private void writeClasses (List<SyntaxTree> trees) {
	trees.parallelStream ().
	    forEach (t -> writeClasses (t));
    }

    private void writeClasses (SyntaxTree tree) {
	BytecodeGenerator w =
	    new BytecodeGenerator (tree.getOrigin (), cth, settings.getClassWriter ());
	tree.getCompilationUnit ().visit (w);
    }

    private <T> T runTimed (CompilationStep<T> cs, String type) {
	long start = System.nanoTime ();
	T ret = cs.run ();
	long end = System.nanoTime ();
	if (settings.getReportTime ())
	    reportTime (type, start, end);
	return ret;
    }

    private void runTimed (VoidCompilationStep cs, String type) {
	long start = System.nanoTime ();
	cs.run ();
	long end = System.nanoTime ();
	if (settings.getReportTime ())
	    reportTime (type, start, end);
    }

    private interface CompilationStep<T> {
	T run ();
    }

    private interface VoidCompilationStep {
	void run ();
    }

    private void reportTime (String type, long start, long end) {
	System.out.format ("%s, time taken: %.3f millis\n", type, (end - start) / 1.0e6);
    }
}
