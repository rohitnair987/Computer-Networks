import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {

	BufferedReader in;
	PrintWriter out;

	public static void main(String[] args) throws Exception {

		Scanner cin = new Scanner(System.in);
		String serverAddress = Inet4Address.getLocalHost().getHostAddress();
		int port = 9001;

		// Set Server IP Address and Port Number
		if (args.length > 0) {
			serverAddress = args[0];
			if (args.length > 1) {
				try {
					Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.out.println("Port number should be an integer");
				}
			}
		}

		final int SOCKET_TIMEOUT = 4000;
		Socket socket = new Socket();

		try {
			System.out.println("Trying to connect to the server at " + serverAddress + " on port number " + port);
			socket.connect(new InetSocketAddress(serverAddress, port), SOCKET_TIMEOUT);

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// Test the connection
			if (!socket.isConnected()) {
				System.out.println("Unable to reach the server right now. Please try again later");
			}

			else {

				System.out.println("Hi! Please Register or Login to continue");
				// Open a new thread for the client
				new Thread(new Receiver(socket, in, out)).start();

				/**
				 * Listener at the client's standard input
				 */
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
			System.out.println("Exception: " + e);
		}

		finally {
			cin.close();
			socket.close();
		}

	}

}

class Receiver implements Runnable {

	Socket socket;
	BufferedReader in;
	PrintWriter out;

	// Accepts only files up to 1 MB
	static final int MAX_FILE_SIZE = 1000000;

	public Receiver(Socket socket, BufferedReader in, PrintWriter out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}

	public void run() {
		while (true) {
			try {
				// Read and display messages from the server
				String line = in.readLine();
				String words[] = line.split(" ");

				if (words[0].equals("sendFile")) {
					String fileName = words[2];
					String targetUserName = words[1];

					// To-do: check if file exists
					File fi = new File(fileName);
					if (!fi.exists()) {
						System.out.println("File not present");
					}

					else {
						byte[] fileContent = Files.readAllBytes(fi.toPath());

						if (fileContent.length > MAX_FILE_SIZE) {
							System.out.println("Maximum File Size supported is 1 MB");
						}

						else {
							out.println(
									"fileDataToServer " + targetUserName + " " + fileName + " " + fileContent.length);
							DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
							dOut.write(fileContent);
						}
					}
				}

				else if (words[0].equals("fileDataFromServer")) {
					String sourceUserName = words[1];
					String fileName = words[2];
					int length = Integer.parseInt(words[3]);
					System.out.println("data length " + length);

					DataInputStream dIn = new DataInputStream(socket.getInputStream());
					byte[] fileContent = new byte[length];
					dIn.readFully(fileContent, 0, length);

					FileOutputStream fos = new FileOutputStream(fileName);
					fos.write(fileContent);
					fos.close();

					System.out.println("Received " + fileName + " from " + sourceUserName);
				}

				else {
					System.out.println(line);
				}

			} catch (NullPointerException npe) {
				System.out.println("Disconnected from server. Please try again later");
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}