
/************************************************************************************************
    * MazePathFinder -- Program to find the optimal path from source 'S' to destination 'D'.	*   
    * 																							*   
    * Author:  Rohit Nair																		*   
    * 																							*   
    * Sample command to run the program:														*   
    *      java MazePathFinder 1 < ../testdata/task1.in.1 > ../testdata/task1.out.1				*
    *      																						*   
************************************************************************************************/

import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

///<summary>
/// x: x-coordinate of the maze point
/// y: y-coordinate of the maze point
/// path: Path from source to current point
///</summary>
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

	public void display() {
		System.out.print("Point: (" + x + "," + y + ")");
		if (path != "") {
			System.out.print(" and Path: " + path);
		}
		System.out.println();
	}

	public boolean isEqual(Point p) {
		if (p.x == x && p.y == y) {
			return true;
		}
		return false;
	}

}

public class MazePathFinder {
	// static int ROWS = 0;
	// static int COLUMNS = 0;

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Please enter an argument (task number) as 1, 2, 3 or 4");
		}
		else {
			String task = args[0];

			char maze[][] = getMazeData();
			System.out.println("out of getMazeData");
			if (maze == null) {
				System.out.println("getMazeData returned null");
				System.out.println("NO");
			}
			else {

				// ********* Task 1 *********
				if (task.equals("1")) {
					System.out.println("isMazeLegal from main");
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
		Point teleportPoints[][] = findTeleportPoints(maze);

		while (!q.isEmpty()) {
			Point p = q.remove();

			if (maze[p.x][p.y] == 'D') {
				System.out.println(p.path);
				break;
			}

			if (isNumber(maze[p.x][p.y])) {
				String path = p.path;
				p = findMatchingPair(teleportPoints, p, Character.getNumericValue(maze[p.x][p.y]));
				p.path = path;
			}

			visited[p.x][p.y] = true;
			if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D' || isNumber(maze[p.x + 1][p.y]))) {
				q.add(new Point(p.x + 1, p.y, p.path + "D"));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D' || isNumber(maze[p.x - 1][p.y]))) {
				q.add(new Point(p.x - 1, p.y, p.path + "U"));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D' || isNumber(maze[p.x][p.y + 1]))) {
				q.add(new Point(p.x, p.y + 1, p.path + "R"));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D' || isNumber(maze[p.x][p.y - 1]))) {
				q.add(new Point(p.x, p.y - 1, p.path + "L"));
			}
		}
	}

	private static Point[][] findTeleportPoints(char[][] maze) {
		Point teleportPoints[][] = new Point[10][2];

		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (isNumber(maze[i][j])) {
					int digit = Character.getNumericValue(maze[i][j]);
					if (teleportPoints[digit][0] == null) {
						teleportPoints[digit][0] = new Point(i, j);
					}
					else {
						teleportPoints[digit][1] = new Point(i, j);
					}
				}
			}
		}

		return teleportPoints;
	}

	private static Point findMatchingPair(Point[][] teleportPoints, Point p, int value) {
		if (teleportPoints[value][0].isEqual(p)) {
			return teleportPoints[value][1];
		}
		return teleportPoints[value][0];
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
			if (maze[p.x][p.y] == 'D') {
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

			if (maze[p.x][p.y] == 'D') {
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
		if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D')) {
			q.add(new Point(p.x + 1, p.y, p.path + "D"));
		}
		if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D')) {
			q.add(new Point(p.x - 1, p.y, p.path + "U"));
		}
		if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D')) {
			q.add(new Point(p.x, p.y + 1, p.path + "R"));
		}
		if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D')) {
			q.add(new Point(p.x, p.y - 1, p.path + "L"));
		}
	}

	private static Point findSource(char[][] maze) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j] == 'S') {
					return new Point(i, j);
				}
			}
		}
		return new Point();
	}

	private static boolean isMazeLegal(char[][] grid) {
		System.out.println("isMazeLegal");
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
				else if (!(grid[i][j] == '.' || grid[i][j] == '#')) {
					return false;
				}
			}
		}
		return sourceCount == 1 && destinationCount == 1;
	}

	private static char[][] getMazeData() {
		ArrayList<StringBuffer> fileContents = new ArrayList<StringBuffer>();

		int rows = 5000;
		int columns = 5000;
		char grid[][] = new char[rows][columns];

		int c = 0;
		char line = 'z';
		char last = 'y';
		String inputLine = "";
		StringBuffer everything = new StringBuffer();
		// String everything = "";

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			Date d1 = new Date();

			// int count = 0;
			// while ((inputLine = br.readLine()) != null)
			//// System.out.println(count++ + ": " + inputLine);
			// count++;
			// System.out.println(count);

			int rowNumber = 0;
			int columnNumber = 0;
			int count = 0;
			int charCount = 0;
			while ((c = br.read()) != -1) {
				// System.out.print(charCount++ + " ");
				line = (char) c;
				// everything.append(String.valueOf(line));
				if (line == '\n') {
					rowNumber++;
					if (columns == 5000) {
						columns = columnNumber;
					}
					if (columnNumber != columns) {
						System.out.println(columnNumber + " " + columns + " ... col unequal");
						return null;
					}

					columnNumber = 0;
					continue;
				}
				grid[rowNumber][columnNumber++] = line;

				// System.out.print(line);

				// Incorrect char in input
				if (!isValid(line)) {
					System.out.println("not valid");
					return null;
				}

				if (line == '\r') {
					System.out.println("return");
					continue;
				}

				// Blank line
				if ((line == '\n' && last == '\n') || (line == '\r' && last == '\r')) {
					System.out.println("blank");
					return null;
				}

				last = line;

				// if (last != '\n' && last != '\r') {
				// continue;// inputLine += line;
				// }
				// else {
				//// System.out.println("count: "+count+" and line: "+
				// everything);
				// fileContents.add(everything);
				// everything=new StringBuffer();
				//// System.out.println("count: "+count+" and line: "+
				// everything);
				//// break;
				// // inputLine = "";
				// }
			}
			br.close();
			Date d2 = new Date();
			System.out.println("read time: " + (d2.getTime() - d1.getTime()) / 1000.0 + " secs");

			rows = rowNumber;
			// COLUMNS = grid[0].length;

			System.out.println("ROWS: " + rows);
			System.out.println("COLUMNS: " + columns);

			char grid2[][] = new char[rows][columns];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					// System.out.print(grid[i][j]);
					grid2[i][j] = grid[i][j];
				}
			}
			Date d3 = new Date();

			// for (int i = 0; i < ROWS; i++) {
			// for (int j = 0; j < COLUMNS; j++) {
			// System.out.print(grid2[i][j]);
			// }
			//// System.out.println();
			// }

			System.out.println("copy time: " + (d3.getTime() - d2.getTime()) / 1000.0 + " secs");

			// System.out.println("cols: " + fileContents.get(0).length());
			// System.out.println("0th index: "+fileContents.get(0));

			if (line != '\n') {
				System.out.println("last line not n");
				return null;
			}

			// grid = new
			// char[fileContents.size()][fileContents.get(0).length()];
			// for (int i = 0; i < fileContents.size(); i++) {
			// grid[i] = fileContents.get(i).toCharArray();
			// }

			return grid2;// null;// grid;
		} catch (IOException e) {
			System.out.println("Exception occured. Below is its Stack Trace:");
			e.printStackTrace();
		}

		return null;
	}

	private static boolean isValid(char last) {
		if (last == '#' || last == '.' || last == 'S' || last == 'D' || last == '\n' || last == '\r' || last == '?' || isNumber(last)) {
			return true;
		}
		return false;
	}

}
