package client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.io.IOUtils;

import client.Application;
import config.BaseConfig;
import entities.DownloadableChunkedFile;
import entities.FileInfo;
import entities.Peer;
import entities.UploadableChunkedFile;

public class ServerRequest {
	
	private final ProtocolCommands type;
	private final String argument;
	
	public ServerRequest(ProtocolCommands type, String argument) {
		this.type = type;
		this.argument = argument;
	}

	public ProtocolCommands getType() {
		return type;
	}

	public String getArgument() {
		return argument;
	}
	
	public void execute() {
		switch (type) {
		case GET:
			try {
				Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
				OutputStream socketOut = socket.getOutputStream();
				InputStream socketIn = socket.getInputStream();
				IOUtils.write("GET " + Application.getClientId() + " " + argument, socketOut);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
				String response = reader.readLine();
				List<Peer> peerInfo = ServerProtocolParser.parsepeerInfo(response);
				response = reader.readLine();
				Application.downloadedFiles.add(
						new DownloadableChunkedFile(response.split(" ")[0], Integer.parseInt(argument), Integer.parseInt(response.split(" ")[1])));
				Application.startDownload(peerInfo);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case LIST:
			try {
				Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
				OutputStream socketOut = socket.getOutputStream();
				InputStream socketIn = socket.getInputStream();
				IOUtils.write("LIST", socketOut);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
				String response = reader.readLine();
				List<FileInfo> filesOnServer = ServerProtocolParser.parseListResponse(response);
				printAvailableFiles(filesOnServer);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case PUT:
			String filename = argument.substring(argument.lastIndexOf(File.pathSeparator));
			try {
				Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
				OutputStream socketOut = socket.getOutputStream();
				InputStream socketIn = socket.getInputStream();
				IOUtils.write("PUT " + Application.getClientId() + " " + filename + " " + FileInfo.getChunkNumbers(argument), socketOut);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));
				Integer fileId = Integer.parseInt(reader.readLine());
				Application.uploadedFiles.add(new UploadableChunkedFile(argument, fileId));
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private void printAvailableFiles(List<FileInfo> filesOnServer) {
		for (FileInfo info : filesOnServer) {
			System.out.println(info);
		}
	}
}
