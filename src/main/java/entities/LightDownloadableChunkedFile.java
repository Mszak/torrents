package entities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import config.BaseConfig;
import exceptions.ChunkNotFoundException;

public class LightDownloadableChunkedFile implements ChunkedFile {

	private static enum ChunkStatus {
		EMPTY, DOWNLOAD_IN_PROGRESS, DOWNLOADED
	}
	
	private final String filename;
	private final Path pathFile;
	private final int fileId;
	private final int chunksNumber;
	
	private ChunkStatus chunkStatuses[];
	
	public LightDownloadableChunkedFile(String filename, int fileId, int chunksNumber) throws IOException {
		this.filename = filename;
		this.fileId = fileId;
		this.chunksNumber = chunksNumber;
		
		pathFile = Paths.get(BaseConfig.DOWNLOAD_DIR + filename);
		Files.createFile(Paths.get(BaseConfig.DOWNLOAD_DIR + filename));
		
		chunkStatuses = new ChunkStatus[chunksNumber];
		Arrays.fill(chunkStatuses, ChunkStatus.EMPTY);
	}
	
	/**
	 * Dopisuje do pliku chunk (fileBytes) o numerze chunkId
	 * @param chunkId
	 * @param fileBytes
	 */
	synchronized public void addChunk(int chunkId, Byte[] fileBytes) {
		if (chunkStatuses[chunkId] == ChunkStatus.DOWNLOAD_IN_PROGRESS) {
			chunkStatuses[chunkId] = ChunkStatus.DOWNLOADED;
			try {
				RandomAccessFile raf = new RandomAccessFile(pathFile.toFile(), "rw");
				raf.seek(chunkId * BaseConfig.CHUNK_SIZE);
				raf.write(ArrayUtils.toPrimitive(fileBytes));
				raf.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Rezerwuje chunk o numerze chunkId do zapisu
	 * @param chunkId
	 * @return
	 */
	synchronized public boolean reserveChunkToDownload(int chunkId) {
		if (chunkStatuses[chunkId] == ChunkStatus.EMPTY) {
			chunkStatuses[chunkId] = ChunkStatus.DOWNLOAD_IN_PROGRESS;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Zwalnia rezerwację do zapisu dla chunku o numerze chunkId
	 * @param chunkId
	 */
	public void unreserveChunk(int chunkId) {
		chunkStatuses[chunkId] = ChunkStatus.EMPTY;
	}
	
	/**
	 * Zwraca tablicę bajtów odpowiadającą chunkowi o numerze chunkId
	 */
	@Override
	public byte[] getChunk(int chunkId) {
		if (chunkStatuses[chunkId] == ChunkStatus.DOWNLOADED) {
			RandomAccessFile raf;
			try {
				raf = new RandomAccessFile(pathFile.toFile(), "r");
				raf.seek(chunkId * BaseConfig.CHUNK_SIZE);
				int fileSizeLeft = (int)(raf.length() - chunkId * BaseConfig.CHUNK_SIZE);
				byte[] result = new byte[Math.min(BaseConfig.CHUNK_SIZE, fileSizeLeft)];
				raf.close();
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		throw new ChunkNotFoundException("File: " + filename + ", chunkId: " + chunkId);
	}

	/**
	 * Zwraca listę numerów posiadanych chunków
	 */
	@Override
	public List<Integer> getPossessedChunks() {
		List<Integer> possessedChunks = new ArrayList<Integer>();
		
		for (int chunkId = 0; chunkId < chunksNumber; ++chunkId) {
			if (chunkStatuses[chunkId] == ChunkStatus.DOWNLOADED) {
				possessedChunks.add(chunkId);
			}
		}
		
		return possessedChunks;
	}

	/**
	 * Zwraca id pliku
	 */
	@Override
	public int getFileId() {
		return fileId;
	}
	
	/**
	 * Zwraca nazwę pliku
	 * @return
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Sprawdza czy plik został pobrany w całości
	 * @return
	 */
	public boolean isFileFull() {
		for (ChunkStatus c : chunkStatuses) {
			if (c != ChunkStatus.DOWNLOADED) {
				return false;
			}
		}
		
		return true;
	}
}
