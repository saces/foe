package aptvoodoo.apt;

import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.visitor.ModifierVisitorAdapter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class Besucher extends ModifierVisitorAdapter<Object> {

	@Override
	public Node visit(ClassExpr n, Object arg) {
		System.out.println("Visit ClassExpr: "+n);
		return super.visit(n, arg);
	}

	@Override
	public Node visit(ClassOrInterfaceDeclaration n, Object arg) {
		System.out.println("Visit ClassOrInterfaceDeclaration: "+n.getName());
		List<BodyDeclaration> members = n.getMembers();
//		for (int i = 0; i < members.size(); i++) {
//			System.out.println("BBB: "+members.get(i).getClass());
//		}
		boolean found = false;
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) instanceof InitializerDeclaration) {
				found = true;
				break;
			} else {
				//members.set(i, (BodyDeclaration) members.get(i).accept(this, arg));
			}
		}
		if (!found) {
			//System.out.println("Add static {}");
			List<Statement> l = new ArrayList<Statement>();
			//l.add(new EmptyStmt());
			members.add(new InitializerDeclaration(true, new BlockStmt(l)));
			n.setMembers(members);
		}

		FieldDeclaration fd = new FieldDeclaration();
		fd.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.VOLATILE);
		fd.setType(new PrimitiveType(Primitive.Boolean));

		List<VariableDeclarator> variables = new ArrayList<VariableDeclarator>();
		variables.add(new VariableDeclarator(new VariableDeclaratorId("logERROR")));
		variables.add(new VariableDeclarator(new VariableDeclaratorId("logWARN")));
		variables.add(new VariableDeclarator(new VariableDeclaratorId("logINFO")));
		variables.add(new VariableDeclarator(new VariableDeclaratorId("logDEBUG")));
		fd.setVariables(variables);

		members.add(0, fd);

		n.setMembers(members);

		//System.out.println("Visit ClassOrInterfaceDeclaration: "+n);
		//System.out.println("Visit ClassOrInterfaceDeclaration: "+n.getName());
		arg = n.getName();
		return super.visit(n, arg);
		//return n;
	}

//	@Override
//	public Node visit(ClassOrInterfaceType n, Object arg) {
//		//System.out.println("Visit ClassOrInterfaceType: "+n);
//		return super.visit(n, arg);
//	}
//
//	@Override
//	public Node visit(TypeDeclarationStmt n, Object arg) {
//		System.out.println("Visit TypeDeclarationStmt: "+n);
//		return super.visit(n, arg);
//	}
//
//	@Override
//	public Node visit(AnnotationDeclaration n, Object arg) {
//		System.out.println("Visit AnnotationDeclaration: "+n);
//		return super.visit(n, arg);
//	}
//
//	@Override
//	public Node visit(AnnotationMemberDeclaration n, Object arg) {
//		System.out.println("Visit AnnotationMemberDeclaration: "+n);
//		return super.visit(n, arg);
//	}
//
//	@Override
//	public Node visit(TypeParameter n, Object arg) {
//		System.out.println("Visit TypeParameter: "+n);
//		return super.visit(n, arg);
//	}

	@Override
	public Node visit(MarkerAnnotationExpr n, Object arg) {
		if ("FreenetLogged".equals(n.getName().toString())) {
			System.out.println("Supressed: "+n);
			return null;
		}
		//System.out.println("Visit MarkerAnnotationExpr: "+n);
		//System.out.println("Visit MarkerAnnotationExpr: "+n.getName());
		return n;
	}

//	@Override
//	public Node visit(NormalAnnotationExpr n, Object arg) {
//		System.out.println("Visit NormalAnnotationExpr: "+n);
//		return super.visit(n, arg);
//	}
//
//	@Override
//	public Node visit(SingleMemberAnnotationExpr n, Object arg) {
//		System.out.println("Visit SingleMemberAnnotationExpr: "+n);
//		return super.visit(n, arg);
//	}

