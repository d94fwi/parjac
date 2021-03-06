package org.khelekore.parjac.tree;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.CompilerDiagnosticCollector;
import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.lexer.ParsePosition;
import org.khelekore.parjac.lexer.Token;

public class ConstructorDeclaration extends FlaggedTypeBase {
    private final ConstructorDeclarator declarator;
    private final Throws throwsClause;
    private final ConstructorBody body;

    public ConstructorDeclaration (Rule r, Deque<TreeNode> parts, ParsePosition ppos,
				   Path path, CompilerDiagnosticCollector diagnostics) {
	super (!r.getRulePart (0).getId ().equals ("ConstructorDeclarator"), parts, ppos, path, diagnostics);
	declarator = (ConstructorDeclarator)parts.pop ();
	if (parts.peek () instanceof Throws) {
	    throwsClause = (Throws)parts.pop ();
	} else {
	    throwsClause = null;
	}
	body = (ConstructorBody)parts.pop ();
    }

    private static final List<TreeNode> PUBLIC_LIST = Arrays.asList (new ModifierTokenType (Token.PUBLIC, null));

    public ConstructorDeclaration (boolean publicFlag, ConstructorDeclarator declarator,
				   Throws throwsClause, ConstructorBody body) {
	super (publicFlag ? PUBLIC_LIST : Collections.emptyList (), null, null, null);
	this.declarator = declarator;
	this.throwsClause = throwsClause;
	this.body = body;
    }

    @Override public String toString () {
	return getClass ().getSimpleName () + "{" + getAnnotations () + " " + getFlags () +
	    " " + declarator + " " + throwsClause + " " + body + "}";
    }

    @Override public void visit (TreeVisitor visitor) {
	if (visitor.visit (this))
	    body.visit (visitor);
	visitor.endConstructor (this);
    }

    public Collection<? extends TreeNode> getChildNodes () {
	return Collections.singleton (body);
    }

    public String getId () {
	return declarator.getId ();
    }

    public TypeParameters getTypeParameters () {
	return declarator.getTypeParameters ();
    }

    public FormalParameterList getParameters () {
	return declarator.getParameters ();
    }

    public Throws getThrows () {
	return throwsClause;
    }

    public String getDescription () {
	StringBuilder sb = new StringBuilder ();
	sb.append ("(");
	FormalParameterList fpl = getParameters ();
	if (fpl != null)
	    fpl.appendDescription (sb);
	sb.append (")V");
	return sb.toString ();
    }
}
