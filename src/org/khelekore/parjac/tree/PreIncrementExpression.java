package org.khelekore.parjac.tree;

import java.util.Deque;

import org.khelekore.parjac.lexer.ParsePosition;

public class PreIncrementExpression extends PositionNode {
    private final TreeNode exp;

    public PreIncrementExpression (Deque<TreeNode> parts, ParsePosition pos) {
	super (pos);
	parts.pop (); // '++'
	exp = parts.pop ();
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + exp + "}";
    }
}
