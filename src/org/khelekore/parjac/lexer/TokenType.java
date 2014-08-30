package org.khelekore.parjac.lexer;

import java.util.EnumSet;

public enum TokenType {
    SUB ("\u001a"),  // only allowed at end of input and should be ignored

    // Whitespace, note that line terminators are also whitespace
    WHITESPACE ("whitespace"), // ' ', '\t', '\f'

    // LineTerminator:
    LF ("\n"),
    CR ("\r"),
    CRLF ("\r\n"),

    // Separators
    LEFT_PARANTHESIS ("("),
    RIGHT_PARANTHESIS (")"),
    LEFT_CURLY ("{"),
    RIGHT_CURLY ("}"),
    LEFT_BRACKET ("["),
    RIGHT_BRACKET ("]"),
    SEMICOLON (";"),
    COMMA (","),
    DOT ("."),
    ELLIPSIS ("..."),
    AT ("@"),
    DOUBLE_COLON ("::"),

    // key words
    ABSTRACT ("abstract"),
    ASSERT ("assert"),
    BOOLEAN ("boolean"),
    BREAK ("break"),
    BYTE ("byte"),
    CASE ("case"),
    CATCH ("catch"),
    CHAR ("char"),
    CLASS ("class"),
    CONST ("const"),            // reserved, but not used
    CONTINUE ("continue"),
    DEFAULT ("default"),
    DO ("do"),
    DOUBLE ("double"),
    ELSE ("else"),
    ENUM ("enum"),
    EXTENDS ("extends"),
    FINAL ("final"),
    FINALLY ("finally"),
    FLOAT ("float"),
    FOR ("for"),
    GOTO ("goto"),              // reserved, but not used
    IF ("if"),
    IMPLEMENTS ("implements"),
    IMPORT ("import"),
    INSTANCEOF ("instanceof"),
    INT ("int"),
    INTERFACE ("interface"),
    LONG ("long"),
    NATIVE ("native"),
    NEW ("new"),
    PACKAGE ("package"),
    PRIVATE ("private"),
    PROTECTED ("protected"),
    PUBLIC ("public"),
    RETURN ("return"),
    SHORT ("short"),
    STATIC ("static"),
    STRICTFP ("strictfp"),
    SUPER ("super"),
    SWITCH ("switch"),
    SYNCHRONIZED ("synchronized"),
    THIS ("this"),
    THROW ("throw"),
    THROWS ("throws"),
    TRANSIENT ("transient"),
    TRY ("try"),
    VOID ("void"),
    VOLATILE ("volatile"),
    WHILE ("while"),

    // operators
    EQUAL ("="),
    GT (">"),
    LT ("<"),
    NOT ("!"),
    TILDE ("~"),
    QUESTIONMARK ("?"),
    COLON (":"),
    ARROW ("->"),
    DOUBLE_EQUAL ("=="),
    GE (">="),
    LE ("<="),
    NOT_EQUAL ("!="),
    LOGICAL_AND ("&&"),
    LOGICAL_OR ("||"),
    INCREMENT ("++"),
    DECREMENT ("--"),
    PLUS ("+"),
    MINUS ("-"),
    MULTIPLY ("*"),
    DIVIDE ("/"),
    BIT_AND ("&"),
    BIT_OR ("|"),
    BIT_XOR ("^"),
    REMAINDER ("%"),
    LEFT_SHIFT ("<<"),
    RIGHT_SHIFT (">>"),
    RIGHT_SHIFT_UNSIGNED (">>>"),
    PLUS_EQUAL ("+="),
    MINUS_EQUAL ("-="),
    MULTIPLY_EQUAL ("*="),
    DIVIDE_EQUAL ("/="),
    BIT_AND_EQUAL ("&="),
    BIT_OR_EQUAL ("|="),
    BIT_XOR_EQUAL ("^="),
    REMAINDER_EQUAL ("%="),
    LEFT_SHIFT_EQUAL ("<<="),
    RIGHT_SHIFT_EQUAL (">>="),
    RIGHT_SHIFT_UNSIGNED_EQUAL (">>>="),

    // comments
    MULTILINE_COMMENT ("/* ... */"),
    ONELINE_COMMENT ("// ..."),

    IDENTIFIER ("identifier"),

    // Literal
    INTEGER_LITERAL ("integer literal"),
    FLOATINGPOINT_LITERAL ("floatingpoint literal"),
    CHARACTER_LITERAL ("character literal"),
    STRING_LITERAL ("string literal"),
    NULL ("null"),
    TRUE ("true"),
    FALSE ("false"),

    // Signal lexer error
    ERROR ("error");

    private final String value;

    private TokenType (String value) {
	this.value = value;
    }

    private static final EnumSet<TokenType> whitespaces =
    EnumSet.of (WHITESPACE, LF, CR, CRLF);

    private static final EnumSet<TokenType> operators =
    EnumSet.of (EQUAL, GT, LT, NOT, TILDE, QUESTIONMARK, COLON, ARROW, DOUBLE_EQUAL,
		GE, LE, NOT_EQUAL, LOGICAL_AND, LOGICAL_OR, INCREMENT, DECREMENT,
		PLUS, MINUS, MULTIPLY, DIVIDE, BIT_AND, BIT_OR, BIT_XOR,
		REMAINDER, LEFT_SHIFT, RIGHT_SHIFT, RIGHT_SHIFT_UNSIGNED,
		PLUS_EQUAL, MINUS_EQUAL, MULTIPLY_EQUAL, DIVIDE_EQUAL,
		BIT_AND_EQUAL, BIT_OR_EQUAL, BIT_XOR_EQUAL, REMAINDER_EQUAL,
		LEFT_SHIFT_EQUAL, RIGHT_SHIFT_EQUAL, RIGHT_SHIFT_UNSIGNED_EQUAL);

    private static final EnumSet<TokenType> keywords =
    EnumSet.of (ABSTRACT, ASSERT, BOOLEAN, BREAK, BYTE, CASE, CATCH, CHAR, CLASS, CONST,
		CONTINUE, DEFAULT, DO, DOUBLE, ELSE, ENUM, EXTENDS, FINAL, FINALLY, FLOAT,
		FOR, GOTO, IF, IMPLEMENTS, IMPORT, INSTANCEOF, INT, INTERFACE, LONG,
		NATIVE, NEW, PACKAGE, PRIVATE, PROTECTED, PUBLIC, RETURN, SHORT, STATIC,
		STRICTFP, SUPER, SWITCH, SYNCHRONIZED, THIS, THROW, THROWS, TRANSIENT,
		TRY, VOID, VOLATILE, WHILE);

    private static final EnumSet<TokenType> literals =
    EnumSet.of (INTEGER_LITERAL, FLOATINGPOINT_LITERAL, CHARACTER_LITERAL,
		STRING_LITERAL, NULL, TRUE, FALSE);

    private static final EnumSet<TokenType> separator =
    EnumSet.of (LEFT_PARANTHESIS, RIGHT_PARANTHESIS, LEFT_CURLY, RIGHT_CURLY,
		LEFT_BRACKET, RIGHT_BRACKET, SEMICOLON, COMMA, DOT, ELLIPSIS,
		AT, DOUBLE_COLON);

    public boolean isWhitespace () {
	return whitespaces.contains (this);
    }

    public boolean isIdentifier () {
	return this == IDENTIFIER;
    }

    public boolean isKeyword () {
	return keywords.contains (this);
    }

    public boolean isLiteral () {
	return literals.contains (this);
    }

    public boolean isSeparator () {
	return separator.contains (this);
    }

    public boolean isOperator () {
	return operators.contains (this);
    }
}
