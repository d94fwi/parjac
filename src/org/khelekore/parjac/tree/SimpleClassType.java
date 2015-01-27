package org.khelekore.parjac.tree;

import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;

public class SimpleClassType implements TreeNode {
    private final List<TreeNode> annotations;
    private final String id;
    private final TypeArguments typeArguments;

    public SimpleClassType (Rule r, Deque<TreeNode> parts) {
	int len = 1;
	TreeNode tn = parts.pop ();
	if (tn instanceof ZOMEntry) {
	    annotations = ((ZOMEntry)tn).get ();
	    tn = parts.pop ();
	    len++;
	} else {
	    annotations = null;
	}
	id = ((Identifier)tn).get ();
	typeArguments = r.size () > len ? (TypeArguments)parts.pop () : null;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + annotations + " " + id + " " + typeArguments + "}";
    }
}