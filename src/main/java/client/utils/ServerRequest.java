package client.utils;

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
		return null; //TODO
	}
}
