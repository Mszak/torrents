package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
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
		Socket peerSocket = new Socket();
		try {
			SocketAddress peerAddress = new InetSocketAddress(peer.getIpAddress(), peer.getPort());
			try {
				peerSocket.connect(peerAddress, BaseConfig.PEER_SOCKET_TIMEOUT);				
			} catch (SocketTimeoutException e) {
				throw new IOException();
			}
			
			IOUtils.write("GET_CHUNK " + file.getFileId() + " " + chunkId + "\n", peerSocket.getOutputStream());
			int readSize = IOUtils.read(peerSocket.getInputStream(), buffer);

			if (readSize == 0) {
				System.out.println("Unable to connect, rollback.");
				throw new IOException();
			}

			if (readSize < BaseConfig.CHUNK_SIZE) {
				buffer = Arrays.copyOf(buffer, readSize);
			}
			file.addChunk(chunkId, ArrayUtils.toObject(buffer));
		} catch (IOException e) {
			file.unreserveChunk(chunkId);
			e.printStackTrace();
		} finally {
			Application.availableDownloads.release();
			try {
				peerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Timeout socket test.
	 */
	public static void main(String[] args) {
		System.out.println("Try to connect...");
		Socket peerSocket = new Socket();
		SocketAddress peerAddress = new InetSocketAddress("178.37.116.165", 10);
//		SocketAddress peerAddress = new InetSocketAddress("74.125.71.103", 10);
		System.out.println("Success!");
		try {
			peerSocket.connect(peerAddress, BaseConfig.PEER_SOCKET_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			peerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
