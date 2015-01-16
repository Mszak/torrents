package client.shell;

import java.util.Scanner;

import client.utils.ClientServerProtocol;
import client.utils.ServerRequest;
import client.utils.ServerResponse;

public class Shell {
	
	private Scanner sc;
	
	public Shell() {
		sc = new Scanner(System.in);
	}
	
	public void run() {
		while(true) {
			String command = sc.nextLine();
			ServerRequest request = parseCommand(command);
			ServerResponse response = request.execute();
		}
	}

	private ServerRequest parseCommand(String command) {
		if (command.startsWith("list")) {
			return new ServerRequest(ClientServerProtocol.LIST, "");
		}
		else {
			System.err.println("NOT IMPLEMENTED: " + command);
			return null;
		}
	}
	
}
