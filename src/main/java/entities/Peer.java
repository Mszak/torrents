package entities;

public class Peer {

	private final String ipAddress;
	private final int port;

	public Peer(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	/**
	 * Zwraca adres IP peera
	 * @return
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Zwraca numer portu, na którym nasłuchuje peer
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "Peer: " + ipAddress + ", port: " + port;
	}
}
