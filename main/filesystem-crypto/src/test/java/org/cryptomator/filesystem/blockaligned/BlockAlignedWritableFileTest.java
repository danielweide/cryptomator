package org.cryptomator.filesystem.blockaligned;

import java.nio.ByteBuffer;

import org.cryptomator.filesystem.File;
import org.cryptomator.filesystem.FileSystem;
import org.cryptomator.filesystem.ReadableFile;
import org.cryptomator.filesystem.WritableFile;
import org.cryptomator.filesystem.inmem.InMemoryFileSystem;
import org.junit.Assert;
import org.junit.Test;

public class BlockAlignedWritableFileTest {

	@Test
	public void testWrite() {
		FileSystem fs = new InMemoryFileSystem();
		File file = fs.file("test");
		try (WritableFile w = file.openWritable()) {
			w.write(ByteBuffer.wrap(new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09}));
		}

		for (int i = 1; i < 12; i++) {
			testWrite(file, i);
		}
	}

	private void testWrite(File file, int blockSize) {
		try (WritableFile w = new BlockAlignedWritableFile(file.openWritable(), file.openReadable(), blockSize)) {
			w.position(4);
			w.write(ByteBuffer.wrap(new byte[] {0x11, 0x22, 0x33}));
		}

		try (ReadableFile r = file.openReadable()) {
			ByteBuffer buf = ByteBuffer.allocate(10);
			r.read(buf);
			buf.flip();
			Assert.assertArrayEquals(new byte[] {0x00, 0x01, 0x02, 0x03, 0x11, 0x22, 0x33, 0x07, 0x08, 0x09}, buf.array());
		}
	}

}
