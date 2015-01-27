package org.khelekore.parjac.tree;

import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;

public class DimExpr implements TreeNode {
    private final List<TreeNode> annotations;
    private final TreeNode exp;

    public DimExpr (Rule r, Deque<TreeNode> parts) {
	annotations = r.size () > 3 ? ((ZOMEntry)parts.pop ()).get () : null;
	exp = parts.pop ();
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + annotations + " [ " + exp + " ] " + "}";
    }
}