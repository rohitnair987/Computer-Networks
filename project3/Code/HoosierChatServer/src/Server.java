import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Server {

	private static final int PORT = 9001;

	/**
	 * The set of all names of clients in the chat room. Maintained so that we
	 * can check that new clients are not registering name already in use.
	 */
	private static Map<String, String> names = new HashMap<String, String>();

	/**
	 * The set of all the print writers for all the clients. This set is kept so
	 * we can easily broadcast messages.
	 */
	private static Map<String, PrintWriter> activeUsers = new HashMap<String, PrintWriter>();

	/*
	 * Events for logging
	 */
	private static List<String> events = new ArrayList<>();

	/**
	 * The application main method, which just listens on a port and spawns
	 * handler threads.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The Server is running.");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			System.out.println("The Server has stopped.");
			listener.close();
		}
	}

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for a dealing with a single client and broadcasting its
	 * messages.
	 */
	private static class Handler extends Thread {
		private String event;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private String currentUser = "";

		/**
		 * Constructs a handler thread, squirreling away the socket. All the
		 * interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name
		 * until a unique one has been submitted, then acknowledges the name and
		 * registers the output stream for the client in a global set, then
		 * repeatedly gets inputs and broadcasts them.
		 */
		public void run() {
			try {

				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// Request a name from this client. Keep requesting until
				// a name is submitted that is not already used. Note that
				// checking for the existence of a name and adding the name
				// must be done while locking the set of names.
				while (true) {
					// out.println("SUBMITNAME");
					event = in.readLine();
					System.out.println("At Server: " + event);
					events.add(event);

					if (event == null) {
						return;
					}

					String words[] = event.split(" ");
					if (words.length < 1) {
						System.out.println("words too small");
						continue;
					}

					String command = words[0].toLowerCase();

					switch (command) {
					case "register":
						if (words.length != 3) {
							System.out.println("register");
							out.println("Invalid Register command, try again");
							break;
						}
						if (!names.containsKey(words[1])) {
							System.out.println("register success");
							names.put(words[1], words[2]);
							out.println("Registration successful. Welcome to Hoosier Chat " + words[1] + " !!");
						} else {
							System.out.println("register failed");
							out.println("This user already exists, please try another one");
						}
						break;

					case "login":
						if (activeUsers.size() >= 16) {
							System.out.println("Server full, please come back later");
							out.println("Server full, please come back later");
							break;
						}
						if (words.length != 3) {
							System.out.println("login");
							out.println("Invalid Login command, try again");
							break;
						}
						String pw = names.get(words[1]);
						if (pw == null) {
							System.out.println("User does not exist. Try again.");
							out.println("User does not exist. Try again.");
						}
						// max 16
						else if (pw.equals(words[2])) {
							activeUsers.put(words[1], out);
							currentUser = words[1];
							System.out.println("Login success! Start typing ...");
							out.println("Login success! Start typing ...");
						} else {
							System.out.println("Wrong password. Try again.");
							out.println("Wrong password. Try again.");
						}
						break;

					case "logout":
						boolean flag = false;
						for (Entry<String, PrintWriter> userEntry : activeUsers.entrySet()) {
							if (userEntry.getValue().equals(out)) {
								System.out.println("See ya");
								out.println("See ya");
								activeUsers.remove(userEntry.getKey());
								flag = true;
								break;
							}
						}
						if (!flag) {
							System.out.println("You're not logged in");
							out.println("You're not logged in");
						}

					case "send":
						if (words.length == 2) {
							for (Entry<String, PrintWriter> targetEntry : activeUsers.entrySet()) {
								if (targetEntry.getKey() != currentUser) {
									targetEntry.getValue().println(currentUser + ": " + words[1]);
								}
							}
						} else if (words.length == 3) {
							PrintWriter targetUser = activeUsers.get(words[1]);
							if (targetUser == null) {
								out.println(words[1] + " is inactive. Please try later.");
							} else {
								out.println(currentUser + ": " + words[2]);
							}
						} else {
							System.out.println("Invalid send command. Please try again.");
							out.println("Invalid send command. Please try again.");
						}
						break;

					case "senda":
						if (words.length == 2) {
							for (Entry<String, PrintWriter> targetEntry : activeUsers.entrySet()) {
								if (targetEntry.getKey() != currentUser) {
									targetEntry.getValue().println(words[1]);
								}
							}
						} else if (words.length == 3) {
							PrintWriter targetUser = activeUsers.get(words[1]);
							if (targetUser == null) {
								out.println(words[1] + " is inactive. Please try later.");
							} else {
								out.println(words[2]);
							}
						} else {
							System.out.println("Invalid send command. Please try again.");
							out.println("Invalid send command. Please try again.");
						}
						break;

					case "list":
						for(String user : activeUsers.keySet()) {
							out.println(user);
						}
						break;
					
					default:
						System.out.println("Invalid command. Please try again.");
					}

					// if (event == null) {
					// return;
					// }
					// synchronized (names) {
					// if (!names.contains(name)) {
					// names.add(name);
					// break;
					// }
					// }
				}

				// Now that a successful name has been chosen, add the
				// socket's print writer to the set of all writers so
				// this client can receive broadcast messages.
				// out.println("NAMEACCEPTED");
				// writers.add(out);

				// Accept messages from this client and broadcast them.
				// Ignore other clients that cannot be broadcasted to.
				// while (true) {
				// String input = in.readLine();
				// if (input == null) {
				// return;
				// }
				// for (PrintWriter writer : writers) {
				// writer.println("MESSAGE " + name + ": " + input);
				// }
				// }
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				// This client is going down! Remove its name and its print
				// writer from the sets, and close its socket.
				// if (name != null) {
				// names.remove(name);
				// }
				// if (out != null) {
				// writers.remove(out);
				// }
				// try {
				// socket.close();
				// } catch (IOException e) {
				// }
			}
		}
	}
}