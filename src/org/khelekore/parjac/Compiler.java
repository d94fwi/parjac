package org.khelekore.parjac;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import org.khelekore.parjac.tree.SyntaxTree;

/** The actual compiler
 */
public class Compiler {
    private static final Charset UTF8 = Charset.forName ("UTF-8");

    public void compile (List<Path> srcFiles, Path destinationDir) {
	List<SyntaxTree> trees = parse (srcFiles);
	if (thereWasErrors ())
	    return;

	checkSemantics (trees);

	writeClasses (trees, destinationDir);
    }

    private List<SyntaxTree> parse (List<Path> srcFiles) {
	return
	    srcFiles.parallelStream ().
	    map (p -> parse (p)).
	    collect (Collectors.toList ());
    }

    private SyntaxTree parse (Path p) {
	try {
	    List<String> lines = Files.readAllLines (p, UTF8);
	    return new SyntaxTree (p);
	} catch (IOException e) {
	    System.err.println ("Failed to read: " + p);
	    return null;
	}
    }

    private void checkSemantics (List<SyntaxTree> trees) {
	// TODO: implement
    }

    private void writeClasses (List<SyntaxTree> trees, Path destinationDir) {
	trees.parallelStream ().
	    forEach (t -> writeClass (t, destinationDir));
    }

    private void writeClass (SyntaxTree tree, Path destinationDir) {
	BytecodeWriter w = new BytecodeWriter ();
	w.write (tree, destinationDir);
    }

    private boolean thereWasErrors () {
	// TODO: implement
	return false;
    }
}
