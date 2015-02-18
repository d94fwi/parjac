package org.khelekore.parjac.tree;

import java.util.Deque;

import org.khelekore.parjac.grammar.Rule;

public class ClassType extends Multipart<SimpleClassType> {
    private String fqn;

    public ClassType (SimpleClassType sct) {
	super (sct);
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + get () + ", fqn: " + fqn + "}";
    }

    public void setFullName (String fqn) {
	this.fqn = fqn;
    }

    public String getFullName () {
	return fqn;
    }

    public static ClassType build (Rule r, Deque<TreeNode> parts) {
	if (r.size () == 1)
	    return new ClassType ((SimpleClassType)parts.pop ());
	ClassType ct = (ClassType)parts.pop ();
	ct.add ((SimpleClassType)parts.pop ());
	return ct;
    }
}
