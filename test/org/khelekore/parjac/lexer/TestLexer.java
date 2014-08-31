package org.khelekore.parjac.lexer;

import java.nio.CharBuffer;
import java.util.Arrays;
import org.testng.annotations.Test;

public class TestLexer {

    @Test
    public void testSpaceWhitespace () {
	testInput (" ", Token.WHITESPACE);
	testInput ("    ", Token.WHITESPACE);
	testInput ("\t", Token.WHITESPACE);
	testInput ("\f", Token.WHITESPACE);
	testInput ("\t\t\t    ", Token.WHITESPACE);
    }

    @Test
    public void testNewlines () {
	testInput ("\n", Token.LF);
	testInput ("\r", Token.CR);
	testInput ("\r \n", Token.CR, Token.WHITESPACE, Token.LF);
	testInput ("\r\n", Token.CRLF);
	testInput ("\n\r", Token.LF, Token.CR);
    }

    @Test
    public void testSub () {
	testInput ("\u001a", Token.SUB);
    }

    @Test
    public void testColon () {
	testInput (":", Token.COLON);
	testInput (": :", Token.COLON, Token.WHITESPACE, Token.COLON);
	testInput ("::", Token.DOUBLE_COLON);
	testInput (":::", Token.DOUBLE_COLON, Token.COLON);
	testInput ("::::", Token.DOUBLE_COLON, Token.DOUBLE_COLON);
	testInput (":: ::", Token.DOUBLE_COLON, Token.WHITESPACE, Token.DOUBLE_COLON);
    }

    @Test
    public void testDot () {
	testInput (".", Token.DOT);
	testInput ("..", Token.DOT, Token.DOT);
	testInput ("...", Token.ELLIPSIS);
    }

    @Test
    public void testSeparators () {
	testInput ("(", Token.LEFT_PARANTHESIS);
	testInput (")", Token.RIGHT_PARANTHESIS);
	testInput ("{", Token.LEFT_CURLY);
	testInput ("}", Token.RIGHT_CURLY);
	testInput ("[", Token.LEFT_BRACKET);
	testInput ("]", Token.RIGHT_BRACKET);
	testInput (";", Token.SEMICOLON);
	testInput (",", Token.COMMA);
	testInput ("@", Token.AT);
    }

    @Test
    public void testOperators () {
	testInput ("=", Token.EQUAL);
	testInput ("= =", Token.EQUAL, Token.WHITESPACE, Token.EQUAL);
	testInput (">", Token.GT);
	testInput ("<", Token.LT);
	testInput ("!", Token.NOT);
	testInput ("~", Token.TILDE);
	testInput ("?", Token.QUESTIONMARK);
	testInput (":", Token.COLON);
	testInput ("->", Token.ARROW);
	testInput ("==", Token.DOUBLE_EQUAL);
	testInput (">=", Token.GE);
	testInput ("<=", Token.LE);
	testInput ("!=", Token.NOT_EQUAL);
	testInput ("&&", Token.LOGICAL_AND);
	testInput ("||", Token.LOGICAL_OR);
	testInput ("++", Token.INCREMENT);
	testInput ("--", Token.DECREMENT);
	testInput ("+", Token.PLUS);
	testInput ("-", Token.MINUS);
	testInput ("*", Token.MULTIPLY);
	testInput ("/", Token.DIVIDE);
	testInput ("&", Token.BIT_AND);
	testInput ("|", Token.BIT_OR);
	testInput ("^", Token.BIT_XOR);
	testInput ("%", Token.REMAINDER);
	testInput ("<<", Token.LEFT_SHIFT);
	testInput (">>", Token.RIGHT_SHIFT);
	testInput (">>>", Token.RIGHT_SHIFT_UNSIGNED);
	testInput ("+=", Token.PLUS_EQUAL);
	testInput ("-=", Token.MINUS_EQUAL);
	testInput ("*=", Token.MULTIPLY_EQUAL);
	testInput ("/=", Token.DIVIDE_EQUAL);
	testInput ("&=", Token.BIT_AND_EQUAL);
	testInput ("|=", Token.BIT_OR_EQUAL);
	testInput ("^=", Token.BIT_XOR_EQUAL);
	testInput ("%=", Token.REMAINDER_EQUAL);
	testInput ("<<=", Token.LEFT_SHIFT_EQUAL);
	testInput (">>=", Token.RIGHT_SHIFT_EQUAL);
	testInput (">>>=", Token.RIGHT_SHIFT_UNSIGNED_EQUAL);
    }

    @Test
    public void testGenerics () {
	Lexer l = getLexer (">>");
	l.setInsideTypeContext (true);
	testLexing (l, Token.GT, Token.GT);
    }

    @Test
    public void testComment () {
	testInput ("// whatever", Token.ONELINE_COMMENT);
	testInput ("/* whatever */", Token.MULTILINE_COMMENT);
	testInput ("/* this comment /* // /** ends here: */", Token.MULTILINE_COMMENT);
	testInput ("/* whatever", Token.ERROR);
	testInput ("/* whatever \n whatever */", Token.MULTILINE_COMMENT);
	testInput ("/* whatever \n * whatever \n *\n/*/", Token.MULTILINE_COMMENT);
    }

