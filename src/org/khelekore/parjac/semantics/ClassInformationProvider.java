package org.khelekore.parjac.semantics;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.khelekore.parjac.tree.NormalClassDeclaration;
import org.khelekore.parjac.tree.SyntaxTree;
import org.khelekore.parjac.tree.TreeNode;

public class ClassInformationProvider {
    private final ClassResourceHolder crh;
    private final CompiledTypesHolder cth;

    public ClassInformationProvider (ClassResourceHolder crh, CompiledTypesHolder cth) {
	this.crh = crh;
	this.cth = cth;
    }

    public boolean hasType (String fqn) {
	return crh.hasType (fqn) || cth.hasType (fqn);
    }

    public void scanClassPath () throws IOException {
	crh.scanClassPath ();
    }

    public void addTypes (SyntaxTree tree) {
	cth.addTypes (tree);
    }

    public TreeNode getType (String fqn) {
	return cth.getType (fqn);
    }

    public String getFullName (TreeNode tn) {
	return cth.getFullName (tn);
    }

    public String getFilename (TreeNode tn) {
	return cth.getFilename (tn);
    }

    public Optional<List<String>> getSuperTypes (String fqn) throws IOException {
	Optional<List<String>> supers = cth.getSuperTypes (fqn);
	if (supers.isPresent ())
	    return supers;
	return crh.getSuperTypes (fqn);
    }

    public int getFlags (String fqn) {
	// TODO: need to make sure I can merge cth and crh and get some interface type back
	// TODO: with appropriate methods on it.
	TreeNode tn;
	if ((tn = getType (fqn)) != null) {
	    // TODO: this can probably fail
	    NormalClassDeclaration clz = (NormalClassDeclaration)tn;
	    return clz.getAccessFlags ();
	} else {
	    return crh.getClassModifiers (fqn);
	}
    }
}