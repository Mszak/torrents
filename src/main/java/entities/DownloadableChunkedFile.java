package entities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import config.BaseConfig;
import exceptions.ChunkAlreadyPossesedException;
import exceptions.ChunkNotFoundException;
import exceptions.FileNotChunkedException;

public class DownloadableChunkedFile implements ChunkedFile {
	
	private final String filename;
	private final int fileId;
	private final int chunksNumber;
	private final HashMap<Integer, Byte[]> chunks;
	
	public DownloadableChunkedFile(String filename, int fileId, int chunkNumber) {
		this.filename = filename;
		this.fileId = fileId;
		this.chunksNumber = chunkNumber;
		chunks = new HashMap<Integer, Byte[]>();
	}
	
	public void addChunk(int chunkId, Byte[] fileBytes) {
		if (chunkId >= chunksNumber) {
			throw new IndexOutOfBoundsException("Chunk out of bound exception: " + chunkId);
		}
		if (chunks.containsKey(chunkId)) {
			throw new ChunkAlreadyPossesedException("Chunk: " + chunkId);
		}
		chunks.put(chunkId, fileBytes);
	}
	
	public byte[] getChunk(int chunkId) {
		if (chunkId >= chunksNumber) {
			throw new IndexOutOfBoundsException("Chunk out of bound exception: " + chunkId);
		}
		if (chunks.containsKey(chunkId)) {
			throw new ChunkNotFoundException("Chunk: " + chunkId);
		}
		
		return ArrayUtils.toPrimitive(chunks.get(chunkId));
	}
	
	public List<Integer> getMissingChunks() { //TODO optimize due to (un)packing int to Integer
		List<Integer> missingChunks = new ArrayList<Integer>();
		
		for (int chunkId = 0; chunkId < chunksNumber; ++chunkId) {
			if (!chunks.containsKey(chunkId)) {
				missingChunks.add(chunkId);
			}
		}
		
		return missingChunks;
	}
	
	public boolean isFileFull() {
		for (int chunkId = 0; chunkId < chunksNumber; ++chunkId) {
			if (!chunks.containsKey(chunkId)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void saveFile() {
		if (!isFileFull()) {
			throw new FileNotChunkedException("Missing chunk");
		}
		
		try {
			Files.createFile(Paths.get(BaseConfig.DOWNLOAD_DIR + filename));
			FileOutputStream out = new FileOutputStream(BaseConfig.DOWNLOAD_DIR + filename, true);
			for (int chunkId = 0; chunkId < chunksNumber - 1; ++chunkId) {
				out.write(ArrayUtils.toPrimitive(chunks.get(chunkId)));
			}
			
			int lastByte = -1;
			Byte[] lastChunk = chunks.get(chunksNumber - 1);
			for (int i = BaseConfig.CHUNK_SIZE - 1; i >= 0; --i) {
				if (lastChunk[i] != 0) {
					lastByte = i + 1;
					break;
				}
			}
			for (int i = 0; i < lastByte; ++i) {
				out.write(lastChunk[i]);
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getFileId() {
		return fileId;
	}
	
	public String getFilename() {
		return filename;
	}

	@Override
	public List<Integer> getPossessedChunks() {
		List<Integer> possessedChunks = new ArrayList<Integer>();
		
		for (int chunkId = 0; chunkId < chunksNumber; ++chunkId) {
			if (chunks.containsKey(chunkId)) {
				possessedChunks.add(chunkId);
			}
		}
		
		return possessedChunks;
	}

	public boolean containsChunk(Integer chunkId) {
		return chunks.containsKey(chunkId);
	}

}