    @Test
    public void testCharLiteral () {
	testChar ("'a'", 'a');
	testChar ("'\\\\'", '\\');
	testChar ("'\\t'", '\t');
	// Does not handle octal escapes yet, silly thing
	// testInput ("'\\12'", Token.CHARACTER_LITERAL); // octal escape
	testInput ("'\\a'", Token.ERROR);
	testInput ("'ab'", Token.ERROR);
	testInput ("'a", Token.ERROR);
    }

    private void testChar (String toLex, char value) {
	Lexer l = getLexer (toLex);
	testLexing (l, Token.CHARACTER_LITERAL);
	char res = l.getCurrentCharValue ();
	assert value == res : "Wrong string value: " + res;
    }

    @Test
    public void testStringLiteral () {
	testString ("\"\"", "");
	testString ("\"abc123\"", "abc123");
    }

    private void testString (String toLex, String value) {
	Lexer l = getLexer (toLex);
	testLexing (l, Token.STRING_LITERAL);
	String res = l.getCurrentStringValue ();
	assert value.equals (res) : "Wrong string value: " + res;
    }

    @Test
    public void testComplex () {
	// nonsense, but valid for tokenisation
	testInput ("{    \n    (/*whatever*/)=[]}",
		   Token.LEFT_CURLY, Token.WHITESPACE,
		   Token.LF, Token.WHITESPACE,
		   Token.LEFT_PARANTHESIS, Token.MULTILINE_COMMENT,
		   Token.RIGHT_PARANTHESIS, Token.EQUAL,
		   Token.LEFT_BRACKET, Token.RIGHT_BRACKET,
		   Token.RIGHT_CURLY);
    }

    /* Not working yet
    @Test
    public void testNullLiteral () {
	testInput ("null", Token.NULL);
    }

    @Test
    public void testBooleanLiterals () {
	testInput ("true", Token.TRUE);
	testInput ("false", Token.FALSE);
    }

    @Test
    public void testKeywords () {
        // fill in keywords
    }

    @Test
    public void testNonNullLiteral () {
	testInput ("null_a", Token.IDENTIFIER);
    }
    */

    @Test
    public void testIntLiterals () {
	testInt ("0", 0);
	testInt ("2", 2);
	testInt ("1996", 1996);
	testInput ("1_", Token.ERROR);
	testInt ("2147483648", -2147483648); // may seem a bit odd
	testInput ("2147483649", Token.ERROR);
	testInt ("0372", 0372);
	testInt ("0xDada_Cafe", 0xDada_Cafe);
	testInt ("0x00_FF__00_FF", 0x00_FF__00_FF);
	testInt ("0b1111", 0b1111);
	testInput ("0x", Token.ERROR);

	// javac is a bit iffy with octal handling compared to binary
	// lets handle individual characters out of range the same here
	testInput ("08", Token.INT_LITERAL, Token.INT_LITERAL);
	testInput ("0b12", Token.INT_LITERAL, Token.INT_LITERAL);
	testInput ("1 2", Token.INT_LITERAL, Token.WHITESPACE, Token.INT_LITERAL);
    }

    private void testInt (String toLex, int expected) {
	Lexer l = getLexer (toLex);
	testLexing (l, Token.INT_LITERAL);
	int val = l.getCurrentIntValue ();
	assert val == expected : "Wrong string value: " + val + ", expected: " + expected;
    }

    @Test
    public void testLongLiterals () {
	testLong ("0l", 0);
	testLong ("0777L", 0777);
	testLong ("0x100000000L", 0x100000000L);
	testLong ("2_147_483_648L", 2_147_483_648L);
	testLong ("2147483649L", 2147483649L);
	testLong ("0xC0B0L", 0xC0B0L);
    }

    private void testLong (String toLex, long expected) {
	Lexer l = getLexer (toLex);
	testLexing (l, Token.LONG_LITERAL);
	long val = l.getCurrentLongValue ();
	assert val == expected : "Wrong string value: " + val + ", expected: " + expected;
    }

    /*
    @Test
    public void testLongLiterals () {
        // fill in
    }

    @Test
    public void testDoubleLiterals () {
        // fill in
    }

    @Test
    public void testFloatLiterals () {
        // fill in
    }
    */

    private void testInput (String text, Token... expected) {
	Lexer l = getLexer (text);
	testLexing (l, expected);
    }

    private Lexer getLexer (String text) {
	CharBuffer cb = CharBuffer.wrap (text.toCharArray ());
	return new Lexer ("TestLexer", cb);
    }

    private void testLexing (Lexer l, Token... expected) {
	for (int i = 0; i < expected.length; i++) {
	    assert l.hasMoreTokens () : "Too few tokens, expected: " + Arrays.toString (expected);
	    Token t = l.nextToken ();
	    assert t != null : "Returned token may not be null";
	    assert t == expected[i] : "Wrong Token: expected: " + expected[i] + ", got: " + t
		+ (t == Token.ERROR ? ", error code: " + l.getError () : "");
	}
	assert !l.hasMoreTokens () : "Lexer has more available tokens than expected";
    }
}