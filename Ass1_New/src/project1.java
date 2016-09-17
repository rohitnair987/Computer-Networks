
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
	int x = 0;
	int y = 0;
//	StringBuffer path = new StringBuffer(25000);
	char direction =' ';

	public Point() {
//		x = 0;
//		y = 0;
//		path = "";
	}

	public Point(int a, int b) {
		x = a;
		y = b;
//		path = "";
	}

	public Point(int a, int b, char p) {
		x = a;
		y = b;
		direction = p;
	}

//	public void display() {
//		System.out.print("Point: (" + x + "," + y + ")");
//		if (path.length() != 0) {
//			System.out.print(" and Path: " + path);
//		}
//		System.out.println();
//	}

	public boolean isEqual(Point p) {
		if (p.x == x && p.y == y) {
			return true;
		}
		return false;
	}

}

public class project1 {

//	private static Point source;
//	private static int m;
//	private static int n;

	public static void main(String[] args) {
		long d1 = new Date().getTime();
//		System.out.println("aaa");
		if (args.length == 0) {
			System.out.println("Please enter an argument (task number) as 1, 2, 3 or 4");
		} else {
			String task = args[0];

			char maze[][] = getMazeData();
			if (maze == null) {
				System.out.println("NO");
			} else {

				// ********* Task 1 *********
				if (task.equals("1")) {
					System.out.println(isMazeLegal(maze) ? "YES" : "NO");
				}

				// ********* Task 2 *********
//				else if (task.equals("2")) {
//					System.out.println(doesSolutionExist(maze) ? "YES" : "NO");
//				}

				// ********* Task 3 *********
				else if (task.equals("3")) {
//					if (doesSolutionExist(maze)) {
						shortestPathBFS(maze);
//					} else {
//						System.out.println("NO");
//					}
				}

				// ********* Task 4 *********
//				else if (task.equals("4")) {
//					shortestPathTeleporter(maze);
//				} else {
//					System.out.println("Please enter first argument (task number) as 1, 2, 3 or 4");
//				}
			}
		}
		long d2 = new Date().getTime();

		System.out.println("Time taken = " + (d2 - d1) + "ms");
	}
/*
	private static void shortestPathTeleporter(char[][] maze) {
		System.out.println("shortestPathTeleporter");
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
				StringBuffer path = p.path;
				p = findMatchingPair(teleportPoints, p, Character.getNumericValue(maze[p.x][p.y]));
				p.path = path;
			}

			visited[p.x][p.y] = true;
			if (p.x + 1 < m && !visited[p.x + 1][p.y]
					&& (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D' || isNumber(maze[p.x + 1][p.y]))) {
				q.add(new Point(p.x + 1, p.y, p.path.append("D")));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y]
					&& (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D' || isNumber(maze[p.x - 1][p.y]))) {
				q.add(new Point(p.x - 1, p.y, p.path.append("U")));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1]
					&& (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D' || isNumber(maze[p.x][p.y + 1]))) {
				q.add(new Point(p.x, p.y + 1, p.path.append("R")));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1]
					&& (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D' || isNumber(maze[p.x][p.y - 1]))) {
				q.add(new Point(p.x, p.y - 1, p.path.append("L")));
			}
		}
	}

	private static Point[][] findTeleportPoints(char[][] maze) {
		System.out.println("findTeleportPoints");
		Point teleportPoints[][] = new Point[10][2];

		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (isNumber(maze[i][j])) {
					int digit = Character.getNumericValue(maze[i][j]);
					if (teleportPoints[digit][0] == null) {
						teleportPoints[digit][0] = new Point(i, j);
					} else {
						teleportPoints[digit][1] = new Point(i, j);
					}
				}
			}
		}

		return teleportPoints;
	}

	private static Point findMatchingPair(Point[][] teleportPoints, Point p, int value) {
		System.out.println("findMatchingPair");
		if (teleportPoints[value][0].isEqual(p)) {
			return teleportPoints[value][1];
		}
		return teleportPoints[value][0];
	}
*/
	
