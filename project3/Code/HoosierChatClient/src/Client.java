import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

		// To-do: server disconnect or no internet --> client gracefully goes
		// down

		Scanner cin = new Scanner(System.in);
		String serverAddress;
		if (args.length == 0 || args[0].equalsIgnoreCase("me")) {
			serverAddress = Inet4Address.getLocalHost().getHostAddress();
		} else {
			serverAddress = args[0];
		}

		// serverAddress = "10.0.0.7";
		// serverAddress = "172.18.25.74";

		System.out.println(serverAddress);

		if ("127.0.0.1".equals(serverAddress)) {
			System.out.println("Offline local chat");
		}

		try {
			System.out.println("trying");
			Socket socket = new Socket();
//			socket.setSoTimeout(5000);
			socket.connect(new InetSocketAddress(serverAddress, 9001), 1000);

			// Socket socket = new Socket(serverAddress, 9001);
			System.out.println("connected jus now");
			// boolean res = socket.getInetAddress().isReachable(1000);
			// if(res) {
			// if (socket.getInputStream().read() != -1) {
			// System.out.println("yes");
			// } else {
			// System.out.println("no");
			// }

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			// out.println("test msg to server");

			// Test the connection
			if (!socket.isConnected()) {
				System.out.println("Unable to reach the server right now. Please try again later");
			}

			else {

				System.out.println("Hi! Please Register or Login to continue");
				new Thread(new Receiver(in, out)).start();

				String line = "";
//				while (true) 
				{
					// ignore case
					// remove to lower at server
					line = cin.nextLine().toLowerCase();
//					if(false) {
					if (line.startsWith("img") || line.startsWith("image")) {
						File fi = new File("deepika.jpg");
						byte[] fileContent = Files.readAllBytes(fi.toPath());
						
						
//						for (int i = 0; i < 10; i++){
//							System.out.print(fileContent[i] + " ");
//						}
//						System.out.println();
						
						
//						System.out.println((int)fileContent[1]);
//						out.println(fileContent.length);
//						out.println(40);
//						out.println(41);
//						out.println(41);
//						out.println(41);
//						out.println(41);
//						Thread.sleep(1000);
						
						for (int i = 0 ; i < 10; i++) {
							System.out.print(fileContent[i] + " ");
						}
						
						DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
						dOut.writeInt(fileContent.length); // write length of the message
						dOut.write(fileContent);           // write the message
						
						
						//BufferedImage img = ImageIO.read(new File("deepika.jpg"));
//						System.out.println("ht = " + img.getHeight());
//						out.print("img ");
//						System.out.println(img);
								
						//out.print(img);
//						out.flush();
//						System.out.println();
						
//						socket.close();
					} else {
						out.println(line);
					}
					System.exit(0);
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

	BufferedReader in;
	PrintWriter out;

	public Receiver(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				// if (line.equals("status")) {
				// out.println("yes");
				// } else {
				System.out.println(line);
				// }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}