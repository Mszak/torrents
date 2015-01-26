package client.shell;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import client.utils.ProtocolCommands;
import client.utils.ServerRequest;

public class Shell {
	
	private Scanner sc;
	
	public Shell() {
		sc = new Scanner(System.in);
	}
	
	/**
	 * Rozpoczącie shella
	 */
	public void run() {
		while(true) {
			System.out.print("$ ");
			String command = sc.nextLine();
			ServerRequest request = parseCommand(command);
			if (request != null) {
				try {
					request.execute();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error occured during request handling. Try again.get");
				}
			}
		}
	}

	private ServerRequest parseCommand(String command) {
		try {
			if (command.startsWith("list")) {
				return new ServerRequest(ProtocolCommands.LIST, "");
			}
			else if (command.startsWith("put")) {
				String filename = command.split(" ")[1];
				if (Files.exists(Paths.get(filename))) {
					return new ServerRequest(ProtocolCommands.PUT, filename);					
				}
			}
			else if (command.startsWith("get")) {
				String fileId = command.split(" ")[1];
				return new ServerRequest(ProtocolCommands.GET, fileId);
			}
			
			throw new RuntimeException("Parse error.");
			
		} catch (Exception e) {
			System.out.println("Parse error.");
			return null;
		}
	}
	
}
