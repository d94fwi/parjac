package org.khelekore.parjac.tree;

import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Rule;

public class Block implements TreeNode {
    private final List<TreeNode> statements;

    public Block (Rule r, Deque<TreeNode> parts) {
	statements = r.size () > 2 ? ((BlockStatements)parts.pop ()).get () : null;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + statements + "}";
    }
}