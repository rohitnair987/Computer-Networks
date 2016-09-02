import java.io.*;
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

		/*
		 * to find matching teleport point calculate a dict of tele points pass
		 * it to the findMatching
		 */

		if (args.length == 0) {
			System.out.println("Please enter an argument (task number) as 1, 2, 3 or 4");
		}
		else {
			String task = args[0];

			char maze[][] = getMazeData();
			if (maze == null) {
				System.out.println("NO");
			}
			else {
				//display maze:
				// for (int i = 0; i < maze.length; i++) {
				// for (int j = 0; j < maze[0].length; j++) {
				// System.out.print(maze[i][j]);
				// }
				// System.out.println();
				// }
				
				// ********* Task 1 *********
				if (task.equals("1")) {
					System.out.println(isMazeLegal(maze) ? "YES" : "NO");
				}

				// ********* Task 2 *********
				else if (task.equals("2")) {
					System.out.println(doesSolutionExist(maze) ? "YES" : "NO");
				}

				// ********* Task 3 *********
				else if (task.equals("3")) {
					if (doesSolutionExist(maze)) {
						shortestPathBFS(maze);
					}
					else {
						System.out.println("NO");
					}
				}

				// ********* Task 4 *********
				else if (task.equals("4")) {
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

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				System.out.println(p.path);
				break;
			}

			if (isNumber(maze[p.x][p.y])) {
				String path = p.path;
				p = findMatchingPair(maze, p);
				p.path = path;
			}

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
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
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

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				System.out.println(p.path);
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
		int sourceCount = 0, destinationCount = 0;
		int noOfColumns = grid[0].length;
		for (int i = 0; i < grid.length; i++) {
			if (grid[i].length != noOfColumns) {
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

	private static char[][] getMazeData() {
		char grid[][];
		ArrayList<String> fileContents = new ArrayList<String>();

		int c = 0;
		char line = 'z';
		char last = 'y';
		String inputLine = "";

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				c = br.read();
				line = (char) c;
				
				// Incorrect char in input such as ' ', d etc.
				if (c != -1 && !isValid(line)) { 
					return null;
				}
				// Blank line
				if (line == '\n' && last == '\n') {
					return null;
				}
				// EOF reached - break
				if (c == -1) {
					break;
				}
				last = line;

				if (line != '\n') {
					inputLine += line;
				}
				else {
					fileContents.add(inputLine);
					inputLine = "";
				}
			}

			if (last != '\n') { // if last line doesn've have a CR
				return null;
			}

			grid = new char[fileContents.size()][fileContents.get(0).length()];
			for (int i = 0; i < fileContents.size(); i++) {
				grid[i] = fileContents.get(i).toCharArray();
			}

			return grid;
		} catch (IOException e) {
			System.out.println("Exception occured. Below is its Stack Trace:");
			e.printStackTrace();
		}
		
		return null;
	}

	private static boolean isValid(char last) {
		if (last == '#' || last == '.' || last == 'S' || last == 'D' || last == '\n' || last == '?' || isNumber(last)) {
			return true;
		}
		return false;
	}

}
