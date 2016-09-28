package com.rohit.networks;

import java.io.*;
import java.util.*;

import com.rohit.networks.PCAPReader;
import com.rohit.networks.PCAPGlobalHeader;
import com.rohit.networks.PCAPHeader;
import com.rohit.networks.PCAPData;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
		// long d1 = new Date().getTime();

		PCAPGlobalHeader pcapGlobalHeader = PCAPReader.readPCAPGlobalHeader();
		// System.out.println("Snaplength = " + pcapGlobalHeader.Snaplength);
		// System.out.println("LinkLayerProtocol = " +
		// pcapGlobalHeader.LinkLayerProtocol);

		PCAPHeader pcapHeader;
		PCAPData pcapData;

		int packetNumber = 1;
		// start loop here
		while (System.in.available() > 0) {
			System.out.println("Packet" + packetNumber++);

			pcapHeader = PCAPReader.readPCAPHeader();
			System.out.println(pcapHeader.NumberOfOctetsOfPacket + " " + pcapHeader.ActualLengthOfPacket);

			// Utils.displayNextIBytes(pcapHeader.ActualLengthOfPacket);
			Utils.skipBytes(pcapHeader.ActualLengthOfPacket);

			// pcapData = PCAPReader.readPCAPData();
		}
		// end loop here

		// System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
		// "ms");
	}

}
