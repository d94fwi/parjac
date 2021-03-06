package org.khelekore.parjac.tree;

import java.util.Deque;

import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;

public class UnannClassType extends ClassType {
    public UnannClassType (SimpleClassType sct, ParsePosition pos) {
	super (sct, pos);
    }

    public static ClassType build (Rule r, Deque<TreeNode> parts, ParsePosition pos) {
	if (r.size () == 1 || r.size () == 2) {
	    SimpleClassType sct = new SimpleClassType (r, parts, pos);
	    return new UnannClassType (sct, pos);
	}
	UnannClassType ct = (UnannClassType)parts.pop ();
	ct.add ((SimpleClassType)parts.pop ());
	return ct;
    }
}
