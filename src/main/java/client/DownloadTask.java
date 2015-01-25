package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import config.BaseConfig;
import entities.LightDownloadableChunkedFile;
import entities.Peer;

public class DownloadTask extends Thread {
	
	private final int chunkId;
	private final LightDownloadableChunkedFile file;
	private final Peer peer;
	
	public DownloadTask(LightDownloadableChunkedFile file, Peer peer, int chunkId) {
		this.file = file;
		this.peer = peer;
		this.chunkId = chunkId;
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[BaseConfig.CHUNK_SIZE];
		Socket peerSocket;
		try {
			peerSocket = new Socket(peer.getIpAddress(), peer.getPort());
			IOUtils.write("GET_CHUNK " + file.getFileId() + " " + chunkId + "\n", peerSocket.getOutputStream());
			int readSize = IOUtils.read(peerSocket.getInputStream(), buffer);
			System.err.println(readSize);
			if (readSize < BaseConfig.CHUNK_SIZE) {
				buffer = Arrays.copyOf(buffer, readSize);
			}
			file.addChunk(chunkId, ArrayUtils.toObject(buffer)); //TODO saving last chunk
		} catch (IOException e) {
			file.unreserveChunk(chunkId);
			e.printStackTrace();
		} finally {
			Application.availableDownloads.release();
		}
	}
}
