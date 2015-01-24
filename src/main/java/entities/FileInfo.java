package entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import config.BaseConfig;

public class FileInfo {

	private final String fileName;
	private final int fileId;

	public FileInfo(String fileName, int fileId) {
		this.fileName = fileName;
		this.fileId = fileId;
	}

	public int getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "File name: " + fileName + ", file id: " + fileId;
	}
	
	public static int getChunkNumbers(String file) throws IOException {
		return (int)Math.ceil((double)Files.size(Paths.get(file)) / BaseConfig.CHUNK_SIZE);
	}
	
	public static String generateFileSha1(String file) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		InputStream fis = new FileInputStream(new File(file));
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1) {
	        n = fis.read(buffer);
	        if (n > 0) {
	            digest.update(buffer, 0, n);
	        }
	    }
		fis.close();
		return sha1BytesToString(digest.digest());
	}
	
	private static String sha1BytesToString(byte[] b) {
		String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}

}
