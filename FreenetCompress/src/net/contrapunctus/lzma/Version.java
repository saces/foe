package net.contrapunctus.lzma;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
public class Version {
public static final int major = 0;
public static final int minor = 95;
public static final String context = 
"\nContext:\n\n[version bump to 0.95\nChristopher League <league@contrapunctus.net>**20090312181654] \n\n[Better program for testing compatibility with lzma(1)\nChristopher League <league@contrapunctus.net>**20090312181456] \n\n[flag for compatibility with lzma(1) tool\nChristopher League <league@contrapunctus.net>**20090312175815\n \n The default is now to be compatible with the lzma(1) command-line\n tool. Note that this is the single-file processor (like gzip) not the\n multi-file archive tool (like zip). To retain compatibility with\n previous version, or to save a few bytes, set\n LzmaOutputStream.LZMA_HEADER = false;\n \n] \n\n[TAG 0.94\nChristopher League <league@contrapunctus.net>**20090125202402] \n";public static void main( String[] args ) {
  if( args.length > 0 ) System.out.println(context);
  else System.out.printf("lzmajio-%d.%d%n", major, minor);
  }
}
