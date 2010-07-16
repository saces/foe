package freenet.log;

import java.io.Closeable;
import java.io.IOException;

// This is here to avoid cyclic dependencies with FreenetSupport
class Closer {

	static void close(Closeable c) {
		try {
			c.close();
		} catch (IOException e) {
		}
	}
}
