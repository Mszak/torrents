package entities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import config.BaseConfig;

public class UploadableChunkedFile {
	
	private final Path pathToFile;
	private final int chunksNumber;
	private final int fileId;

	public UploadableChunkedFile(String sPathToFile, int fileId) throws IOException {
		this.fileId = fileId;
		pathToFile = Paths.get(sPathToFile);
		if (Files.exists(pathToFile)) {
			throw new FileNotFoundException();
		}
		
		chunksNumber = (int)Math.ceil((double)Files.size(pathToFile) / BaseConfig.CHUNK_SIZE);
	}
	
	public byte[] getChunk(int chunkId) {
		if (chunkId < chunksNumber) {
			throw new IndexOutOfBoundsException("Chunk to big: " + chunkId);
		}
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(pathToFile.toFile(), "r");
			raf.seek(chunkId * BaseConfig.CHUNK_SIZE);
			byte[] result = new byte[BaseConfig.CHUNK_SIZE];
			raf.read(result, 0, BaseConfig.CHUNK_SIZE);
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getFileId() {
		return fileId;
	}

}
