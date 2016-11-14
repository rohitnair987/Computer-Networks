import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Receiver implements Runnable {

	Scanner cin;
	List<String> events;

	public Receiver(List<String> events) {
		cin = new Scanner(System.in);
		this.events = events;
	}

	public void run() {
		while (true) {
			try {
				String line = cin.nextLine();
				if (line.equalsIgnoreCase("events")) {
					if (events.size() == 0) {
						System.out.println("\nNo events logged at the server");
					} else {
						System.out.println("\n--------------------------------------");
						System.out.println("List of requests to the server:");
						for (String event : events) {
							System.out.println(event);
						}
						System.out.println("--------------------------------------");
					}
					System.out.println();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

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

	/**
	 * Events for logging
	 */
	private static List<String> events = new ArrayList<>();

	/**
	 * The application main method, which just listens on a port and spawns
	 * handler threads.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The Server is running.");

		Thread consoleReader = new Thread(new Receiver(events));
		consoleReader.start();

		// Thread userPinger = new Thread(new Pinger(events));
		// userPinger.start();

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
					event = in.readLine();
					System.out.println(event);
					events.add(event);

					if (event == null) {
						return;
					}

					String words[] = event.split(" ");

					String command = words[0].toLowerCase();

					switch (command) {
					
					case "register":

						if (words.length != 3) {
							out.println("Invalid Register command, try again");
						}

						else {
							String res = checkString(words[1]);
							if (!res.equals("Yes")) {
								out.println(res);
								break;
							}

							res = checkString(words[2]);
							if (!res.equals("Yes")) {
								out.println(res);
								break;
							}

							if (!names.containsKey(words[1])) {
								names.put(words[1], words[2]);
								out.println("Registration successful. Welcome to Hoosier Chat " + words[1] + " !!");
							}

							else {
								out.println("This user already exists, please try another one");
							}

						}
						break;

					case "login":

						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							if (activeUsers.size() >= 16) {
								out.println("Server full. Please come back later");
							} else {
								if (words.length != 3) {
									out.println("Invalid Login command, try again");
								} else {
									String pw = names.get(words[1]);
									if (pw == null) {
										out.println("User does not exist. Try again.");
									} else if (pw.equals(words[2])) {
										activeUsers.put(words[1], out);
										currentUser = words[1];
										out.println("Login success! Start typing ...");
									} else {
										out.println("Wrong password. Try again.");
									}
								}

							}

						}

						else {
							out.println("You're already logged in as " + currentUser);
						}

						break;

						
					case "logout":
						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("You're not logged in");
						}

						else {
							out.println("See ya!");
							activeUsers.remove(currentUser);
						}
						break;

						
					case "send":
						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("Please login. Register if you do not have an account");
						}

						// To-do: refresh
						else {
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
									targetUser.println(currentUser + ": " + words[2]);
								}
							} else {
								out.println("Invalid send command. Please try again.");
							}
						}
						break;

						
					case "senda":
						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("Please login. Register if you do not have an account");
						}

						else {
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
									targetUser.println(words[2]);
								}
							} else {
								out.println("Invalid send command. Please try again.");
							}
						}
						break;

					case "list":
						for (String user : activeUsers.keySet()) {
							if (isUserOnline(user)) {

							}
							out.println(user);
						}
						break;

					default:
						out.println("Invalid command. Please try again.");
					}

				}

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

		private String checkString(String s) {
			int len = s.length();
			if (len < 4 || len > 8) {
				return "Username and password should be 4 t0 8 characters long";
			}
			String pattern = "^[a-zA-Z0-9]*$";
			if (s.matches(pattern)) {
				return "Yes";
			}
			return "Only alphanumerics are allowed";
		}

		private boolean isUserLoggedIn(String user) {
			for (String activeUser : activeUsers.keySet()) {
				if (user.equals(activeUser)) {
					return true;
				}
			}
			return false;
		}

		private boolean isUserOnline(String user) {

			return false;
		}
	}
}