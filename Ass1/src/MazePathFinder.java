import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

class Point {
	int x;
	int y;
	char direction;

	public Point() {
		x = 0;
		y = 0;
		direction = 'x';
	}

	public Point(int a, int b) {
		x = a;
		y = b;
		direction = 'x';
	}

	public Point(int a, int b, char d) {
		x = a;
		y = b;
		direction = d;
	}
}

public class MazePathFinder {
	public static void main(String[] args) {
		// String file = args[0];
		// testing
		String file = "testdata/task3.in.1";
		System.out.println("File: " + file + "\n");

		char maze[][] = getFileData(file);
		display(maze);

		// ********* Task 1 *********
		 System.out.print("\nTask 1: Is the Maze Legal?: ");
		 System.out.println(isMazeLegal(maze) ? "YES" : "NO");
		 // ********* Task 1 *********
		
		 // ********* Task 2 *********
		 System.out.print("\nTask 2: Is there a solution?: ");
		 System.out.println(doesSolutionExist(maze) ? "YES" : "NO");
		 // ********* Task 2 *********
		
		 // ********* Task 3 *********
		 System.out.println("\nTask 3: Shortest path to destination: ");
		 shortestPathBFS(maze);
		// ********* Task 3 *********

		// ********* Task 4 *********
//		System.out.println("\nTask 4: Shortest path with Teleporter: ");
//		shortestPathTeleporter(maze);
		// ********* Task 4 *********

	}

	private static void shortestPathTeleporter(char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		q.add(findSource(maze));
		boolean visited[][] = new boolean[m][n];
		Point parent[][] = new Point[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			System.out.println(p.x + " " + p.y + " " + p.direction + ", maze: " + maze[p.x][p.y]);

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				ArrayList<String> path = new ArrayList<String>();
				while (maze[p.x][p.y] != 's' && maze[p.x][p.y] != 'S') {
					path.add(Character.toString(Character.toUpperCase(p.direction)));
					p = parent[p.x][p.y];
				}

				Collections.reverse(path);
				for (String s : path) {
					System.out.print(s);
				}
				System.out.println();
				break;
			}

			if (isNumber(maze[p.x][p.y])) {
				System.out.println("teleport to " + findMatchingPair(maze, p).x + " " + findMatchingPair(maze, p).y);
				p = findMatchingPair(maze, p);
			}

			// addNeighbors(q, p, visited, parent, maze);
			visited[p.x][p.y] = true;
			if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'd' || maze[p.x + 1][p.y] == 'D' || isNumber(maze[p.x + 1][p.y]))) {
				parent[p.x + 1][p.y] = p;
				q.add(new Point(p.x + 1, p.y, 'd'));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'd' || maze[p.x - 1][p.y] == 'D' || isNumber(maze[p.x - 1][p.y]))) {
				parent[p.x - 1][p.y] = p;
				q.add(new Point(p.x - 1, p.y, 'u'));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'd' || maze[p.x][p.y + 1] == 'D' || isNumber(maze[p.x][p.y + 1]))) {
				parent[p.x][p.y + 1] = p;
				q.add(new Point(p.x, p.y + 1, 'r'));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'd' || maze[p.x][p.y - 1] == 'D' || isNumber(maze[p.x][p.y - 1]))) {
				parent[p.x][p.y - 1] = p;
				q.add(new Point(p.x, p.y - 1, 'l'));
			}
		}
	}

	private static Point findMatchingPair(char[][] maze, Point p) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j] == maze[p.x][p.y] && i != p.x && j != p.y) {
					return new Point(i, j);
				}
			}
		}
		return null;
	}

	private static boolean isNumber(char c) {
		if (c >= '1' && c <= '9') {
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
		Point parent[][] = new Point[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				return true;
			}

			addNeighbors(q, p, visited, parent, maze);
		}
		return false;
	}

	private static void shortestPathBFS(char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		q.add(findSource(maze));
		boolean visited[][] = new boolean[m][n];
		Point parent[][] = new Point[m][n];

		while (!q.isEmpty()) {
			Point p = q.remove();
			// System.out.println(p.x + " " + p.y + " " + p.direction + ", maze:
			// " + maze[p.x][p.y]);

			if (maze[p.x][p.y] == 'd' || maze[p.x][p.y] == 'D') {
				ArrayList<String> path = new ArrayList<String>();
				while (maze[p.x][p.y] != 's' && maze[p.x][p.y] != 'S') {
					path.add(Character.toString(Character.toUpperCase(p.direction)));
					p = parent[p.x][p.y];
				}

				Collections.reverse(path);
				for (String s : path) {
					System.out.print(s);
				}
				System.out.println();
				break;
			}

			addNeighbors(q, p, visited, parent, maze);
		}
	}

	private static void addNeighbors(Queue<Point> q, Point p, boolean[][] visited, Point[][] parent, char[][] maze) {
		int m = maze.length;
		int n = maze[0].length;
		visited[p.x][p.y] = true;
		if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'd' || maze[p.x + 1][p.y] == 'D')) {
			parent[p.x + 1][p.y] = p;
			q.add(new Point(p.x + 1, p.y, 'd'));
		}
		if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'd' || maze[p.x - 1][p.y] == 'D')) {
			parent[p.x - 1][p.y] = p;
			q.add(new Point(p.x - 1, p.y, 'u'));
		}
		if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'd' || maze[p.x][p.y + 1] == 'D')) {
			parent[p.x][p.y + 1] = p;
			q.add(new Point(p.x, p.y + 1, 'r'));
		}
		if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'd' || maze[p.x][p.y - 1] == 'D')) {
			parent[p.x][p.y - 1] = p;
			q.add(new Point(p.x, p.y - 1, 'l'));
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
		if (grid == null || grid.length == 0) {
			System.out.print("Matrix Empty ");
			return false;
		}
		boolean isSourcePresent = false, isDestinationPresent = false;
		int noOfColumns = grid[0].length;
		for (int i = 0; i < grid.length; i++) {
			// System.out.println(grid[i].length);
			if (grid[i].length != noOfColumns) {
				System.out.print("Not an m*n matrix ");
				return false;
			}
			for (int j = 0; j < noOfColumns; j++) {
				if (grid[i][j] == 's' || grid[i][j] == 'S') {
					isSourcePresent = true;
				}
				else if (grid[i][j] == 'd' || grid[i][j] == 'D') {
					isDestinationPresent = true;
				}
				else if (!(grid[i][j] == '.' || grid[i][j] == '#')) {
					return false;
				}
			}
		}
		return isSourcePresent && isDestinationPresent;
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
		else {
			System.out.println("Nothing to display.");
		}
	}

	private static char[][] getFileData(String file) {
		String line = "";
		char grid[][];
		ArrayList<String> fileContents = new ArrayList<String>();
		boolean emptyLinePresent = false;
		try {
			BufferedReader bIn = new BufferedReader(new FileReader(file));
			while ((line = bIn.readLine()) != null) {
				if (line.trim().isEmpty()) {
					emptyLinePresent = true;
				}
				else {
					if (emptyLinePresent) {
						System.out.println("Illegal file");
						bIn.close();
						return null;
					}
					else {
						fileContents.add(line.trim());
					}
				}
			}
			bIn.close();

			grid = new char[fileContents.size()][fileContents.get(0).toString().length()];
			for (int i = 0; i < fileContents.size(); i++) {
				grid[i] = fileContents.get(i).toString().toCharArray();
			}
			return grid;
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return null;
	}
}
