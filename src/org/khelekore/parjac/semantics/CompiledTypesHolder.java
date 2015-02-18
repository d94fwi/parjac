package org.khelekore.parjac.semantics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.khelekore.parjac.tree.*;

public class CompiledTypesHolder {
    private Map<String, TreeNode> name2node = new HashMap<> ();
    private Map<TreeNode, String> node2id = new HashMap<> ();
    private Map<TreeNode, String> node2fqn = new HashMap<> ();

    public void addTypes (SyntaxTree tree) {
	tree.getCompilationUnit ().visit (new ClassMapper ());
    }

    /** Get the outer tree node for a given fully qualified name, that is "some.package.Foo$Bar" */
    public TreeNode getType (String name) {
	return name2node.get (name);
    }

    /** Get the class id, something like "Foo$Bar$1". */
    public String getId (TreeNode tn) {
	return node2id.get (tn);
    }

    /** Get the class id, something like "some.package.Foo$Bar$1". */
    public String getFullName (TreeNode tn) {
	return node2fqn.get (tn);
    }

    private class ClassMapper implements TreeVisitor {
	private DottedName packageName;
	private final Deque<ClassId> classes = new ArrayDeque<> ();

	public void visit (CompilationUnit cu) {
	    packageName = cu.getPackage ();
	}

	public void visit (NormalClassDeclaration c) {
	    pushClass (c.getId (), c);
	}

	public void visit (EnumDeclaration e) {
	    pushClass (e.getId (), e);
	}

	public void visit (NormalInterfaceDeclaration i) {
	    pushClass (i.getId (), i);
	}

	public void visit (AnnotationTypeDeclaration a) {
	    pushClass (a.getId (), a);
	}

	public void anonymousClass (ClassBody b) {
	    pushClass (generateAnonId (), b);
	}

	private String generateAnonId () {
	    ClassId cid = classes.peekLast ();
	    cid.anonId++;
	    return Integer.toString (cid.anonId);
	}

	public void endType () {
	    classes.removeLast ();
	}

	private void pushClass (String id, TreeNode tn) {
	    ClassId cid = new ClassId (id);
	    classes.addLast (cid);
	    String fullId = getFullId ();
	    String name = getFQN (packageName, fullId);
	    synchronized (name2node) {
		name2node.put (name, tn);
		node2id.put (tn, fullId);
		node2fqn.put (tn, name);
	    }
	}

	private String getFullId () {
	    return classes.stream ().map (cid -> cid.id).collect (Collectors.joining ("$"));
	}

	public String getFQN (DottedName packageName, String fullId) {
	    if (packageName == null)
		return fullId;
	    return packageName.getDotName () + "." + fullId;
	}
    }

    private static class ClassId {
	private final String id;
	private int anonId;

	public ClassId (String id) {
	    this.id = id;
	}

	@Override public String toString () {
	    return getClass ().getSimpleName () + "{id: " + id + ", anonId: " + anonId + "}";
	}
    }
}
