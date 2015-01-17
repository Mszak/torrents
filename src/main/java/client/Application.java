package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;

import client.shell.Shell;
import config.BaseConfig;
import entities.DownloadableChunkedFile;
import entities.UploadableChunkedFile;

public class Application {
	
	public static final List<UploadableChunkedFile> uploadedFiles = new CopyOnWriteArrayList<>();
	public static final List<DownloadableChunkedFile> downloadedFiles = new CopyOnWriteArrayList<>();
	
	private static int clientId;
		
	public static void main(String[] args) {
		initTickTask();
		initServerConnection();
		Shell shell = new Shell();
		try {
			shell.run();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initServerConnection() {
		try {
			Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
			OutputStream socketOut = socket.getOutputStream();
			InputStream socketIn = socket.getInputStream();
			IOUtils.write("REGISTER", socketOut);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
			clientId = Integer.parseInt(reader.readLine());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Can't connect to server");
			System.exit(0);
		}
	}

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
		tickTimer.schedule(tickTask, 1000, 1000);
	}

	public static int getClientId() {
		return clientId;
	}
}
