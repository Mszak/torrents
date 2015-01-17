package client.utils;

import java.util.ArrayList;
import java.util.List;

import entities.FileInfo;
import entities.Peer;

public class ServerProtocolParser {

	public static List<FileInfo> parseListResponse(String response) {
		List<FileInfo> result = new ArrayList<FileInfo>();
		
		for (String info : response.split(":")) {
			result.add(new FileInfo(info.split(",")[0], Integer.parseInt(info.split(",")[1]), Integer.parseInt(info.split(",")[2])));
		}
		
		return result;
	}

	public static List<Peer> parsepeerInfo(String response) {
		List<Peer> result = new ArrayList<>();
		
		for (String info : response.split(":")) {
			result.add(new Peer(info.split(",")[0], Integer.parseInt(info.split(",")[1])));
		}
		
		return result;	
	}
}
