package com.rohit.networks;

import java.io.IOException;

public class Utils {

	// Converts the hex input string into a decimal value
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
		return val;
	}

	// Converts the hex input string into a decimal value
	public static int hexToDecimal(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		return Character.toUpperCase(c) - 'A' + 10;
	}

	// Reads i number of bytes from the input stream and throws them away
	public static void skipBytes(int i) throws IOException {
		int bytesRead = 0;
		while (bytesRead++ < i) {
			System.in.read();
		}
	}

	// Read i bytes from input stream in Little-Endian style and return as
	// string
	public static String readBytesAsStringLittleEndian(long i) throws IOException {
		int bytesRead = 0;
		StringBuffer length = new StringBuffer();
		while (bytesRead++ < i) {
			String hex = Integer.toHexString(System.in.read());
			length.insert(0, hex);
			if (hex.length() < 2) {
				length.insert(0, "0");
			}
		}
		return length.toString();
	}

	// Read i bytes from input stream in Big-Endian style and return as
	// string
	public static String readBytesAsStringBigEndian(long i) throws IOException {
		int bytesRead = 0;
		StringBuffer length = new StringBuffer();
		while (bytesRead++ < i) {
			String hex = Integer.toHexString(System.in.read());
			if (hex.length() < 2) {
				length.append("0");
			}
			length.append(hex);
		}
		return length.toString();
	}

	// Displays the next i bytes from the input stream
	public static void displayNextIBytes(int i) throws IOException {
		int counter = 0;
		long streamPtr = 0, numberOfColumns = 8;
		boolean space = true;

		while (System.in.available() > 0) {
			if (counter++ >= i)
				break;
			final long col = streamPtr++ % numberOfColumns;
			System.out.printf("%02x", System.in.read());
			if (counter % 2 == 0) {
				System.out.print(" ");
			}
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
		System.out.println();
	}

	// Convert input string to ip address
	public static String stringToIP(String ipString) {
		if (ipString.length() != 8) {
			return "Invalid ipString, cannot generate ip address";
		}

		StringBuffer ip = new StringBuffer();
		for (int i = 0; i < 8; i += 2) {
			ip.append(Utils.hexToDecimal(ipString.substring(i, i + 2)));
			if (i < 6) {
				ip.append(".");
			}
		}

		return ip.toString();
	}

	// Update counts of packet types to output object
	public static void updateOutputValues(PCAPData pcapData, Output out) {
		System.out.println(pcapData.ipHeader.TransportLayerProtocol);
		switch (pcapData.ipHeader.TransportLayerProtocol) {
		case UDP:
			out.Task1.UDPPackets++;
			break;

		case TCP:
			out.Task1.TCPPackets++;
			break;
		default:
			break;
		}
	}

}
