package org.khelekore.parjac.tree;

import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;

public class CompilationUnit implements TreeNode {
    private final PackageDeclaration pd;
    private final ZOMEntry imports;
    private final ZOMEntry types;

    public CompilationUnit (Rule r, Deque<TreeNode> parts) {
	PackageDeclaration pd = null;
	ZOMEntry imports = null;
	ZOMEntry types = null;

	if (!parts.isEmpty ()) {
	    TreeNode tn = parts.pop ();
	    pd = tn instanceof PackageDeclaration ? (PackageDeclaration)tn : null;
	    if (pd != null)
		tn = parts.isEmpty () ? null : parts.pop ();
	    if (tn != null) {
		ZOMEntry zn = (ZOMEntry)tn;
		if (hasImports (zn)) {
		    imports = zn;
		    tn = parts.isEmpty () ? null : parts.pop ();
		}
		if (tn != null)
		    types = (ZOMEntry)tn;
	    }
	}
	this.pd = pd;
	this.imports = imports;
	this.types = types;
    }

    private final boolean hasImports (ZOMEntry z) {
	List<TreeNode> ls = z.getNodes ();
	return ls.get (0) instanceof ImportDeclaration;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{package: " +  pd +
	    ", imports: " + imports + ", types: " + types + "}";
    }
}