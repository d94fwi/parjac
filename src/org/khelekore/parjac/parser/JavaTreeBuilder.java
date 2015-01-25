package org.khelekore.parjac.parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.khelekore.parjac.grammar.Grammar;
import org.khelekore.parjac.grammar.Rule;
import org.khelekore.parjac.grammar.RuleCollection;
import org.khelekore.parjac.lexer.Lexer;
import org.khelekore.parjac.lexer.Token;
import org.khelekore.parjac.tree.*;

public class JavaTreeBuilder {

    // Indexed by rule id
    private List<Builder> builders;

    public JavaTreeBuilder (Grammar g) {
	builders = new ArrayList<> (g.getNumberOfRules ());
	for (int i = 0; i < g.getNumberOfRules (); i++)
	    builders.add (null);

	// Productions from §3 (Lexical Structure)

	// Productions from §4 (Types, Values, and Variables)
	register (g, "PrimitiveType", constructored (PrimitiveType::new));
	register (g, "ClassType", constructored (ClassType::build));
	register (g, "SimpleClassType", constructored (SimpleClassType::new));
	register (g, "ArrayType", constructored (ArrayType::new));
	register (g, "Dims", constructored (Dims::build));
	register (g, "OneDim", constructored (OneDim::new));
	register (g, "TypeParameter", constructored (TypeParameter::new));
	register (g, "TypeBound", constructored (TypeBound::new));
	register (g, "AdditionalBound", constructored (AdditionalBound::new));
	register (g, "TypeArguments", constructored (TypeArguments::new));
	register (g, "TypeArgumentList", constructored (TypeArgumentList::new));
	register (g, "Wildcard", constructored (Wildcard::new));
	register (g, "WildcardBounds", constructored (WildcardBounds::new));

	// Productions from §6 Names
	register (g, "DottedName", constructored (DottedName::create));

	// Productions from §7 (Packages)
	register (g, "CompilationUnit", constructored (CompilationUnit::new));
	register (g, "PackageDeclaration", constructored (PackageDeclaration::new));
	register (g, "SingleTypeImportDeclaration", constructored (SingleTypeImportDeclaration::new));
	register (g, "TypeImportOnDemandDeclaration", constructored (TypeImportOnDemandDeclaration::new));
	register (g, "SingleStaticImportDeclaration", constructored (SingleStaticImportDeclaration::new));
	register (g, "StaticImportOnDemandDeclaration", constructored (StaticImportOnDemandDeclaration::new));

	// Productions from §8 (Classes)
	register (g, "NormalClassDeclaration", constructored (NormalClassDeclaration::new));
	register (g, "TypeParameters", constructored (TypeParameters::new));
	register (g, "InterfaceTypeList", constructored (InterfaceTypeList::new));
	register (g, "ClassBody", constructored (ClassBody::new));
	register (g, "FieldDeclaration", constructored (FieldDeclaration::new));
	register (g, "VariableDeclaratorList", constructored (VariableDeclaratorList::new));
	register (g, "VariableDeclarator", constructored (VariableDeclarator::new));
	register (g, "VariableDeclaratorId", constructored (VariableDeclaratorId::new));
	register (g, "UnannClassType", constructored (UnannClassType::build));
	register (g, "UnannArrayType", constructored (UnannArrayType::new));
	register (g, "MethodDeclaration", constructored (MethodDeclaration::new));
	register (g, "MethodHeader", constructored (MethodHeader::new));
	register (g, "UntypedMethodHeader", constructored (UntypedMethodHeader::new));
	register (g, "MethodDeclarator", constructored (MethodDeclarator::new));
	register (g, "FormalParameterList", constructored (FormalParameterList::new));
	register (g, "NormalFormalParameterList", constructored (NormalFormalParameterList::new));
	register (g, "FormalParameter", constructored (FormalParameter::new));
	register (g, "LastFormalParameter", constructored (LastFormalParameter::build));
	register (g, "ReceiverParameter", constructored (ReceiverParameter::new));
	register (g, "Throws", constructored (Throws::new));
	// ConstructorDeclaration
	// ConstructorDeclarator
	// ConstructorBody
	// ExplicitConstructorInvocation
	// ConstructorArguments
	register (g, "EnumDeclaration", constructored (EnumDeclaration::new));
	register (g, "EnumBody", constructored (EnumBody::new));
	register (g, "EnumConstantList", constructored (EnumConstantList::new));
	register (g, "EnumConstant", constructored (EnumConstant::new));
	register (g, "EnumBodyDeclarations", constructored (EnumBodyDeclarations::new));

	// Productions from §9 (Interfaces)
	// NormalInterfaceDeclaration
	// ExtendsInterfaces
	// InterfaceBody
	// ConstantDeclaration
	// InterfaceMethodDeclaration
	// AnnotationTypeDeclaration
	// AnnotationTypeBody
	// AnnotationTypeElementDeclaration
	// DefaultValue
	register (g, "NormalAnnotation", constructored (NormalAnnotation::new));
	register (g, "ElementValuePairList", constructored (ElementValuePairList::new));
	register (g, "ElementValuePair", constructored (ElementValuePair::new));
	register (g, "ElementValueArrayInitializer", constructored (ElementValueArrayInitializer::new));
	register (g, "ElementValueList", constructored (ElementValueList::new));
	register (g, "MarkerAnnotation", constructored (MarkerAnnotation::new));
	register (g, "SingleElementAnnotation", constructored (SingleElementAnnotation::new));

	// Productions from §10 (Arrays)
	register (g, "ArrayInitializer", constructored (ArrayInitializer::new));
	register (g, "VariableInitializerList", constructored (VariableInitializerList::new));

	// Productions from §14 (Blocks and Statements)
	register (g, "Block", constructored (Block::new));
	register (g, "BlockStatements", constructored (BlockStatements::new));
	register (g, "LocalVariableDeclarationStatement", constructored (LocalVariableDeclarationStatement::new));
	register (g, "LocalVariableDeclaration", constructored (LocalVariableDeclaration::new));
	register (g, "EmptyStatement", constructored (EmptyStatement::build));
	register (g, "LabeledStatement", constructored (LabeledStatement::new));
	register (g, "LabeledStatementNoShortIf", constructored (LabeledStatement::new));
	register (g, "ExpressionStatement", constructored (ExpressionStatement::new));
	register (g, "IfThenStatement", constructored (IfThenStatement::new));
	register (g, "IfThenElseStatement", constructored (IfThenStatement::new));
	register (g, "IfThenElseStatementNoShortIf", constructored (IfThenStatement::new));
	register (g, "AssertStatement", constructored (AssertStatement::new));
	register (g, "SwitchStatement", constructored (SwitchStatement::new));
	register (g, "SwitchBlock", constructored (SwitchBlock::new));
	register (g, "SwitchBlockStatementGroup", constructored (SwitchBlockStatementGroup::new));
	register (g, "SwitchLabel", constructored (SwitchLabel::build));
	register (g, "WhileStatement", constructored (WhileStatement::new));
	register (g, "WhileStatementNoShortIf", constructored (WhileStatement::new));
	register (g, "DoStatement", constructored (DoStatement::new));
	register (g, "BasicForStatement", constructored (BasicForStatement::new));
	register (g, "BasicForStatementNoShortIf", constructored (BasicForStatement::new));
	register (g, "StatementExpressionList", constructored (StatementExpressionList::new));
	register (g, "EnhancedForStatement", constructored (EnhancedForStatement::new));
	register (g, "EnhancedForStatementNoShortIf", constructored (EnhancedForStatement::new));
	register (g, "BreakStatement", constructored (BreakStatement::new));
	register (g, "ContinueStatement", constructored (ContinueStatement::new));
	register (g, "ReturnStatement", constructored (ReturnStatement::new));
	register (g, "ThrowStatement", constructored (ThrowStatement::new));
	register (g, "SynchronizedStatement", constructored (SynchronizedStatement::new));
	register (g, "TryStatement", constructored (TryStatement::new));
	register (g, "Catches", constructored (Catches::new));
	register (g, "CatchClause", constructored (CatchClause::new));
	register (g, "CatchFormalParameter", constructored (CatchFormalParameter::new));
	register (g, "CatchType", constructored (CatchType::new));
	register (g, "ExtraCatchType", constructored (ExtraCatchType::new));
	register (g, "Finally", constructored (Finally::new));
	register (g, "ResourceList", constructored (ResourceList::new));
	register (g, "Resource", constructored (Resource::new));

	// Productions from §15 (Expressions)
	// PrimaryNoNewArray
	register (g, "ClassInstanceCreationExpression", constructored (ClassInstanceCreationExpression::build));
	register (g, "UntypedClassInstanceCreationExpression",
		  constructored (UntypedClassInstanceCreationExpression::new));
	register (g, "ClassInstanceName", constructored (ClassInstanceName::new));
	register (g, "ExtraName", constructored (ExtraName::new));
	register (g, "Diamond", constructored (Diamond::build));
	register (g, "FieldAccess", constructored (FieldAccess::new));
	register (g, "ArrayAccess", constructored (ArrayAccess::new));
	register (g, "MethodInvocation", constructored (MethodInvocation::build));
	register (g, "UntypedMethodInvocation", constructored (UntypedMethodInvocation::new));
	register (g, "ArgumentList", constructored (ArgumentList::new));
	// MethodReference
	register (g, "ArrayCreationExpression", constructored (ArrayCreationExpression::new));
	register (g, "DimExprs", constructored (DimExprs::new));
	register (g, "DimExpr", constructored (DimExpr::new));
	register (g, "LambdaExpression", constructored (LambdaExpression::new));
	register (g, "LambdaParameters", constructored (LambdaParameters::new));
	register (g, "InferredFormalParameterList", constructored (InferredFormalParameterList::new));
	register (g, "Assignment", constructored (Assignment::new));
	register (g, "ConditionalExpression", constructored (TernaryExpression::build));
	register (g, "ConditionalOrExpression", constructored (TwoPartExpression::build));
	register (g, "ConditionalAndExpression", constructored (TwoPartExpression::build));
	register (g, "InclusiveOrExpression", constructored (TwoPartExpression::build));
	register (g, "ExclusiveOrExpression", constructored (TwoPartExpression::build));
	register (g, "AndExpression", constructored (TwoPartExpression::build));
	register (g, "EqualityExpression", constructored (TwoPartExpression::build));
	register (g, "RelationalExpression", constructored (TwoPartExpression::build));
	register (g, "ShiftExpression", constructored (TwoPartExpression::build));
	register (g, "ShiftOp", constructored (ShiftOp::create));
	register (g, "AdditiveExpression", constructored (TwoPartExpression::build));
	register (g, "MultiplicativeExpression", constructored (TwoPartExpression::build));
	register (g, "UnaryExpression", constructored (UnaryExpression::build));
	register (g, "PreIncrementExpression", constructored (PreIncrementExpression::new));
	register (g, "PreDecrementExpression", constructored (PreDecrementExpression::new));
	register (g, "UnaryExpressionNotPlusMinus", constructored (UnaryExpression::build));
	register (g, "PostIncrementExpression", constructored (PostIncrementExpression::new));
	register (g, "PostDecrementExpression", constructored (PostDecrementExpression::new));
	register (g, "CastExpression", constructored (CastExpression::new));
    }

