package org.khelekore.parjac.grammar;

import java.util.Collection;
import java.util.Collections;

import org.khelekore.parjac.lexer.Token;

public class TokenPart extends PartBase<Token> {
    public TokenPart (Token token) {
	super (token);
    }

    @Override public Collection<String> getSubrules () {
	return Collections.emptySet ();
    }

    @Override public boolean isRulePart () {
	return false;
    }

    @Override public boolean isTokenPart () {
	return true;
    }
}
