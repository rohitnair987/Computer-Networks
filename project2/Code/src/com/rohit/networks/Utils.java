package com.rohit.networks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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

	// Read i bytes from input stream in Big-Endian style and return as byte
	// array
	public static byte[] readInputAsByteArray(int i) throws IOException {
		byte[] buffer = new byte[i];
		System.in.read(buffer, 0, i);
		return buffer;
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
	public static void updateOutputValues(PCAPData pcapData, Output out, int taskNumber) {
		if (taskNumber == 1) {
			if (pcapData.linkHeader.ipVersion == 4) {
				out.Task1.IPPackets++;
			}

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

	public static String getIP(byte[] buffer, int startInd) {
		StringBuilder sb = new StringBuilder();
		for (int i = startInd; i < startInd + 3; i++) {
			sb.append(buffer[i] & 0xff);
			sb.append(".");
		}
		sb.append(buffer[startInd + 3] & 0xff);
		return sb.toString();
	}

	public static int bytesToInt2(byte[] buffer, int startInd) {
		return ((buffer[startInd] & 0xff) << 8) | (buffer[startInd + 1] & 0xff);
	}

	public static long bytesToInt4(byte[] buffer, int startInd) {
		return ((buffer[startInd] << 24) | (buffer[startInd + 1] & 0xFF) << 16 | (buffer[startInd + 2] & 0xFF) << 8
				| (buffer[startInd + 3] & 0xFF)) & 0xffffffffL;
	}

	public static int getHigherNibble(byte b) {
		return (b & 0xff) >>> 4;
	}

	public static int getLowerNibble(byte b) {
		return b & 0x0f;
	}

	public static String bytesToBinaryString(byte[] buffer, int startInd, int bytes) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < bytes) {
			sb.append(String.format("%8s", Integer.toBinaryString(buffer[startInd + i] & 0xFF)).replace(' ', '0'));
			i++;
		}
		return sb.toString();
	}

	public static String ByteArrayToString(byte[] buffer, int startInd, int numberOfBytes) {
		if (numberOfBytes == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < numberOfBytes) {
			sb.append(String.format("%02x ", (buffer[startInd + i]) & 0xff));
			i++;
		}
		return sb.toString();
	}

	public static int getNoOfBytes(TCPConnectionTupleAndBytes tuple, char type) {
		ArrayList<byte[]> stream;
		if (type == 'u') {
			stream = tuple.upstreamBytes;
		} else {
			stream = tuple.downstreamBytes;
		}

		int length = 0;
		for (byte[] b : stream) {
			length += b.length;
		}

		return length;
	}

	public static int stringToNumber(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static TreeMap<Long, TCPConnectionTupleAndBytes> sortByTimeStamp(
			HashMap<Integer, TCPConnectionTupleAndBytes> upLink) {
		
		TreeMap<Long, TCPConnectionTupleAndBytes> upLinksSortedByTimeStamp = new TreeMap<Long, TCPConnectionTupleAndBytes>();
		for(TCPConnectionTupleAndBytes t : upLink.values()) {
			upLinksSortedByTimeStamp.put(t.PacketTimeStamp, t);
		}
		
		return upLinksSortedByTimeStamp;
	}

}
