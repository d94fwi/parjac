package org.khelekore.parjac.tree;

import org.khelekore.parjac.lexer.ParsePosition;

public class IntLiteral extends PositionNode {
    private final int value;

    public IntLiteral (int value, ParsePosition pos) {
	super (pos);
	this.value = value;
    }

    public int get () {
	return value;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + value + "}";
    }
}