package org.khelekore.parjac.tree;

import java.util.Deque;

import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;

public class ClassInstanceCreationExpression extends PositionNode {
    private final TreeNode from;
    private final UntypedClassInstanceCreationExpression ucice;

    public ClassInstanceCreationExpression (Rule r, Deque<TreeNode> parts, ParsePosition pos) {
	super (pos);
	from = parts.pop ();
	ucice = (UntypedClassInstanceCreationExpression)parts.pop ();
    }

    public static TreeNode build (Rule r, Deque<TreeNode> parts, ParsePosition pos) {
	if (r.size () == 1)
	    return parts.pop ();
	return new ClassInstanceCreationExpression (r, parts, pos);
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + from + "." + ucice + "}";
    }

    public void visit (TreeVisitor visitor) {
	ucice.visit (visitor);
    }
}
