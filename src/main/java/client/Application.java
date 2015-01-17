package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import miniServer.MiniServer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import client.shell.Shell;
import config.BaseConfig;
import entities.ChunkedFile;
import entities.DownloadableChunkedFile;
import entities.Peer;
import entities.UploadableChunkedFile;

public class Application {
	
	public static final List<UploadableChunkedFile> uploadedFiles = new CopyOnWriteArrayList<>();
	public static final List<DownloadableChunkedFile> downloadedFiles = new CopyOnWriteArrayList<>();
	
	private static int clientId = 7;
		
	public static void main(String[] args) {
		initServerConnection();
		initTickTask();
		initMiniServer();
		Shell shell = new Shell();
		try {
			shell.run();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initMiniServer() {
		new Thread(new MiniServer()).start();
	}

	private static void initServerConnection() {
		try {
			Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
			OutputStream socketOut = socket.getOutputStream();
			InputStream socketIn = socket.getInputStream();
			IOUtils.write("REGISTER", socketOut);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
			clientId = Integer.parseInt(reader.readLine());
			System.out.println(clientId);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't connect to server");
			System.exit(0);
		}
	}
//    put /home/jakub/I.sql
	private static void initTickTask() {
		TimerTask tickTask = new TimerTask() {
			
			private Socket socket;
			
			@Override
			public void run() {
				try {
					socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
					for (UploadableChunkedFile file : uploadedFiles) {
						tickFile(file.getFileId());
					}
					for (DownloadableChunkedFile file : downloadedFiles) {
						tickFile(file.getFileId());
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}

			private void tickFile(int fileId) throws IOException {
				IOUtils.write("TICK " + clientId + " " + fileId, socket.getOutputStream());
			}
		};
		Timer tickTimer = new Timer();
		tickTimer.schedule(tickTask, 1000, 2000);
	}

	public static int getClientId() {
		return clientId;
	}

	//TODO refactor
	public static void startDownload(List<Peer> peerInfo, DownloadableChunkedFile file) {
		Random r = new Random();
		
		while (!file.isFileFull()) {
			int randomPeerIndex = r.nextInt(peerInfo.size());
			Peer peer = peerInfo.get(randomPeerIndex);
			
			try {
				Socket peerSocket = new Socket(peer.getIpAddress(), peer.getPort());
				IOUtils.write("GET_POSSESSED_CHUNKS " + file.getFileId() + "\n", peerSocket.getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
				String chunks = reader.readLine();
				peerSocket.close();
				
				List<Integer> integerChunks = parseToArray(chunks);
				for (Integer chunkId : integerChunks) {
					if (!file.containsChunk(chunkId)) {
						byte[] buffer = new byte[BaseConfig.CHUNK_SIZE];
						peerSocket = new Socket(peer.getIpAddress(), peer.getPort());
						
						IOUtils.write("GET_CHUNK " + file.getFileId() + " " + chunkId + "\n", peerSocket.getOutputStream());
						IOUtils.read(peerSocket.getInputStream(), buffer);
						file.addChunk(chunkId, ArrayUtils.toObject(buffer));
						
						peerSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		file.saveFile();
	}

	private static List<Integer> parseToArray(String chunks) {
		List<Integer> result = new ArrayList<Integer>();
		
		for (String chunkId : chunks.split(",")) {
			result.add(Integer.parseInt(chunkId));
		}
		
		return result;
	}

	public static ChunkedFile getFile(int fileId) {
		for (ChunkedFile file : downloadedFiles) {
			if (file.getFileId() == fileId) {
				return file;
			}
		}
		
		for (ChunkedFile file : uploadedFiles) {
			if (file.getFileId() == fileId) {
				return file;
			}
		}
		
		throw new RuntimeException("No file with id: " + fileId);
	}
}
