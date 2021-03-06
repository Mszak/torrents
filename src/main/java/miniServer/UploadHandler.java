package miniServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.io.IOUtils;

import client.Application;
import entities.ChunkedFile;

public class UploadHandler {	
	
	private static class UploadTask extends Thread {
		
		private final Socket socket;
		
		public UploadTask(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String command = reader.readLine();
				int fileId = Integer.parseInt(command.split(" ")[1]);
				ChunkedFile file = null;
				switch (command.split(" ")[0]) {
				case "GET_CHUNK":
					int chunkId = Integer.parseInt(command.split(" ")[2]);
					file = Application.getFile(fileId);
					IOUtils.write(file.getChunk(chunkId), out);

					break;
				case "GET_POSSESSED_CHUNKS":
					file = Application.getFile(fileId);
					String chunks = chunksToString(file.getPossessedChunks());
					IOUtils.write(chunks + "\n", out);
					break;
				default:
					throw new RuntimeException("No such command");
				}
				
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String chunksToString(List<Integer> possessedChunks) {
			StringBuffer result = new StringBuffer();
			for(int chunk: possessedChunks) {
				result.append(chunk + ",");
			}
			result.deleteCharAt(result.length() - 1);
			return result.toString();
		}
	}

	/**
	 * Rozpoczyna nowy wątek do obsługi zapytań od innych peerów
	 * @param socket
	 */
	public static void handle(Socket socket) {
		System.out.println("Handling download chunk request");
		new UploadTask(socket).start();
	}

}