//	@Override
//	public Node visit(BlockStmt n, Object arg) {
//		System.out.println("Visit BlockStmt: "+n);
//		List<Statement> stmts = n.getStmts();
//		List<Statement> sl = new ArrayList<Statement>();
//		if (stmts != null) {
//			for (int i = 0; i < stmts.size(); i++) {
//				Statement s = stmts.get(i);
//				if ((s instanceof ExpressionStmt)) {
//					System.out.println(stmts.get(i).getClass());
//				}
//			}
//		}
//		System.out.println("Visit BlockStmt END");
//		//n.setStmts(sl);
//		//return n;
//		return super.visit(n, arg);
//	}

	@Override
	public Node visit(ExpressionStmt n, Object arg) {
		Expression ex = n.getExpression();
		if (ex instanceof MethodCallExpr) {
			MethodCallExpr mex = (MethodCallExpr) ex;
			if ((mex.getScope() != null) && ("TestLogger".equals(mex.getScope().toString()))) {
				IfStmt ifs = new IfStmt();

				String name = mex.getName();
				
				if ("error".equals(name)) {
					ifs.setCondition(new NameExpr("logERROR"));
				} else if ("warn".equals(name)) {
					ifs.setCondition(new NameExpr("logWARN"));
				} else if ("info".equals(name)) {
					ifs.setCondition(new NameExpr("logINFO"));
				} else if ("debug".equals(name)) {
					ifs.setCondition(new NameExpr("logDEBUG"));
				} else {
					return super.visit(n, arg);
				}
				ifs.setThenStmt(new ExpressionStmt(mex));
				return ifs;
//				System.out.println("AAAmex: "+mex.getName());
//				System.out.println("AAAmex: "+mex.getName());
//				//System.out.println("AAAmex: "+mex.getScope().getClass());
//				System.out.println("AAAmex: "+mex.getScope());
//				System.out.println("AAAmex: "+mex.getArgs());
			}
		}
		System.out.println("Visit ExpressionStmt: "+n);
		// TODO Auto-generated method stub
		return super.visit(n, arg);
	}
	
	@Override
	public Node visit(InitializerDeclaration n, Object arg) {
		System.out.println("Visit InitializerDeclaration: "+n);
		BlockStmt block = (BlockStmt) n.getBlock().accept(this, arg);
		List<Statement> stmts = block.getStmts();
		if (stmts != null) {
			for( Statement stmt: stmts) {
				System.out.println("AAA: "+stmt.getClass());
				System.out.println("AAA: "+stmt);
				if (stmt instanceof ExpressionStmt) {
					Expression ex = ((ExpressionStmt) stmt).getExpression();
					System.out.println("AAAex: "+ex.getClass());
					System.out.println("AAAex: "+ex);
					if (ex instanceof MethodCallExpr) {
						MethodCallExpr mex = (MethodCallExpr) ex;
						System.out.println("AAAmex: "+mex.getName());
						System.out.println("AAAmex: "+mex.getScope().getClass());
						System.out.println("AAAmex: "+mex.getScope());
						System.out.println("AAAmex: "+mex.getArgs());
						List<Expression> args = mex.getArgs();
						for (Expression e: args) {
							System.out.println("AAAmexArg: "+e.getClass());
							System.out.println("AAAmexArg: "+e);
						}
					}
				}
			}
		} else {
			stmts = new ArrayList<Statement>();
			
		}
		List<Expression> args = new ArrayList<Expression>();
		args.add(new NameExpr((String)arg+".class"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("TestLogger"), "registerClass", args)));
		block.setStmts(stmts);
		n.setBlock(block);
		//return super.visit(n, arg);
		return n;
	}

	@Override
	public Node visit(IfStmt n, Object arg) {
//		System.out.println("IF: "+n);
//		System.out.println("IF: "+n.getCondition().getClass());
//		System.out.println("IF: "+n.getCondition());
		
		// TODO Auto-generated method stub
		return super.visit(n, arg);
	}
}
