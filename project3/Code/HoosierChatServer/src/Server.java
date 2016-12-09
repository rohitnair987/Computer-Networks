import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Listener at the server's standard input Currently used only to listen to the
 * events command, which displays all the client commands since the server has
 * been up
 */
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
				if (line.equalsIgnoreCase("events") || line.equalsIgnoreCase("e")) {
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
					System.out.println(Inet4Address.getLocalHost().getHostAddress());
				} else {
					System.out.println("Invalid command");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

public class Server {

	/**
	 * Port number at which the server process listens to Clients need this port
	 * number to contact the server
	 */
	private static final int PORT = 9001;
	private static final int MAX_NUMBER_OF_CLIENTS = 16;
	private static final int MAX_MESSAGE_LENGTH = 4096;

	/**
	 * Stores the user-names and passwords of all clients in the system Can be
	 * stored in secondary storage for persistence
	 */
	private static Map<String, String> names = new HashMap<String, String>();

	/**
	 * Logged in Users, mapped to their corresponding sockets
	 */
	private static Map<String, Socket> activeUsers = new HashMap<>();

	/**
	 * Events for logging at server Can be stored in secondary storage for
	 * persistence Invoked by the command 'events' at the server
	 */
	private static List<String> events = new ArrayList<>();

	/**
	 * The application main method, which just listens on a port and spawns
	 * handler threads.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The Server is running.");

		/**
		 * Events for logging at server
		 */
		Thread consoleReader = new Thread(new Receiver(events));
		consoleReader.start();

		ServerSocket listener = new ServerSocket(PORT);
		System.out.println("My IP: " + Inet4Address.getLocalHost().getHostAddress());
		System.out.println("Port: " + listener.getLocalPort());

		try {
			while (true) {
				// Open a new thread per client
				new Handler(listener.accept()).start();
			}
		} finally {
			System.out.println("The Server has stopped.");
			listener.close();
		}
	}

	/**
	 * A thread per client to handle it's requests
	 */
	private static class Handler extends Thread {
		private String event;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private String currentUser = "";

		public Handler(Socket socket) throws IOException {
			this.socket = socket;
		}

		public void run() {
			try {

				// Input and output streams for each client's socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				Socket targetUser = null;

				/**
				 * Listens to the client, validated the requests and services
				 * them Sends corresponding error messages to clients
				 */
				while (true) {
					event = in.readLine();
					System.out.println(currentUser + ": " + event);

					if (event == null) {
						if (!currentUser.isEmpty()) {
							System.out.println(currentUser + " disconnected");
							activeUsers.remove(currentUser);
							events.add(currentUser + " disconnected");
						}

						else {
							events.add("User disconnected");
						}

						socket.close();
						return;
					}

					else {
						events.add(event);
					}

					String words[] = event.split(" ");
					String command = words[0];
					String targetUserName = "";

					switch (command) {

					/**
					 * The original client request for sending an file to
					 * another client
					 */
					case "f":
					case "file":

						// User has to be online to send an file
						if (currentUser.isEmpty() || !activeUsers.containsKey(currentUser)) {
							out.println("Please login to send a file");
						}

						else {

							if (words.length != 3) {
								out.println("Invalid " + command + " command.");
							} else {
								targetUserName = words[1];

								// We do not allow sending files to oneself
								if (currentUser.equals(targetUserName)) {
									out.println("Can't send an file to yourself");
								}

								else {
									// Check if target user is registered
									if (names.get(targetUserName) == null) {
										out.println(targetUserName + " is not registered in HoosierChat :(");
									}

									else {
										targetUser = activeUsers.get(targetUserName);

										if (targetUser == null) {
											out.println(targetUserName + " is inactive. Please try later.");
										}

										/*
										 * Tells the user that command is
										 * correct Provides information required
										 * to send the file
										 */
										else {
											String fileName = words[2];
											out.println("sendFile " + targetUserName + " " + fileName);
										}
									}
								}
							}
						}

						break;

					/*
					 * Inputs the file data Provides information required to
					 * receive the file Outputs the file
					 */
					case "fileDataToServer":

						targetUserName = words[1];
						String fileName = words[2];
						int length = Integer.parseInt(words[3]);
						targetUser = activeUsers.get(targetUserName);

						PrintWriter targetOut = new PrintWriter(targetUser.getOutputStream(), true);

						DataInputStream dIn = new DataInputStream(socket.getInputStream());

						if (length > 0) {
							// Read the file from the current socket
							byte[] fileContent = new byte[length];
							dIn.readFully(fileContent, 0, length);

							// Forward the file to target socket
							targetOut.println("fileDataFromServer " + currentUser + " " + fileName + " " + length);
							DataOutputStream dOut = new DataOutputStream(targetUser.getOutputStream());
							dOut.write(fileContent);
						} else {
							System.out.println("Empty file");
						}

						break;

					case "r":
					case "register":

						// Logged in users cannot register
						if (!currentUser.isEmpty()) {
							out.println("Logout to register a new user");
						}

						else {

							if (words.length != 3) {
								out.println("Invalid Register command, try again");
							}

							else {
								String userName = words[1];
								String password = words[2];

								// Checks the length and alphanumeric conformity
								String res = checkString(userName);
								if (!res.equals("Yes")) {
									out.println(res);
									break;
								}

								res = checkString(password);
								if (!res.equals("Yes")) {
									out.println(res);
									break;
								}

								if (!names.containsKey(userName)) {
									names.put(userName, password);
									out.println("Registration successful. Welcome to Hoosier Chat " + userName + " !!");
								}

								else {
									out.println("This user already exists, please try another one");
								}

							}
						}

						break;

					case "l":
					case "login":

						if (words.length != 3) {
							out.println("Invalid Login command, try again");
						} else {
							String userName = words[1];
							String password = words[2];

							// User is already online
							if (!currentUser.isEmpty()) {
								out.println("You're online as " + currentUser);
							}

							else {

								String pw = names.get(userName);
								if (pw == null) {
									out.println("User does not exist. Try again.");
								} else {

									if (!pw.equals(password)) {
										out.println("Wrong password! Try again.");
									} else {

										if (activeUsers.size() >= MAX_NUMBER_OF_CLIENTS) {
											out.println("Server full. Please come back later");
										} else {
											/*
											 * Override login on another machine
											 * through the same user name
											 */
											if (activeUsers.containsKey(userName)) {
												out.println("Logging you out of the other instance you've logged in");
												activeUsers.get(userName).close();
												activeUsers.remove(userName);
											}

											activeUsers.put(userName, socket);
											currentUser = userName;
											out.println("Login successful! Start typing ...");

										}

									}

								}
							}
						}

						break;

					case "lo":
					case "logout":

						if (currentUser.isEmpty()) {
							out.println("You're not logged in");
						}

						else {
							activeUsers.remove(currentUser);
							currentUser = "";
							out.println("See ya!");
						}

						break;

					case "b":
					case "broadcast":
					case "ba":
					case "broadcasta":

						if (currentUser.isEmpty()) {
							out.println("Please login to send messages. Register if you do not have an account");
						}

						else {
							if (words.length < 2) {
								out.println("Invalid " + command + " command. Please try again.");
							}

							else {

								StringBuffer msg = new StringBuffer();

								if (!command.endsWith("a")) {
									msg.insert(0, currentUser + ": ");
								}

								msg.append(event.substring(command.length() + 1));

								if (msg.length() > MAX_MESSAGE_LENGTH) {
									out.println("Your message is too long");
								}

								else {
									// Broadcast the message to all online users
									for (Entry<String, Socket> targetEntry : activeUsers.entrySet()) {
										if (targetEntry.getKey() != currentUser) {
											new PrintWriter(targetEntry.getValue().getOutputStream(), true)
													.println(msg);
										}
									}
								}

							}
						}

						break;

					case "s":
					case "send":
					case "sa":
					case "senda":

						if (currentUser.isEmpty()) {
							out.println("Please login to send messages. Register if you do not have an account");
						}

						else {
							if (words.length < 3) {
								out.println("Invalid " + command + " command. Please try again.");
							}

							else {

								targetUserName = words[1];

								// We do not allow sending files to oneself
								if (currentUser.equals(targetUserName)) {
									out.println("Can't send a text to yourself");
								}

								else {
									StringBuffer msg = new StringBuffer();
									Socket targetSocket = null;

									// Check if target user is registered
									if (names.get(targetUserName) == null) {
										out.println(targetUserName + " is not registered in HoosierChat :(");
										break;
									}

									// Check if target user is logged in
									targetSocket = activeUsers.get(targetUserName);
									if (targetSocket == null) {
										out.println(targetUserName + " is inactive. Please try later.");
										break;
									}

									// Non-anonymous send
									if (!command.endsWith("a")) {
										msg.append(currentUser + ": ");
									}

									msg.append(event.substring(command.length() + targetUserName.length() + 2));

									if (msg.length() > MAX_MESSAGE_LENGTH) {
										out.println("Your message is too long");
									}

									else {
										// Send a one-to-one message to the
										// designated user
										new PrintWriter(targetSocket.getOutputStream(), true).println(msg);
									}
								}
							}

						}

						break;

					/**
					 * Lists all the users who are online in the system
					 */
					case "list":
						activeUsers.forEach((k, v) -> out.println(k));
						break;

					/**
					 * Gets the user-name of the user that sends this request
					 */
					case "whoami":
						out.println(!currentUser.isEmpty() ? currentUser : "You're not logged in");
						break;

					/**
					 * Whether the user is online
					 */
					case "isonline":
						if (words.length < 2) {
							out.println("Please enter username after the command");
						} else {
							if (!names.containsKey(words[1])) {
								out.println("This user does not exist");
							} else {
								out.println(activeUsers.containsKey(words[1]) ? "Yes" : "No");
							}
						}
						break;

					/**
					 * Whether the user is registered
					 */
					case "exists":
						if (words.length < 2) {
							out.println("Please enter username after the command");
						} else {
							out.println(names.containsKey(words[1]) ? "Yes" : "No");
						}
						break;

					default:
						out.println("Invalid command. Please try again.");
					}

				}

			}

			catch (Exception e) {
				System.out.println(e);
			}

		}

		/*
		 * Checks for the length Makes sure that the string is alphanumeric
		 */
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

	}
}