package org.khelekore.parjac.tree;

import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;

public class SwitchBlock implements TreeNode {
    private final List<SwitchBlockStatementGroup> groups;
    private final List<SwitchLabel> labels;

    public SwitchBlock (Rule r, Deque<TreeNode> parts) {
	List<SwitchBlockStatementGroup> groups = null;
	List<SwitchLabel> labels = null;

	if (r.size () > 2) {
	    ZOMEntry ze = (ZOMEntry)parts.pop ();
	    if (ze.getType ().equals ("SwitchBlockStatementGroup")) {
		groups = ze.get ();
		ze = r.size () > 3 ? (ZOMEntry)parts.pop () : null;
	    }
	    if (ze != null && ze.getType ().equals ("SwitchLabel"))
		labels = (ze).get ();
	}

	this.groups = groups;
	this.labels = labels;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + groups + " " + labels + "}";
    }
}
