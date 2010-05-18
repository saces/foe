package freenet.log;

import java.io.Closeable;
import java.io.IOException;

public class Closer {

	public static void close(Closeable c) {
		try {
			c.close();
		} catch (IOException e) {
		}
	}
}
