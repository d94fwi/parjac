package org.khelekore.parjac.tree;

import java.util.Deque;

import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;

public class ClassType extends Multipart<SimpleClassType> {
    private final ParsePosition pos;
    private String fqn;

    public ClassType (SimpleClassType sct, ParsePosition pos) {
	super (sct);
	this.pos = pos;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + get () + ", fqn: " + fqn + "}";
    }

    public ParsePosition getParsePosition () {
	return pos;
    }

    public void setFullName (String fqn) {
	this.fqn = fqn;
    }

    public String getFullName () {
	return fqn;
    }

    public static ClassType build (Rule r, Deque<TreeNode> parts, ParsePosition pos) {
	if (r.size () == 1)
	    return new ClassType ((SimpleClassType)parts.pop (), pos);
	ClassType ct = (ClassType)parts.pop ();
	ct.add ((SimpleClassType)parts.pop ());
	return ct;
    }
}