	private static boolean isNumber(char c) {
//		System.out.println("isNumber");
		if (c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}


//	private static boolean doesSolutionExist(char[][] maze) {
//		System.out.println("doesSolutionExist");
//		int m = maze.length;
//		int n = maze[0].length;
//		Queue<Point> q = new LinkedList<Point>();
//		q.add(findSource(maze));
//		boolean visited[][] = new boolean[m][n];
//
//		while (!q.isEmpty()) {
//			Point p = q.remove();
//			if (maze[p.x][p.y] == 'D') {
//				return true;
//			}
//			addNeighbors(q, p, visited, maze);
//		}
//		return false;
//	}

	private static void shortestPathBFS(char[][] maze) {
		System.out.println("shortestPathBFS");
		int m = maze.length;
		int n = maze[0].length;
		Queue<Point> q = new LinkedList<Point>();
		System.out.println("start source search");
		q.add(findSource(maze));
		System.out.println("end source search");
		boolean visited[][] = new boolean[m][n];

		int count=0;
		while (!q.isEmpty()) {
			Point p = q.remove();

			if (maze[p.x][p.y] == 'D') {
//				System.out.println(p.path);
				System.out.println("done");
				break;
			}
//			addNeighbors(q, p, visited, maze);
			visited[p.x][p.y] = true;
			System.out.print("n" + count++ + " ");
			if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D')) {
				q.add(new Point(p.x + 1, p.y, 'D'));
			}
			if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D')) {
				q.add(new Point(p.x - 1, p.y, 'U'));
			}
			if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D')) {
				q.add(new Point(p.x, p.y + 1, 'R'));
			}
			if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D')) {
				q.add(new Point(p.x, p.y - 1, 'L'));
			}
		}
	}

//	private static void addNeighbors(Queue<Point> q, Point p, boolean[][] visited, char[][] maze) {
////		System.out.println("addNeighbors");
//		int m = maze.length;
//		int n = maze[0].length;
//		visited[p.x][p.y] = true;
//		if (p.x + 1 < m && !visited[p.x + 1][p.y] && (maze[p.x + 1][p.y] == '.' || maze[p.x + 1][p.y] == 'D')) {
//			q.add(new Point(p.x + 1, p.y, p.path.append("D")));
//		}
//		if (p.x - 1 >= 0 && !visited[p.x - 1][p.y] && (maze[p.x - 1][p.y] == '.' || maze[p.x - 1][p.y] == 'D')) {
//			q.add(new Point(p.x - 1, p.y, p.path.append("U")));
//		}
//		if (p.y + 1 < n && !visited[p.x][p.y + 1] && (maze[p.x][p.y + 1] == '.' || maze[p.x][p.y + 1] == 'D')) {
//			q.add(new Point(p.x, p.y + 1, p.path.append("R")));
//		}
//		if (p.y - 1 >= 0 && !visited[p.x][p.y - 1] && (maze[p.x][p.y - 1] == '.' || maze[p.x][p.y - 1] == 'D')) {
//			q.add(new Point(p.x, p.y - 1, p.path.append("L")));
//		}
//	}

	private static Point findSource(char[][] maze) {
		System.out.println("findSource");
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
				} else if (grid[i][j] == 'D') {
					destinationCount++;
				} else if (!(grid[i][j] == '.' || grid[i][j] == '#')) {
					return false;
				}
			}
		}
		return sourceCount == 1 && destinationCount == 1;
	}

	private static char[][] getMazeData() {
		System.out.println("getMazeData");
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
					if (rowNumber > 5000) {
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
				if (columnNumber > 5000) {
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
				if ((inputChar == '\n' && previousInputChar == '\n')
						|| (inputChar == '\r' && previousInputChar == '\r')) {
					return null;
				}
				
//				if(inputChar == 'S'){
//					source = new Point()
//				}

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
//		System.out.println("isValid");
		if (last == '#' || last == '.' || last == 'S' || last == 'D' || last == '\n' || last == '\r' || last == '?'
				|| isNumber(last)) {
			return true;
		}
		return false;
	}

}
