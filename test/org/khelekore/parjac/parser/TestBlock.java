package org.khelekore.parjac.parser;

import java.io.IOException;

import org.khelekore.parjac.CompilerDiagnosticCollector;
import org.khelekore.parjac.grammar.Grammar;
import org.khelekore.parjac.grammar.GrammarReader;
import org.khelekore.parjac.lexer.Token;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestBlock {
    private Grammar g;
    private CompilerDiagnosticCollector diagnostics;

    @BeforeClass
    public void createLRParser () throws IOException {
	GrammarReader gr = new GrammarReader (true);
	gr.read (getClass ().getResource ("/java_8.pj"));
	g = gr.getGrammar ();
	g.addRule ("Goal", "Block", Token.END_OF_INPUT);
	try {
	    g.validateRules ();
	} catch (Throwable t) {
	    t.printStackTrace ();
	}
    }

    @BeforeMethod
    public void createDiagnostics () {
	diagnostics = new CompilerDiagnosticCollector ();
    }

    @Test
    public void testEmptyBlock () {
	testSuccessfulParse ("{ }");
	testSuccessfulParse ("{ ; }");
	testSuccessfulParse ("{ ;;; }");
    }

    @Test
    public void testSimpleBlock () {
	testSuccessfulParse ("{ int a = b + c; }");
    }

    @Test
    public void testConditionalExpressions () {
	testSuccessfulParse ("{ int a = b * c; }");
	testSuccessfulParse ("{ int a = b / c; }");
	testSuccessfulParse ("{ int a = b + c; }");
	testSuccessfulParse ("{ int a = b - c; }");
	testSuccessfulParse ("{ int a = b & c; }");
	testSuccessfulParse ("{ int a = b | c; }");
	testSuccessfulParse ("{ int a = b ^ c; }");
	testSuccessfulParse ("{ int a = b % c; }");
	testSuccessfulParse ("{ boolean a = b == c; }");
	testSuccessfulParse ("{ boolean a = b != c; }");
	testSuccessfulParse ("{ int a = b << 1; }");
	testSuccessfulParse ("{ int a = b << c; }");
	testSuccessfulParse ("{ int a = b >> 1; }");
	testSuccessfulParse ("{ int a = b >> c; }");
	testSuccessfulParse ("{ boolean a = b > c; }");
	testSuccessfulParse ("{ boolean a = b < c; }");
	testSuccessfulParse ("{ boolean a = a.b < c; }");
	testSuccessfulParse ("{ boolean a = a.b.c < d; }");
	testSuccessfulParse ("{ a = b < c; }");
	testSuccessfulParse ("{ a = b + c; }");
    }

    @Test
    public void testMethodCall () {
	testSuccessfulParse ("{ foo(); }");
	testSuccessfulParse ("{ int a = foo (); }");
	testSuccessfulParse ("{ a.foo (); }");
	testSuccessfulParse ("{ a.b.foo (); }");
    }

    @Test
    public void testFor () {
	testSuccessfulParse ("{ for (Foo foo : listOfFoo) {\n" +
			     "System.out.println (\"foo: \"+ foo);\n" +
			     "}\n}");
	testSuccessfulParse ("{ for (int i = CONSTANT; i >= 0; i--) {}}");
	testSuccessfulParse ("{ for (int i = 0; i < CONSTANT; i++) {}}");
	testSuccessfulParse ("{ for (int i = 0; i < 10; i++) {}}");
	testSuccessfulParse ("{ for (int i = 0, j = 0, k = a; bar1.b; i++) {}}");
    }

    @Test
    public void testIf () {
	testSuccessfulParse ("{ if (true) a = b; }");
	testSuccessfulParse ("{ if (true) return a; }");
	testSuccessfulParse ("{ if (true) return a; else return b; }");
	testSuccessfulParse ("{ if (true) { return a; } else { return b; }}");
    }

    @Test
    public void testWhile () {
	testSuccessfulParse ("{ while (true) { i++; }}");
	testSuccessfulParse ("{ do { i++; } while (true); }");
	testSuccessfulParse ("{ while (i < 4) { i++; }}");
	testSuccessfulParse ("{ while (i < foo) { i++; }}");
    }

    @Test
    public void testSwitch () {
	testSuccessfulParse ("{ switch (foo) {\n" +
			     "default: a = b; }}");
	testSuccessfulParse ("{ switch (foo) {\n" +
			     "case 1: a = foo; break;\n" +
			     "case 2: a = bar; break;\n" +
			     "case 3: a = baz; break;\n" +
			     "}}");
	testSuccessfulParse ("{ switch (foo) {\n" +
			     "case 1: a = b; break;\n" +
			     "case 2: b = 2; break;\n" +
			     "default: c = 3; }}");
	testSuccessfulParse ("{ switch (foo) {\n" +
			     "case A: return 1;\n" +
			     "case B: case C: return 2;\n" +
			     "default: return 3;\n" +
			     "}}");
    }

    @Test
    public void testReturn () {
	testSuccessfulParse ("{return true;}");
	testSuccessfulParse ("{return 1;}");
	testSuccessfulParse ("{return 1.0;}");
	testSuccessfulParse ("{return a || b;}");
	testSuccessfulParse ("{return 2 * 3;}");
	testSuccessfulParse ("{return 2 + 3;}");
	testSuccessfulParse ("{return 2 & 3;}");
    }

    @Test
    public void testLocalVariableDeclaration () {
	testSuccessfulParse ("{int i;}");
	testSuccessfulParse ("{int i, j;}");
	testSuccessfulParse ("{int[] i, j;}");
	testSuccessfulParse ("{int[] i[], j[][];}");
	testSuccessfulParse ("{int i = 1;}");
	testSuccessfulParse ("{int i = a;}");
	testSuccessfulParse ("{int i = 1 + 2;}");
	testSuccessfulParse ("{int i = 1 + a;}");
	testSuccessfulParse ("{int i;\nFoo foo;\nBar baz;\ndouble d; }");
	testSuccessfulParse ("{foo.Bar bar; }");
	testSuccessfulParse ("{foo.bar.Baz baz; }");
	testSuccessfulParse ("{foo.Bar bar = new foo.Bar(); }");
	testSuccessfulParse ("{foo.bar.Baz baz = new foo.bar.Baz(); }");
	testSuccessfulParse ("{Map<D, S> work = new TreeMap<D, S> (); }");
    }

    @Test
    public void testNew () {
	testSuccessfulParse ("{Foo foo = new Foo ();}");
	testSuccessfulParse ("{Bar bar = new foo.Bar ();}");
	testSuccessfulParse ("{Baz baz = new foo.bar.Baz ();}");
	testSuccessfulParse ("{rules = new ArrayList<> ();}");
    }

    @Test
    public void testSynchronized () {
	testSuccessfulParse ("{synchronized (foo) { i = 0; }}");
    }

    @Test
    public void testThrow () {
	testSuccessfulParse ("{throw e;}");
	testSuccessfulParse ("{throw new FooException(\"blargle\");}");
    }

    @Test
    public void testTry () {
	testSuccessfulParse ("{try { a = b; } finally { c = d; }}");
	testSuccessfulParse ("{try { a = b; } catch (AException a) { } finally { c = d; }}");
	testSuccessfulParse ("{try { a = b; } catch (A | B ex) { } finally { c = d; }}");

	testSuccessfulParse ("{try (Foo foo = new Foo()) { a = b; } }");
	testSuccessfulParse ("{try (Foo foo = new Foo(); Bar bar = new Bar(foo)) { a = b; } }");
    }

    @Test
    public void testAssert () {
	testSuccessfulParse ("{ assert foo; }");
	testSuccessfulParse ("{ assert foo : \"lorem...\"; }");
    }

    @Test
    public void testLambda () {
	testSuccessfulParse("{ btn.foo(e -> System.out.println(\"Hello World!\")); }");
	testSuccessfulParse("{ btn.foo((k, v) -> k - v); }");
	testSuccessfulParse("{ IntegerMath addition = (a, b) -> a + b; }");
	testSuccessfulParse("{ String s = invoke(() -> \"done\"); }");
	testSuccessfulParse("{ btn.foo((int i) -> i + 2); }");
	testSuccessfulParse("{ Consumer<Integer>  c = (int x) -> { System.out.println(x); }; }");
    }

    @Test
    public void testCast () {
	testSuccessfulParse ("{ int i = (int)j; }");
	testSuccessfulParse ("{ Foo foo = (Foo)bar; }");
	testSuccessfulParse ("{ foo = (foo.Bar)bar; }");
	testSuccessfulParse ("{ Baz foo = (foo.bar.Baz)bar; }");
	testSuccessfulParse ("{ foo = (Foo & bar.Baz)bar; }");
	testSuccessfulParse ("{ foo = (foo.Bar & Baz)bar; }");
	testSuccessfulParse ("{ foo = (foo.Bar)bar; }");
	testSuccessfulParse ("{ Foo<T> foo = (Foo<T>)bar; }");
	testSuccessfulParse ("{ Bar<T> foo = (foo.Bar<T>)bar; }");
	testSuccessfulParse ("{ Baz<T> foo = (foo.bar.Baz<T>)bar; }");
	testSuccessfulParse ("{ foo = (Foo<T> & Bar<T>)bar; }");
	testSuccessfulParse ("{ foo = (@Foo Bla)bar; }");
	testSuccessfulParse ("{ foo = (@Foo Bla<T>)bar; }");
	testSuccessfulParse ("{ foo = (int[])bar; }");
	testSuccessfulParse ("{ foo = (@Foo int[])bar; }");
	testSuccessfulParse ("{ foo = (int[][])bar; }");
	testSuccessfulParse ("{ foo = (Foo[])bar; }");
	testSuccessfulParse ("{ foo = (foo.Bar[])bar; }");
	testSuccessfulParse ("{ foo = (@Foo Bla<T>.bleh<S>)bar; }");
	testSuccessfulParse ("{ foo = (@Foo Bla<T>.@Bar bleh<S>)bar; }");
    }

    private void testSuccessfulParse (String s) {
	TestParseHelper.earleyParse (g, s, diagnostics);
	assert !diagnostics.hasError () : "Got parser errors: " + TestParseHelper.getParseOutput (diagnostics);
    }
}