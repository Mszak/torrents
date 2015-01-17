package entities;

import java.util.List;

public interface ChunkedFile {
	public byte[] getChunk(int chunkId);
	
	public List<Integer> getPossessedChunks();
	
	public int getFileId();
}
