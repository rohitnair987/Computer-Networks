import java.io.BufferedReader;
import java.io.DataInputStream;
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

				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// Request a name from this client. Keep requesting until
				// a name is submitted that is not already used. Note that
				// checking for the existence of a name and adding the name
				// must be done while locking the set of names.
//				while (true) 
				{
//					int len = Integer.parseInt(in.readLine());
//					System.out.println(len);
					
//					System.out.print(in.read() + " ");
//					System.out.print(in.read() + " ");
//					System.out.print(in.read() + " ");
//					System.out.print(in.read() + " ");
//					System.out.print(in.read() + " ");
//					System.out.print(in.read() + " ");
					
					DataInputStream dIn = new DataInputStream(socket.getInputStream());

					int length = dIn.readInt();                    // read length of incoming message
					System.out.println(length);
					if(length>0) {
					    byte[] message = new byte[length];
					    dIn.readFully(message, 0, message.length); // read the message
					    for (int i = 0 ; i < 10; i++) {
							System.out.print(message[i] + " ");
						}
					    System.out.println();

						FileOutputStream fos = new FileOutputStream("deeps.jpg");
						fos.write(message);
						fos.close();
//					    System.out.println(message.toString());
					}
					
					
					
//					Thread.sleep(1000);
					
//					len = 1;
//					
//					byte[] fileContent = new byte[len];
//					for (int i = 0; i < len; i++) {
//						fileContent[i] = (byte) in.read();
//						System.out.println("received: " + fileContent[i]);
//					}
//					
//					System.out.println("asd");
					
//					FileOutputStream fos = new FileOutputStream("f.jpg");
//					byte[] fileContent = in.read(socket.getInputStream())
//					ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
//					System.out.println(oin);
//                    BufferedImage screenshot = ImageIO.read(oin);
//					System.out.println(screenshot);
//                    ImageIO.write(screenshot, "jpg", new File("screenshot.jpg"));

//                    fos.write(fileContent);
//					fos.close();
//					InputStreamReader b = new InputStreamReader(socket.getInputStream());
//					System.out.println(in.readLine());
//					b.read();
//					String s = in.readLine();
//					BufferedImage img = ImageIO.read(b);
//					BufferedImage img = ImageIO.read(socket.getInputStream());
//					BufferedImage img = ImageIO.read(s);
//					System.out.println("yayy");
//					System.out.println(img);
//					System.exit(0);
//					ImageIO.write(img, "jpg", new File("deepika2.jpg"));
					
//					event = in.readLine();
//					System.out.println("from " + currentUser + ": " + event);
//					events.add(event);
//					
//					if (event == null) {
//						if (!currentUser.isEmpty()) {
//							activeUsers.remove(currentUser);
//						}
//						socket.close();
//						return;
//					}
					
//					if(event.startsWith("img")) {
//						System.out.println(event.substring(4));
//						RenderedImage i = new Render
//						ImageIO.write(new Render event.substring(4), "jpg", new File("deepika2.jpg"));
//					}
					
					System.exit(0);
//					if(true)
//						continue;
					
//					if()
					

					String words[] = event.split(" ");

					String command = words[0].toLowerCase();

					switch (command) {

					case "r":
					case "register":
						
						//To-do: check if already online

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
						// newly registered user automatically logs in
//						break;

					case "l":
					case "login":
						
						// To-do: prevent login on another m/c thru the same uname

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
										activeUsers.put(words[1], socket);
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

						
					case "lo":
					case "logout":
						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("You're not logged in");
						}

						else {
							out.println("See ya!");
							activeUsers.remove(currentUser);
						}
						break;

						
					// To-do: 2 different commands:
						// 1. send user msg with spaces
						// 2. broadcast msg with spaces
					case "s":
					case "send":
						// To-do: msg length max 4096 bytes
						// To-do: msg containing spaces
						System.out.println("refreshing");
//						refreshActiveUserList();
						System.out.println("refreshed");

						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("Please login. Register if you do not have an account");
						}

						else {
							if (words.length == 2) {
								for (Entry<String, Socket> targetEntry : activeUsers.entrySet()) {
									if (targetEntry.getKey() != currentUser) {
										new PrintWriter(targetEntry.getValue().getOutputStream(), true)
												.println(currentUser + ": " + words[1]);
									}
								}
							} else if (words.length == 3) {
								Socket targetUser = activeUsers.get(words[1]);
								if (targetUser == null) {
									out.println(words[1] + " is inactive. Please try later.");
								} else {
									new PrintWriter(targetUser.getOutputStream(), true)
											.println(currentUser + ": " + words[2]);
								}
							} else {
								out.println("Invalid send command. Please try again.");
							}
						}
						break;

					case "sa":
					case "senda":
						// To-do: cannot send private msg to oneself
//						refreshActiveUserList();
						System.out.println("refreshed");

						if (currentUser.isEmpty() || !isUserLoggedIn(currentUser)) {
							out.println("Please login. Register if you do not have an account");
						}

						else {
							if (words.length == 2) {
								activeUsers.forEach((user, sock) -> {
									if(user != currentUser) {
										try {
											new PrintWriter(sock.getOutputStream(), true).println(words[1]);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
								
							} else if (words.length == 3) {
								Socket targetUser = activeUsers.get(words[1]);
								if (targetUser == null) {
									out.println(words[1] + " is inactive. Please try later.");
								} else {
									new PrintWriter(targetUser.getOutputStream(), true).println(words[2]);
								}
							} else {
								out.println("Invalid send command. Please try again.");
							}
						}
						break;

					case "list":
//						refreshActiveUserList();
						System.out.println("refreshed");

						activeUsers.forEach((k, v) -> out.println(k));

						break;

						
						
					case "whoami":
						// To-do
						break;
						
						
//					case "img":
//					case "image":
//						break;
						
						
					default:
						out.println("Invalid command. Please try again.");
					}
					
					

				}

			} catch (Exception e) {
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