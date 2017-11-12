import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.MimetypesFileTypeMap;

import org.omg.Messaging.SyncScopeHelper;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// Waits for a connection request
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

	private static void HandleRequest(Socket s) {
		BufferedReader in;
		PrintWriter out;
		String request;
		try {

			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			// System.out.println(f);
			request = in.readLine();
			System.out.println("--- Client request: " + request);
			String fileName = "";
			char[] req = request.toCharArray();
			boolean add = false;
			for (char c : req) {
				if(add) {
					if(c==' ') {
						break;
					}
					fileName+=c;
				}
				if (c == '/') {
					add = true;
				}	
			}
			if(fileName.equals("")) {
				fileName = "index.html";
			}
			System.out.println("TEST:"+fileName);
			Scanner key1 = new Scanner(new File(fileName));
			String ext = "";
			char[] fileArr = ext.toCharArray();
			boolean ad = false;
			for(char x: fileArr) {
				if(add) {
					ext+=x;
				}
				if(x=='.') {
					add = true;
				}
			}
			String FileContent = "";
			while(key1.hasNextLine()) {
				FileContent+=key1.nextLine();
			}
			String contentType = "";
			if(ext.equals("js")) {
				contentType = "application/javascript";
			}
			if(ext.equals("html")) {
				contentType = "text/html";
			}
			if(ext.equals("css")) {
				contentType = "text/css";
			}
			out = new PrintWriter(s.getOutputStream(), true);
			out.println("HTTP/1.0 200");
			out.println("Content-type:"+contentType);
			out.println("Server-name: myserver");
			//todo
			String response = FileContent;
			out.println("Content-length: " + response.length());
			out.println("");
			out.println(response);
			
			
			

			out.flush();
			out.close();
			s.close();
		} catch (IOException e) {
			System.out.println("Failed respond to client request: " + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}