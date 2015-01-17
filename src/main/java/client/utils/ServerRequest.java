package client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.io.IOUtils;

import config.BaseConfig;
import entities.FileInfo;

public class ServerRequest {
	
	private final ClientServerProtocol type;
	private final String argument;
	
	public ServerRequest(ClientServerProtocol type, String argument) {
		this.type = type;
		this.argument = argument;
	}

	public ClientServerProtocol getType() {
		return type;
	}

	public String getArgument() {
		return argument;
	}
	
	public ServerResponse execute() {
		switch (type) {
		case GET:
			break;
		case LIST:
			try {
				Socket socket = new Socket(BaseConfig.SERVER_ADDRESS, BaseConfig.SERVER_PORT);
				OutputStream socketOut = socket.getOutputStream();
				InputStream socketIn = socket.getInputStream();
				IOUtils.write("LIST", socketOut);
				String response = IOUtils.toString(socketIn);
				List<FileInfo> filesOnServer = ServerProtocolParser.parseListResponse(response);
				printAvailableFiles(filesOnServer);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case PUT:
			break;
		case TICK:
			break;
		default:
			break;
		
		}
		return null; //TODO
	}

	private void printAvailableFiles(List<FileInfo> filesOnServer) {
		for (FileInfo info : filesOnServer) {
			System.out.println(info);
		}
	}
}
