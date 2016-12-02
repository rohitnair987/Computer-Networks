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
		String serverAddress;
		if (args.length == 0 || args[0].equalsIgnoreCase("me")) {
			serverAddress = Inet4Address.getLocalHost().getHostAddress();
		} else {
			serverAddress = args[0];
		}

		Socket socket = new Socket();

		try {
			socket.connect(new InetSocketAddress(serverAddress, 9001), 1000);

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
				System.out.println(line);

				String words[] = line.split(" ");

				if (words[0].equals("sendImg")) {
					String imgName = words[2];
					String targetUserName = words[1];

					// To-do: check if file exists
					File fi = new File(imgName);
					if (!fi.exists()) {
						System.out.println("File not present");
					} 
					
					else {
						byte[] fileContent = Files.readAllBytes(fi.toPath());

						out.println("imgData " + targetUserName + " " + imgName + " " + fileContent.length);

						DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
						dOut.write(fileContent);
					}
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