package entities;

public class FileInfo {

	private final String fileName;
	private final int fileId;

	public FileInfo(String fileName, int fileId) {
		this.fileName = fileName;
		this.fileId = fileId;
	}

	public int getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String toString() {
		return "File name: " + fileName + ", file id: " + fileId;
	}
}
