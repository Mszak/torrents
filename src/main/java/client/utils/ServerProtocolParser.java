package client.utils;

import java.util.ArrayList;
import java.util.List;

import entities.FileInfo;

public class ServerProtocolParser {

	public static List<FileInfo> parseListResponse(String response) {
		List<FileInfo> result = new ArrayList<FileInfo>();
		
		for (String info : response.split(":")) {
			result.add(new FileInfo(info.split(",")[0], Integer.parseInt(info.split(",")[1])));
		}
		
		return result;	
	}
}
