package aptvoodoo.apt;

import java.util.Collection;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;

import freenet.log.annotation.FreenetLogged;

public class AnnoChecker implements AnnotationProcessor {

	private AnnotationProcessorEnvironment environment;

	private AnnotationTypeDeclaration loggerDeclaration;

	//private DeclarationVisitor declarationVisitor;

	public AnnoChecker(AnnotationProcessorEnvironment env) {
		environment = env;
		loggerDeclaration = (AnnotationTypeDeclaration) environment
				.getTypeDeclaration("aptvoodoo.FreenetLogged");
		//declarationVisitor = new AllDeclarationsVisitor();
	}

	public void process() {

		//Collection<TypeDeclaration> declarations = environment.getTypeDeclarations();
		// Note here we use a helper method to create a declaration scanner for our 
		// visitor, and a no-op visitor.
		DeclarationVisitor scanner = DeclarationVisitors
			.getSourceOrderDeclarationScanner(new AllDeclarationsVisitor(),
				DeclarationVisitors.NO_OP);
		Collection<Declaration> log_declarations = environment.getDeclarationsAnnotatedWith(loggerDeclaration);
		for (Declaration declaration : log_declarations) {
			declaration.accept(scanner);
		}

//		for (TypeDeclaration declaration : declarations) {
//			declaration.accept(scanner); // invokes the processing on the scanner.
//		}
	}

	private class AllDeclarationsVisitor extends SimpleDeclarationVisitor {
		@Override
		public void visitClassDeclaration(ClassDeclaration arg0) {
			System.out.println("VisitClass: "+arg0.getSimpleName());
			if (arg0.getAnnotation(FreenetLogged.class) != null) {
				//System.out.println("Do it");
				Collection<FieldDeclaration> fields = arg0.getFields();
				System.out.println("Fields:");
				for (FieldDeclaration field: fields) {
					String name = field.getSimpleName();
					boolean isOK = true;
					if ("logERROR".equals(name)) {
						isOK = false;
					} else if ("logWARN".equals(name)) {
						isOK = false;
					} else if ("logINFO".equals(name)) {
						isOK = false;
					} else if ("logDEBUG".equals(name)) {
						isOK = false;
					}
					if (!isOK)
						environment.getMessager().printError(field.getPosition(), "Invalid field Name: A class tagged with @FreenetLogged can not have such a field.");
					
					//System.out.println("Field: "+field.getSimpleName());
				}
				//System.out.println("VisitClass End");
				//visitDeclaration(arg0);
				//rewriteFile2(arg0);
			}
			
			//Collection<FieldDeclaration> fields = arg0.getFields();
			//System.out.println("Fields:");
			//for (FieldDeclaration field: fields) {
				//System.out.println("Field: "+field.getSimpleName());
			//}
			//System.out.println("VisitClass End");
			//visitDeclaration(arg0);
		}
	}
}