    private void register (Grammar g, String rulename, Builder b) {
	RuleCollection rc = g.getRules (rulename);
	for (Rule r : rc.getRules ())
	    builders.set (r.getId (), b);
    }

    /** Constructor taking one argument */
    public interface ConstructorOne<T> {
	TreeNode create (T arg);
    }

    /** Constructor taking two argument */
    public interface ConstructorTwo<T, U> {
	TreeNode create (T t, U u);
    }

    /** Given a rule and a deque of parts pop some parts and push the newly built part. */
    private interface Builder {
	void build (Rule r, Deque<TreeNode> parts);
    }

    private static Builder constructored (ConstructorOne<Deque<TreeNode>> cb) {
	return new Builder () {
	    public void build (Rule r, Deque<TreeNode> parts) {
		parts.push (cb.create (parts));
	    }
	};
    }

    private static Builder constructored (ConstructorTwo<Rule, Deque<TreeNode>> cb) {
	return new Builder () {
	    public void build (Rule r, Deque<TreeNode> parts) {
		parts.push (cb.create (r, parts));
	    }
	};
    }

    public TreeNode getTokenValue (Lexer lexer, Token token) {
	if (token.isOperator ())
	    return new OperatorTokenType (token);
	else if (token.isPrimitive ())
	    return new PrimitiveTokenType (token);
	else if (token.isModifier ())
	    return new ModifierTokenType (token);
	else if (!token.hasValue ())
	    return null;
	switch (token) {
	case INT_LITERAL:
	    return new IntLiteral (lexer.getIntValue ());
	case LONG_LITERAL:
	    return new LongLiteral (lexer.getLongValue ());
	case FLOAT_LITERAL:
	    return new FloatLiteral (lexer.getFloatValue ());
	case DOUBLE_LITERAL:
	    return new DoubleLiteral (lexer.getDoubleValue ());
	case CHARACTER_LITERAL:
	    return new CharLiteral (lexer.getCharValue ());
	case STRING_LITERAL:
	    return new StringLiteral (lexer.getStringValue ());
	case TRUE:
	    return BooleanLiteral.TRUE_VALUE;
	case FALSE:
	    return BooleanLiteral.FALSE_VALUE;
	case IDENTIFIER:
	    return new Identifier (lexer.getIdentifier ());
	case NULL:
	    return NullLiteral.NULL;
	default:
	    throw new IllegalStateException ("Do not know how to get value from:" + token);
	}
    }

    public void build (State start, Deque<TreeNode> parts) {
	Rule rule = start.getRule ();
	Builder b = builders.get (rule.getId ());
	if (b != null) {
	    b.build (rule, parts);
	} else if (rule.getName ().startsWith ("ZOM_")) {
	    buildZOM (rule, parts);
	}
    }

    private void buildZOM (Rule r, Deque<TreeNode> parts) {
	ZOMEntry z;
	String name = r.getName ();
	if (r.size () > 1 && r.getRulePart (0).getId () == name) {
	    z = (ZOMEntry)parts.pop ();
	} else {
	    z = new ZOMEntry (r.getRulePart (0).getId ().toString ());
	}
	// We should not have ',' or similar in the parts so no need to pop those
	// TODO: this will break for some "ZOM_34 -> [ZOM_34, [, ]]"
	z.add (parts.pop ());
	parts.push (z);
    }
}