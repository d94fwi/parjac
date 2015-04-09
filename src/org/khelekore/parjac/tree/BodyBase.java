package org.khelekore.parjac.tree;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;
import org.khelekore.parjac.tree.ZOMEntry;

public abstract class BodyBase implements TreeNode {
    private final List<TreeNode> declarations;

    public BodyBase () {
	declarations = Collections.emptyList ();
    }

    public BodyBase (Rule r, Deque<TreeNode> parts, ParsePosition ppos) {
	if (r.size () > 2) {
	    ZOMEntry ze = (ZOMEntry)parts.pop ();
	    declarations = ze.get ();
	} else {
	    declarations = Collections.emptyList ();
	}
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{declarations: " + declarations + "}";
    }

    public List<TreeNode> getDeclarations () {
	return declarations;
    }

    public void visit (TreeVisitor visitor) {
	declarations.forEach (d -> d.visit (visitor));
    }
}
