package client;

import java.io.IOException;
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
		
	public static void main(String[] args) {
		initTickTask();
		
		Shell shell = new Shell();
		try {
			shell.run();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initTickTask() {
		TimerTask tickTask = new TimerTask() {
			
			private Socket socket;
			
			@Override
			public void run() {
				System.out.println("TICK TASK: ticking dowloadable files.");
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
				IOUtils.write("TICK " + fileId, socket.getOutputStream());
			}
		};
		Timer tickTimer = new Timer();
		tickTimer.schedule(tickTask, 1000, 1000);
	}
}
