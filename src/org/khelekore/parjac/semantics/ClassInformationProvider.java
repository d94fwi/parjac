package org.khelekore.parjac.semantics;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.khelekore.parjac.CompilerDiagnosticCollector;
import org.khelekore.parjac.tree.ExpressionType;
import org.khelekore.parjac.tree.NormalClassDeclaration;
import org.khelekore.parjac.tree.NormalInterfaceDeclaration;
import org.khelekore.parjac.tree.SyntaxTree;
import org.khelekore.parjac.tree.TreeNode;

public class ClassInformationProvider {
    private final ClassResourceHolder crh;
    private final CompiledTypesHolder cth;
    private Map<String, Map<String, FieldInformation<?>>> classFields;
    private Map<String, Map<String, List<MethodInformation>>> classMethods;

    public ClassInformationProvider (ClassResourceHolder crh, CompiledTypesHolder cth) {
	this.crh = crh;
	this.cth = cth;
	classFields = new ConcurrentHashMap<> ();
	classMethods = new ConcurrentHashMap<> ();
    }

    public LookupResult hasVisibleType (String fqn) {
	LookupResult res = cth.hasVisibleType (fqn);
	if (res.getFound ())
	    return res;
	return crh.hasVisibleType (fqn);
    }

    public void scanClassPath () throws IOException {
	crh.scanClassPath ();
    }

    public void addTypes (SyntaxTree tree, CompilerDiagnosticCollector diagnostics) {
	cth.addTypes (tree, diagnostics);
    }

    public TreeNode getType (String fqn) {
	return cth.getType (fqn);
    }

    public String getFullName (TreeNode tn) {
	return cth.getFullName (tn);
    }

    public String getFilename (TreeNode tn) {
	return cth.getFilename (tn);
    }

    public Optional<List<String>> getSuperTypes (String fqn) throws IOException {
	Optional<List<String>> supers = cth.getSuperTypes (fqn);
	if (supers.isPresent ())
	    return supers;
	return crh.getSuperTypes (fqn);
    }

    public int getFlags (String fqn) {
	// TODO: need to make sure I can merge cth and crh and get some interface type back
	// TODO: with appropriate methods on it.
	TreeNode tn;
	if ((tn = getType (fqn)) != null) {
	    // TODO: this can probably fail
	    NormalClassDeclaration clz = (NormalClassDeclaration)tn;
	    return clz.getFlags ();
	} else {
	    return crh.getClassModifiers (fqn);
	}
    }

    public void registerFields (String fqn, Map<String, FieldInformation<?>> fields) {
	classFields.put (fqn, fields);
    }

    public void registerMethods (String fqn, Map<String, List<MethodInformation>> methods) {
	classMethods.put (fqn, methods);
    }

    public FieldInformation<?> getFieldInformation (String fqn, String field) {
	if ((getType (fqn)) != null) {
	    Map<String, FieldInformation<?>> m = classFields.get (fqn);
	    if (m == null)
		return null;
	    return m.get (field);
	} else {
	    throw new IllegalStateException ("not implemented yet");
	}
    }

    public ExpressionType getFieldType (String fqn, String field) {
	if ((getType (fqn)) != null) {
	    Map<String, FieldInformation<?>> m = classFields.get (fqn);
	    if (m == null)
		return null;
	    return m.get (field).getExpressionType ();
	} else {
	    return crh.getFieldType (fqn, field);
	}
    }

    public Map<String, List<MethodInformation>> getMethods (String fqn) {
	if ((getType (fqn)) != null) {
	    return classMethods.get (fqn);
	} else {
	    return crh.getMethods (fqn);
	}
    }

    public boolean isInterface (String type) {
	TreeNode tn = getType (type);
	if (tn != null)
	    return tn instanceof NormalInterfaceDeclaration;
	return crh.isInterface (type);
    }
}