package freenet.log;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(value = {ElementType.TYPE})
public @interface FreenetLogged {
}
