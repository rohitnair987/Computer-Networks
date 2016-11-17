import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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
	private static Map<String, Socket> activeUsers = new HashMap<>();

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

		// check constantly if a user is alive
		// for (Entry<String, PrintWriter> entry : activeUsers.entrySet()) {
		// new Thread(new Pinger(activeUsers, entry)).start();
		// }

		// Thread userPinger = new Thread(new Pinger(activeUsers));
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
		 * 
		 * @throws IOException
		 */
		public Handler(Socket socket) throws IOException {
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

				// To-do: check .close() et al

				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				Socket targetUser = null;

				// Request a name from this client. Keep requesting until
				// a name is submitted that is not already used. Note that
				// checking for the existence of a name and adding the name
				// must be done while locking the set of names.
				while (true) {
					event = in.readLine();
					System.out.println(currentUser + ": " + event);

					// Remove the client from the active list when disconnected
					// and log it
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

					case "img":
					case "image":

						// User logged in?
						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("Please login to send an image");
						}

						else {

							if (words.length != 3) {
								out.println("Invalid " + command + " command.");
							} else {
								targetUserName = words[1];

								// Current user logged in?
								if (currentUser.equals(targetUserName)) {
									out.println("Can't send an image to yourself");
								}

								else {
									targetUser = activeUsers.get(targetUserName);

									if (targetUser == null) {
										out.println(targetUserName + " is inactive. Please try later.");
									}

									// Indicate to the client that the command
									// is correct
									else {
										String imgName = words[2];
										out.println("sendImg " + targetUserName + " " + imgName);
									}
								}
							}
						}

						break;

					case "imgData":

						targetUserName = words[1];
						String imgName = words[2];
						int length = Integer.parseInt(words[3]);
						targetUser = activeUsers.get(targetUserName);

						PrintWriter targetOut = new PrintWriter(targetUser.getOutputStream(), true);

						// Reading the image
						DataInputStream dIn = new DataInputStream(socket.getInputStream());

						if (length > 0) {
							// read from current socket
							byte[] fileContent = new byte[length];
							dIn.readFully(fileContent, 0, length);

							// forward to target socket
							targetOut.println("imgData " + currentUser + " " + imgName + " " + length);
							DataOutputStream dOut = new DataOutputStream(targetUser.getOutputStream());
							dOut.write(fileContent);
						}

						break;

					case "r":
					case "register":

						// To-do: check if already online
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

								// String res = checkString(userName);
								// if (!res.equals("Yes")) {
								// out.println(res);
								// break;
								// }
								//
								// res = checkString(password);
								// if (!res.equals("Yes")) {
								// out.println(res);
								// break;
								// }

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

							// Client online?
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
										
										if (activeUsers.size() >= 16) {
											out.println("Server full. Please come back later");
										}
										else {
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

					// To-do: msg length max 4096 bytes
					case "b":
					case "broadcast":
					case "ba":
					case "broadcasta":
					case "s":
					case "send":
					case "sa":
					case "senda":

						if (currentUser.isEmpty()) {
							out.println("Please login to send messages. Register if you do not have an account");
						}

						else {
							if (words.length < 2) {
								out.println("Invalid " + command + " command. Please try again.");
							}

							else {

								StringBuffer msg = new StringBuffer();
								Socket targetSocket = null;

								if (command.startsWith("s")) {
									if (words.length < 3) {
										out.println("Invalid " + command + " command. Please try again.");
										break;
									}
									targetUserName = words[1];
									targetSocket = activeUsers.get(targetUserName);

									if (targetSocket == null) {
										out.println(targetUserName + " is inactive. Please try later.");
										break;
									}
									msg.append(event.substring(command.length() + targetUserName.length() + 2));
								} else {
									msg.append(event.substring(command.length() + 1));
								}

								if (!command.endsWith("a")) {
									msg.insert(0, currentUser + ": ");
								}

								if (msg.length() > 4096) {
									out.println("Your message is too long");
								}

								else {

									// Broadcast the msg
									if (command.startsWith("b")) {
										for (Entry<String, Socket> targetEntry : activeUsers.entrySet()) {
											if (targetEntry.getKey() != currentUser) {
												new PrintWriter(targetEntry.getValue().getOutputStream(), true)
														.println(msg);
											}
										}
									}

									// Send a one-to-one msg
									else {
										new PrintWriter(targetUser.getOutputStream(), true).println(msg);
									}
								}
							}

						}

						break;

					case "list":
						activeUsers.forEach((k, v) -> out.println(k));
						break;

					case "whoami":
						out.println(!currentUser.isEmpty() ? currentUser : "You're not logged in");
						break;

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

		private void refreshActiveUserList() {
			try {

				for (Entry<String, Socket> entry : activeUsers.entrySet()) {
					new PrintWriter(entry.getValue().getOutputStream(), true).write((byte) '\n');
					int ch = new BufferedReader(new InputStreamReader(entry.getValue().getInputStream())).read();
					if (ch != '\n') {
						activeUsers.remove(entry.getKey());
						entry.getValue().close();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
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

		private boolean isUserLoggedIn(String userName) {
			if (activeUsers.containsKey(userName)) {
				return true;
			}
			return false;
		}

	}
}