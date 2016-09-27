package com.rohit.networks;

import java.util.*;
import java.io.*;

public class PCAPFileReader {

	public static int readPCAPFile() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// byte[] data = packet.getByteArray(0, packet.size());

		// int i = 0;
		// System.out.println("Available bytes at d strt:" +
		// System.in.available());
		// while (System.in.available() > 0) {
		// if (i++ > 10) {
		// break;
		// }
		// int b = System.in.read();
		// // System.out.println("Available bytes :" + System.in.available());
		// System.out.println(b + " " + Integer.toHexString(b));
		// }

		int i = 0;
		long streamPtr = 0, numberOfColumns = 8;
		boolean space = true;
		while (System.in.available() > 0) {
			if (i++ > 63)
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
//			break;
			
		}

		br.close();
		return 0;
	}
}
