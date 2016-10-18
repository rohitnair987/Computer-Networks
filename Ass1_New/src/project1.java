
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

public class project1 {

	public static void main(String[] args) {

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

			if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D' || isNumber(maze[p.x + 1][p.y]))) {
				visited[p.x+1][p.y] = true;
				q.add(new Point(p.x + 1, p.y, p.path + "D"));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D' || isNumber(maze[p.x - 1][p.y]))) {
				visited[p.x-1][p.y] = true;
				q.add(new Point(p.x - 1, p.y, p.path + "U"));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D' || isNumber(maze[p.x][p.y + 1]))) {
				visited[p.x][p.y+1] = true;
				q.add(new Point(p.x, p.y + 1, p.path + "R"));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D' || isNumber(maze[p.x][p.y - 1]))) {
				visited[p.x][p.y-1] = true;
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
			visited[p.x+1][p.y] = true;
			q.add(new Point(p.x + 1, p.y, p.path + "D"));
		}
		if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D')) {
			visited[p.x-1][p.y] = true;
			q.add(new Point(p.x - 1, p.y, p.path + "U"));
		}
		if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D')) {
			visited[p.x][p.y+1] = true;
			q.add(new Point(p.x, p.y + 1, p.path + "R"));
		}
		if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D')) {
			visited[p.x][p.y-1] = true;
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
		int rows = 5000;
		int columns = 5000;
		char grid[][] = new char[rows][columns];

		int inputCharValue = 0;
		char inputChar = 'z';
		char previousInputChar = 'y';

		int rowNumber = 0;
		int columnNumber = 0;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {

			while ((inputCharValue = br.read()) != -1) {
				inputChar = (char) inputCharValue;
				if (inputChar == '\n') {
					rowNumber++;
					if(rowNumber > 5000){
						return null;
					}
					if (columns == 5000) {
						columns = columnNumber;
					}
					if (columnNumber != columns) {
						return null;
					}

					columnNumber = 0;
					continue;
				}
				grid[rowNumber][columnNumber++] = inputChar;
				if(columnNumber > 5000){
					return null;
				}

				// Incorrect char in input
				if (!isValid(inputChar)) {
					return null;
				}

				// Required for cross-platform support
				if (inputChar == '\r') {
					continue;
				}

				// Blank line
				if ((inputChar == '\n' && previousInputChar == '\n') || (inputChar == '\r' && previousInputChar == '\r')) {
					return null;
				}

				previousInputChar = inputChar;

			}
			br.close();

			rows = rowNumber;

			// Minimal grid to return correct input
			char grid2[][] = new char[rows][columns];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					grid2[i][j] = grid[i][j];
				}
			}

			if (inputChar != '\n') {
				return null;
			}

			return grid2;
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
