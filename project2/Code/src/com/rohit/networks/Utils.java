package com.rohit.networks;

import java.io.*;

public class Utils {

	public static long hexToDecimal(String s) {
		s = s.toUpperCase();
		long val = 0;
		int digit = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				digit = Character.getNumericValue(c);
			} else {
				digit = (c - 'A') + 10;
			}
			val = 16 * val + digit;
		}
		// System.out.println("val = " + val);
		return val;
	}

	public static void skipBytes(int i) throws IOException {
		int bytesRead = 0;
		while (bytesRead++ < i) {
			System.in.read();
			// System.out.printf("%02x ", System.in.read());
		}
	}

	public static String readBytesAsStringLittleEndian(int i) throws IOException {
		int bytesRead = 0;
		String snapLength = "";
		while (bytesRead++ < 4) {
			String hex = Integer.toHexString(System.in.read());
			if (hex.length() < 2) {
				hex = "0" + hex;
			}
			snapLength = hex + snapLength;
		}
		// System.out.println("snapLength at readBytesAsStringLittleEndian = " +
		// snapLength);
		return snapLength;
	}

	public static void displayNextIBytes(int i) throws IOException {
		int counter = 0;
		long streamPtr = 0, numberOfColumns = 8;
		boolean space = true;

		while (System.in.available() > 0) {
			if (counter++ >= i)
				break;
			final long col = streamPtr++ % numberOfColumns;
			System.out.printf("%02x ", System.in.read());
			if (col == (numberOfColumns - 1)) {
				streamPtr = 0;
				if (space) {
					System.out.print(" ");
					space = false;
				} else {
					System.out.printf("\n");
					space = true;
				}
			}

		}
	}

}
