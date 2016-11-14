import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
		System.out.println("Hi! Please Register or Login to continue");

		Scanner cin = new Scanner(System.in);

		// ask till you get a correct format of input
		String line = "";
		int test = 0;
		while (test++ < 1) {
			line = cin.nextLine();
			String words[] = line.split(" ");
			if (words.length == 3) {
				break;
			}
			System.out.println("Invalid input, try again");
		}

		String serverAddress = "10.0.0.7";
		Socket socket = new Socket(serverAddress, 9001);

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		out.println(line);

		Thread receiver = new Thread(new Receiver(in, out));
		receiver.start();

		while (true) {
			line = cin.nextLine();
			out.println(line);
		}

		// cin.close();

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
				if (line.equals("status")) {
					out.println("yes");
				} else {
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}