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

		try {
			Socket socket = new Socket();
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
					line = cin.nextLine().toLowerCase();
					out.println(line);
				}

			}
		} 
		
		catch (SocketException se) {
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
					String imgName = words[2];
					String targetUserName = words[1];
					
					// To-do: check if file exists
					File fi = new File(imgName);
					byte[] fileContent = Files.readAllBytes(fi.toPath());

					out.println("imgData " + targetUserName + " " + imgName + " " + fileContent.length);
					
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					dOut.write(fileContent);
					
				}

				if (words[0].equals("imgData")) {
					String sourceUserName = words[1];
					String imgName = words[2];
					int length = Integer.parseInt(words[3]);
					System.out.println("data length " + length);

					DataInputStream dIn = new DataInputStream(socket.getInputStream());
					byte[] fileContent = new byte[length];
					dIn.readFully(fileContent, 0, length);
					
					FileOutputStream fos = new FileOutputStream(imgName);
					fos.write(fileContent);
					fos.close();
					
					System.out.println("Received " + imgName + " from " + sourceUserName);
					
				}
			} 
			catch(NullPointerException npe) {
				System.out.println("Disconnected from server. Please try again later");
				System.exit(1);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}