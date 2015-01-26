package client.utils;

import java.util.ArrayList;
import java.util.List;

import entities.FileInfo;
import entities.Peer;

public class ServerProtocolParser {

	/**
	 * Metoda zwraca listę informacji o plikach po sparsowaniu odpowiedzi z serwera
	 */
	public static List<FileInfo> parseListResponse(String response) {
		List<FileInfo> result = new ArrayList<FileInfo>();
		
		for (String info : response.split(",")) {
			result.add(new FileInfo(info.split(":")[0], Integer.parseInt(info.split(":")[1])));
		}

		return result;
	}

	/**
	 * Metoda zwraca listę peerów po sparsowaniu odpowiedzi z serwera
	 */
	public static List<Peer> parsepeerInfo(String response) {
		List<Peer> result = new ArrayList<>();

		

		for (String info : response.split(",")) {
			result.add(new Peer(info.split(":")[0], Integer.parseInt(info.split(":")[1])));
		}

		return result;
	}
}
