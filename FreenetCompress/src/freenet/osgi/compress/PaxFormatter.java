package freenet.osgi.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;

import freenet.osgi.support.io.FileUtil;

public class PaxFormatter {

	private final TarArchiveOutputStream _out;

	private long paxcounter = 0;

	public PaxFormatter(TarArchiveOutputStream out) {
		_out = out;
	}

	private synchronized long getPaxCounter() {
		return paxcounter++;
	}

	public void addFile(String name, File file) throws IOException {
		if (name.startsWith("/")) {
			throw new Error("name begins with / : "+name);
		}

		TarArchiveEntry tae;

		if (TarUtils.needsPaxingName(name)) {
			String paxSuffix = Long.toString(getPaxCounter());
			StringBuilder sb = new StringBuilder(1024);
			long size = TarUtils.paxFormatter(sb, "path", name);
			if (TarUtils.needsPaxingSize(file.length())) {
				size += TarUtils.paxFormatter(sb, "size", file.length());
			}
			byte[] bb = sb.toString().getBytes();
			tae = new TarArchiveEntry("././@PaxHeader-" + paxSuffix,
					TarConstants.LF_PAX_EXTENDED_HEADER_LC);
			tae.setSize(bb.length);
			_out.putArchiveEntry(tae);
			_out.write(bb);
			_out.closeArchiveEntry();
			tae = new TarArchiveEntry(file, "././@PaxItem-" + paxSuffix);
			tae.setMode(0);
		} else {
			tae = new TarArchiveEntry(file, name);
			tae.setMode(0);
		}
		_out.putArchiveEntry(tae);
		FileUtil.copy(new FileInputStream(file), _out, -1);
		_out.closeArchiveEntry();
	}

	public void addItem(String name, InputStream is, long length) throws IOException {
		if (name.startsWith("/")) {
			new Error("name begins with / : "+name).printStackTrace();
		}

		TarArchiveEntry tae;

		if (TarUtils.needsPaxingName(name)) {
			String paxSuffix = Long.toString(getPaxCounter());
			if (name.endsWith("/")) {
				paxSuffix = paxSuffix.concat("/");
			}
			StringBuilder sb = new StringBuilder(1024);
			long size = TarUtils.paxFormatter(sb, "path", name);
			if (TarUtils.needsPaxingSize(length)) {
				size += TarUtils.paxFormatter(sb, "size", length);
			}
			byte[] bb = sb.toString().getBytes();
			tae = new TarArchiveEntry("././@PaxHeader-" + paxSuffix,
					TarConstants.LF_PAX_EXTENDED_HEADER_LC);
			tae.setSize(bb.length);
			tae.setModTime(0);
			_out.putArchiveEntry(tae);
			_out.write(bb);
			_out.closeArchiveEntry();
			tae = new TarArchiveEntry("././@PaxItem-" + paxSuffix);
			tae.setModTime(0);
		} else {
			tae = new TarArchiveEntry(name);
			tae.setModTime(0);
		}
		tae.setSize(length);
		_out.putArchiveEntry(tae);
		FileUtil.copy(is, _out, length);
		_out.closeArchiveEntry();
	}

}
