package miniServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import config.BaseConfig;

public class MiniServer implements Runnable {

	@Override
	public void run() {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(BaseConfig.CLIENT_UPLOAD_PORT);
			
			while (true) {
				Socket clientSocket = listenSocket.accept();
				DownloadHandler.handle(clientSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (listenSocket != null) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	
}
