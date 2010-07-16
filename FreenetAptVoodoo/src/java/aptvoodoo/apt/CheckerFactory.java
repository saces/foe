package aptvoodoo.apt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class CheckerFactory implements AnnotationProcessorFactory {

	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> declarations,
			AnnotationProcessorEnvironment env) {
		if(declarations.isEmpty()) {
			return AnnotationProcessors.NO_OP;
		} else {
			return new AnnoChecker(env);
		}
	}

	public Collection<String> supportedAnnotationTypes() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("freenet.log.FreenetLogged");
		return al;
	}

	public Collection<String> supportedOptions() {
		return Collections.emptyList();
	}

}
