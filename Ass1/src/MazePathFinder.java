import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class Point {
	int x;
	int y;
	String path;

	public Point() {
		x = 0;
		y = 0;
		path = "";
	}

	public Point(int a, int b) {
		x = a;
		y = b;
		path = "";
	}

	public Point(int a, int b, String p) {
		x = a;
		y = b;
		path = p;
	}
}

public class MazePathFinder {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please enter an argument (task number) as 1, 2, 3 or 4");
		}
		else {
			String task = args[0];
			// Scanner cIn = new Scanner(System.in);
			// String file = cIn.next();
			// cIn.close();
			// String file = "testdata/task1.in.1";
			String file = args[1];
			System.out.println("File: " + file + "\n");

			char maze[][] = getFileData(file);
			if (maze == null) {
				System.out.println("NO");
			}
			else {
				// display(maze);

				// ********* Task 1 *********
				if (task.equals("1")) {
					System.out.print("\nTask 1: Is the Maze Legal?: ");
					System.out.println(isMazeLegal(maze) ? "YES" : "NO");
				}

				// ********* Task 2 *********
				else if (task.equals("2")) {
					System.out.print("\nTask 2: Is there a solution?: ");
					System.out.println(doesSolutionExist(maze) ? "YES" : "NO");
				}

				// ********* Task 3 *********
				else if (task.equals("3")) {
					System.out.println("\nTask 3: Shortest path to destination: ");
					shortestPathBFS(maze);
				}

				// ********* Task 4 *********
				else if (task.equals("4")) {
					System.out.println("\nTask 4: Shortest path with Teleporter: ");
					shortestPathTeleporter(maze);
				}
				else {
					System.out.println("Please enter first argument (task number) as 1, 2, 3 or 4");
				}
			}
		}
	}

	private static void shortestPathTeleporter(char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		q.add(findSource(maze));
		boolean visited[][] = new boolean[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			// System.out.print(p.x + " " + p.y + " " + p.path + ", maze: " +
			// maze[p.x][p.y] + " -- Queue: ");
			// Queue<Point> q2 = new LinkedList<Point>(q);
			// while (!q2.isEmpty()) {
			// Point x = q2.remove();
			// System.out.print(x.x + "," + x.y + " ");
			// }
			// System.out.println();

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				// replace this syso by writing to file
				System.out.println(p.path);
				break;
			}

			if (isNumber(maze[p.x][p.y])) {
				String path = p.path;
				p = findMatchingPair(maze, p);
				p.path = path;
				System.out.println("teleport to " + p.x + " " + p.y);
			}

			// addNeighbors(q, p, visited, maze);
			visited[p.x][p.y] = true;
			if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'd' || maze[p.x + 1][p.y] == 'D' || isNumber(maze[p.x + 1][p.y]))) {
				q.add(new Point(p.x + 1, p.y, p.path + "D"));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'd' || maze[p.x - 1][p.y] == 'D' || isNumber(maze[p.x - 1][p.y]))) {
				q.add(new Point(p.x - 1, p.y, p.path + "U"));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'd' || maze[p.x][p.y + 1] == 'D' || isNumber(maze[p.x][p.y + 1]))) {
				q.add(new Point(p.x, p.y + 1, p.path + "R"));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'd' || maze[p.x][p.y - 1] == 'D' || isNumber(maze[p.x][p.y - 1]))) {
				q.add(new Point(p.x, p.y - 1, p.path + "L"));
			}
		}
	}

	private static Point findMatchingPair(char[][] maze, Point p) {
		// char x2 = maze[p.x][p.y];
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				// char x1 = maze[i][j];
				// if (maze[i][j] == maze[p.x][p.y] && i != p.x && j != p.y) {
				if (maze[i][j] == maze[p.x][p.y]) {
					if (!(i == p.x && j == p.y)) {
						return new Point(i, j);
					}
				}
			}
		}
		return null;
	}

	private static boolean isNumber(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}

	private static boolean doesSolutionExist(char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		q.add(findSource(maze));
		boolean visited[][] = new boolean[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				return true;
			}

			addNeighbors(q, p, visited, maze);
		}
		return false;
	}

	private static void shortestPathBFS(char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		q.add(findSource(maze));
		boolean visited[][] = new boolean[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			// System.out.println(p.x + " " + p.y + " " + p.path + ", maze:
			// " + maze[p.x][p.y]);

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				// replace this syso by writing to file
				System.out.println("Final path: " + p.path);
				break;
			}

			addNeighbors(q, p, visited, maze);
		}
	}

	private static void addNeighbors(Queue<Point> q, Point p, boolean[][] visited, char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		visited[p.x][p.y] = true;
		if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'd' || maze[p.x + 1][p.y] == 'D')) {
			q.add(new Point(p.x + 1, p.y, p.path + "D"));
		}
		if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'd' || maze[p.x - 1][p.y] == 'D')) {
			q.add(new Point(p.x - 1, p.y, p.path + "U"));
		}
		if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'd' || maze[p.x][p.y + 1] == 'D')) {
			q.add(new Point(p.x, p.y + 1, p.path + "R"));
		}
		if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'd' || maze[p.x][p.y - 1] == 'D')) {
			q.add(new Point(p.x, p.y - 1, p.path + "L"));
		}
	}

	private static Point findSource(char[][] maze) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j] == 'S' || maze[i][j] == 's') {
					return new Point(i, j);
				}
			}
		}
		return new Point();
	}

	private static boolean isMazeLegal(char[][] grid) {
		// if (grid == null || grid.length == 0) {
		// System.out.print("Matrix Empty ");
		// return false;
		// }
		int sourceCount = 0, destinationCount = 0;
		int noOfColumns = grid[0].length;
		for (int i = 0; i < grid.length; i++) {
			// System.out.println(grid[i].length);
			if (grid[i].length != noOfColumns) {
				System.out.print("Not an m*n matrix ");
				return false;
			}
			for (int j = 0; j < noOfColumns; j++) {
				if (grid[i][j] == 'S') {
					sourceCount++;
				}
				else if (grid[i][j] == 'D') {
					destinationCount++;
				}
				else if (!(grid[i][j] == '.' || grid[i][j] == '#'
				/* || !isNumber(grid[i][j]) */)) {
					return false;
				}
			}
		}
		return sourceCount == 1 && destinationCount == 1;
	}

	private static void display(char[][] grid) {
		if (grid != null) {
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[i].length; j++) {
					System.out.print(grid[i][j]);
				}
				System.out.println();
			}
		}
		// else {
		// System.out.println("Nothing to display.");
		// }
	}

	private static char[][] getFileData(String file) {
		String line = "";
		char grid[][];
		ArrayList<String> fileContents = new ArrayList<String>();

		try {
			if (!isLastCharNewLine(file)) {
				return null;
			}

			BufferedReader bIn = new BufferedReader(new FileReader(file));
			while ((line = bIn.readLine()) != null) {
//				System.out.println(line);
				fileContents.add(line);
			}
			bIn.close();

			grid = new char[fileContents.size()][fileContents.get(0).toString().length()];
			for (int i = 0; i < fileContents.size(); i++) {
				grid[i] = fileContents.get(i).toString().toCharArray();
			}
			return grid;
		} catch (Exception e) {
			// remove this later
			System.out.println("Exception: " + e);
			return null;
		}
	}

	private static boolean isLastCharNewLine(String file) {
		int value = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			char c = ' ';
			while ((value = br.read()) != -1) {
				c = (char) value;
//				System.out.print(c);
			}
			br.close();
			if (c == '\n') {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
