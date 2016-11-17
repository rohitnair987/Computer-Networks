import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Socket based TCP chat application: List of commands by the client: REGISTER
 * [username] [password]
 * 
 * A simple Swing-based client for the chat server. Graphically it is a frame
 * with a text field for entering messages and a textarea to see the whole
 * dialog.
 *
 * The client follows the Chat Protocol which is as follows. When the server
 * sends "SUBMITNAME" the client replies with the desired screen name. The
 * server will keep sending "SUBMITNAME" requests as long as the client submits
 * screen names that are already in use. When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE " then all characters
 * following this string should be displayed in its message area.
 */
public class Client {

	BufferedReader in;
	PrintWriter out;

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Return in the listener sends the
	 * textfield contents to the server. Note however that the textfield is
	 * initially NOT editable, and only becomes editable AFTER the client
	 * receives the NAMEACCEPTED message from the server.
	 */

	/**
	 * Runs the client as an application with a closable frame.
	 */
	public static void main(String[] args) throws Exception {

		// To-do: check .close() et al

		
		// To-do: server disconnect or no internet --> client gracefully goes
		// down

		Scanner cin = new Scanner(System.in);
		String serverAddress;
		if (args.length == 0 || args[0].equalsIgnoreCase("me")) {
			serverAddress = Inet4Address.getLocalHost().getHostAddress();
		} else {
			serverAddress = args[0];
		}


		System.out.println(serverAddress);

		// To-do: remove this
		if ("127.0.0.1".equals(serverAddress)) {
			System.out.println("Offline local chat");
		}

		try {
			Socket socket = new Socket();
			// socket.setSoTimeout(5000);
			socket.connect(new InetSocketAddress(serverAddress, 9001), 1000);

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// Test the connection
			if (!socket.isConnected()) {
				System.out.println("Unable to reach the server right now. Please try again later");
			}

			else {

				System.out.println("Hi! Please Register or Login to continue");
				new Thread(new Receiver(socket, in, out)).start();

				String line = "";
				while (true) {
					// ignore case
					// remove to lower at server
					line = cin.nextLine().toLowerCase();
					out.println(line);

//					if (line.startsWith("img") || line.startsWith("image")) {
//						String words[] = line.split(" ");
//						if (words.length != 3) {
//							System.out.println("Invalid " + words[0] + " command.");
//						} else {
//
//							words[2] = "deepika.jpg";
//							// To-do: check if file exists
//							File fi = new File(words[2]);
//							byte[] fileContent = Files.readAllBytes(fi.toPath());
//
////							for (int i = 0; i < 10; i++) {
////								System.out.print(fileContent[i] + " ");
////							}
////							System.out.println();
//
//							out.println(line + " " + fileContent.length);
//							System.out.println("sending " + fileContent.length + " bytes");
//
//							DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
//							dOut.write(fileContent);           // write the message
//							
////							dOut.close();
//
//							// socket.close();
//						}
//					} else {
//						out.println(line);
//					}
				}

			}
		} catch (SocketException se) {
			System.out.println("Network is unreachable. Please try again later.");
		}

		catch (Exception e) {
			System.out.println("excp: " + e);
		}

	}

}

class Receiver implements Runnable {

	Socket socket;
	BufferedReader in;
	PrintWriter out;

	public Receiver(Socket socket, BufferedReader in, PrintWriter out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}

	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				System.out.println(line);

//				if line starts with sendimg or imgdata --> efficiency
				
				String words[] = line.split(" ");
				
				if (words[0].equals("sendImg")) {
					System.out.println("sendImg method");
					words[2] = "deepika.jpg";
					
					String imgName = words[2];
					String targetUserName = words[1];
					
//					// To-do: check if file exists
					File fi = new File(imgName);
					byte[] fileContent = Files.readAllBytes(fi.toPath());

//					for (int i = 0; i < 10; i++) {
//						System.out.print(fileContent[i] + " ");
//					}
//					System.out.println();

					System.out.println("sending " + fileContent.length + " bytes");
					
					out.println("imgData " + targetUserName + " " + imgName + " " + fileContent.length);
					
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					dOut.write(fileContent);
					
//					dOut.close();
					
				}

				if (words[0].equals("imgData")) {
					System.out.println("imgData method");
					
					String sourceUserName = words[1];
					String imgName = words[2];
					int length = Integer.parseInt(words[3]);
					System.out.println("data length " + length);

					DataInputStream dIn = new DataInputStream(socket.getInputStream());
					byte[] fileContent = new byte[length];
					dIn.readFully(fileContent, 0, length);
					
					// To-do: imgName
					FileOutputStream fos = new FileOutputStream("deeps.jpg");
					fos.write(fileContent);
					fos.close();
					
					System.out.println("Received " + imgName + " from " + sourceUserName);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}