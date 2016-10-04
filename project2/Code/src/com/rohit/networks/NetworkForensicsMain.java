package com.rohit.networks;

import java.io.IOException;
import java.util.Date;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
//		 long d1 = new Date().getTime();

		PCAPGlobalHeader pcapGlobalHeader = PCAPReader.readPCAPGlobalHeader();
		// System.out.println("Snaplength = " + pcapGlobalHeader.Snaplength);
		// System.out.println("LinkLayerProtocol = " +
		// pcapGlobalHeader.LinkLayerProtocol);

		PCAPHeader pcapHeader;
		PCAPData pcapData;
		Output out = new Output();

		int packetNumber = 1;

		while (System.in.available() > 0) {
//			 while (packetNumber < 20) {
			// System.out.println("Packet" + packetNumber++);
			System.out.print("\n" + packetNumber + ": ");
			pcapHeader = PCAPReader.readPCAPHeader();

			// System.out.println(pcapHeader.NumberOfOctetsOfPacket + " " +
			// pcapHeader.ActualLengthOfPacket);

			if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
				System.out.println("\nNot a valid packet!");
				System.exit(0);
			}
			// Utils.displayNextIBytes(pcapHeader.ActualLengthOfPacket);
			// Utils.skipBytes(pcapHeader.ActualLengthOfPacket);

			pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket);

			Utils.updateOutputValues(pcapData, out);
			packetNumber++;
			// break;
		}

		out.Task1.TotalPackets = packetNumber - 1;
		// System.out.println("Packets = " + out.Task1.TotalPackets);
		// System.out.println("UDP Packets = " + out.Task1.UDPPackets);
		// System.out.println("TCP Packets = " + out.Task1.TCPPackets);

//		 System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
//		 "ms");
	}

}
