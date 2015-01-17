package entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

}
