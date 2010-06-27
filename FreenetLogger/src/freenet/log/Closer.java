package freenet.log;

import java.io.Closeable;
import java.io.IOException;

class Closer {

	static void close(Closeable c) {
		try {
			c.close();
		} catch (IOException e) {
		}
	}
}
