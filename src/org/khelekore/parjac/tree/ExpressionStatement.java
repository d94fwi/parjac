package org.khelekore.parjac.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;

public class ExpressionStatement extends PositionNode {
    private final TreeNode exp;

    public ExpressionStatement (Rule r, Deque<TreeNode> parts, ParsePosition pos) {
	super (pos);
	exp = parts.pop ();
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + exp + ";}";
    }

    public void visit (TreeVisitor visitor) {
	exp.visit (visitor);
    }

    public Collection<? extends TreeNode> getChildNodes () {
	return Collections.singleton (exp);
    }
}
