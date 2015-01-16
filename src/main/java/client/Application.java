package client;

import client.shell.Shell;

public class Application {
		
	public static void main(String[] args) {
		Shell shell = new Shell();
		try {
			shell.run();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
