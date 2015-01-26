package miniServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import config.BaseConfig;

public class MiniServer implements Runnable {

	/**
	 * Otwiera socketa na porcie BaseConfig.CLIENT_UPLOAD_PORT
	 * do komunikacji z innymi peerami
	 */
	@Override
	public void run() {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(BaseConfig.CLIENT_UPLOAD_PORT);
			
			while (true) {
				Socket clientSocket = listenSocket.accept();
				UploadHandler.handle(clientSocket);
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
