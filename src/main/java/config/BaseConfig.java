package config;

public interface BaseConfig {
	public static final String DOWNLOAD_DIR = "/home/jakub/workspace/torrents/DOWNLOADS/";
	public static final int CHUNK_SIZE = 10 * 1024; //in bytes
	public static final int MAX_PARALLEL_DOWNLOADS = 5;
	
	public static final int RELOAD_PEER_INFO_PERIOD = 10 * 1000;
	
	public static final int CLIENT_UPLOAD_PORT = 10000;
	
//	public static final String SERVER_ADDRESS =  "83.22.65.18";
	public static final String SERVER_ADDRESS =  "178.37.116.165";
	public static final int SERVER_PORT = 3003;
	public static final int PEER_SOCKET_TIMEOUT = 10000;
}
