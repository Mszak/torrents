package entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import config.BaseConfig;

public class FileInfo {

	private final String fileName;
	private final int fileId;
	private final int chunksNumber;

	public FileInfo(String fileName, int fileId, int chunksNumber) {
		this.fileName = fileName;
		this.fileId = fileId;
		this.chunksNumber = chunksNumber;
	}

	public int getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}
	
	public int getChunksNumber() {
		return chunksNumber;
	}

	@Override
	public String toString() {
		return "File name: " + fileName + ", file id: " + fileId;
	}
	
	public static int getChunkNumbers(String file) throws IOException {
		return (int)Math.ceil((double)Files.size(Paths.get(file)) / BaseConfig.CHUNK_SIZE);
	}

}
