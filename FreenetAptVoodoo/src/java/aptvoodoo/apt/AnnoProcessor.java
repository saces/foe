package aptvoodoo.apt;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.DumpVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;

import freenet.log.annotation.FreenetLogged;

public class AnnoProcessor implements AnnotationProcessor {

	private AnnotationProcessorEnvironment environment;

	private AnnotationTypeDeclaration loggerDeclaration;

	private final HashSet<String> converted = new HashSet<String>();

	public AnnoProcessor(AnnotationProcessorEnvironment env) {
		environment = env;
		loggerDeclaration = (AnnotationTypeDeclaration) environment
				.getTypeDeclaration("aptvoodoo.FreenetLogged");
	}

	public void process() {
		// Note here we use a helper method to create a declaration scanner for our 
		// visitor, and a no-op visitor.
		DeclarationVisitor scanner = DeclarationVisitors
			.getSourceOrderDeclarationScanner(new AllDeclarationsVisitor(),
				DeclarationVisitors.NO_OP);
		Collection<Declaration> log_declarations = environment.getDeclarationsAnnotatedWith(loggerDeclaration);
		for (Declaration declaration : log_declarations) {
			declaration.accept(scanner);
		}
	}

	private class AllDeclarationsVisitor extends SimpleDeclarationVisitor {
		@Override
		public void visitClassDeclaration(ClassDeclaration arg0) {
			System.out.println("VisitClass: "+arg0.getSimpleName());
			if (arg0.getAnnotation(FreenetLogged.class) != null) {
				//System.out.println("Do it");
				
				rewriteFile(arg0);
			}
			
			//Collection<FieldDeclaration> fields = arg0.getFields();
			//System.out.println("Fields:");
			//for (FieldDeclaration field: fields) {
				//System.out.println("Field: "+field.getSimpleName());
			//}
			//System.out.println("VisitClass End");
			//visitDeclaration(arg0);
		}

		private void rewriteFile(ClassDeclaration arg0) {
			File srcFile = arg0.getPosition().file();
			String fileName = srcFile.getAbsolutePath();
			if (converted.contains(fileName)) {
				System.out.println("Already converted: "+fileName);
				return;
			}
			converted.add(fileName);
			System.out.println("Converting: "+fileName);
			try {
				String result = innerRewriteFile(srcFile);
				String[] t = srcFile.getName().split("\\.");
				String targetName = ""+ arg0.getPackage().getQualifiedName() + "." + t[0];
				PrintWriter pw = environment.getFiler().createSourceFile(targetName);
				pw.print(result);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private String innerRewriteFile(File srcFile) throws ParseException, IOException {
			CompilationUnit cu = JavaParser.parse(srcFile);
			
			// mangle imports
			List<ImportDeclaration> imports = cu.getImports();
			List<ImportDeclaration> newImports = new ArrayList<ImportDeclaration>();

//			for (ImportDeclaration impo: imports) {
//				System.out.println("Imp name: "+impo.getName());
//				System.out.println("Imp name: "+impo.getName().toString());
//			}

			Iterator<ImportDeclaration> iter = imports.iterator();
			boolean isOK = false;
			while (iter.hasNext()) {
				if ("aptvoodoo.FreenetLogged".equals(iter.next().getName().toString())) {
					isOK = true;
					iter.remove();
					break;
				}
			}
			if (!isOK) environment.getMessager().printError("Import 'aptvoodoo.FreenetLogged' not found.");

			isOK = false;
			for (ImportDeclaration impo: imports) {
				newImports.add(impo);
				if ("apttest.log.TestLogger".equals(impo.getName().toString())) {
					newImports.add(new ImportDeclaration(new NameExpr("apttest.log.TestLogger.LogLevel"), false, false));
					isOK = true;
				}
			}
			if (!isOK) {
				environment.getMessager().printError("Import 'apttest.log.TestLogger' not found.");
			}

			cu.setImports(newImports);

			// rewrite logging
			Besucher b = new Besucher();
			cu.accept(b, null);

			// spit out rewritten source
			DumpVisitor dv = new DumpVisitor();
			cu.accept(dv, null);

			return dv.getSource();
		}
	}
}
