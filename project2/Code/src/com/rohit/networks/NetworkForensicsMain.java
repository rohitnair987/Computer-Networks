package com.rohit.networks;

import java.io.IOException;
import java.util.*;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
		long d1 = new Date().getTime();
		// int taskNumber = Integer.parseInt(args[0]);
		// if(!(taskNumber == 1 || taskNumber == 2 || taskNumber == 3 ||
		// taskNumber == 4)) {
		// System.out.println("Please enter task number (1, 2 , 3 or 4)");
		// System.exit(1);
		// }

		int taskNumber = 2;

		// Skip global header
		Utils.skipBytes(24);
		// PCAPGlobalHeader pcapGlobalHeader =
		// PCAPReader.readPCAPGlobalHeader();
		// System.out.println("Snaplength = " + pcapGlobalHeader.Snaplength);
		// System.out.println("LinkLayerProtocol = " +
		// pcapGlobalHeader.LinkLayerProtocol);

		PCAPHeader pcapHeader = null;
		PCAPData pcapData = null;
		TreeMap<Long, Integer> tcpConnectionPackets = new TreeMap<Long, Integer>();
		Output out = new Output();

		Map<Integer, PCAPData> allPCAPDataPackets = new HashMap<Integer, PCAPData>();

		/*
		 * src.hashcode + dest.hashcode seqNo, packetNo
		 */
		HashMap<Integer, TreeMap<Long, Integer>> tcpConnections = new HashMap<Integer, TreeMap<Long, Integer>>();

		int packetNumber = 1;

		while (System.in.available() > 0) {
			// while (packetNumber < 63) {
			// System.out.println("\n" + packetNumber + ": ");

			// Read PCAP Header of each packet
			pcapHeader = PCAPReader.readPCAPHeader();
			if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
				System.out.println("\nNot a valid packet!");
				// System.exit(1);
			}

			// Read the packet
			pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket, taskNumber);

			// Store the packet
			allPCAPDataPackets.put(packetNumber, pcapData);

			if (pcapData.ipHeader.TransportLayerProtocol == ConstantsEnum.TCP) {
				int key = new StringBuilder().append(pcapData.ipHeader.Source)
						.append(pcapData.transportHeader.SourcePort).toString().hashCode()
						+ new StringBuilder().append(pcapData.ipHeader.Destination)
								.append(pcapData.transportHeader.DestinationPort).toString().hashCode();

				if (!tcpConnections.containsKey(key)) {
					tcpConnectionPackets = new TreeMap<Long, Integer>();
					tcpConnectionPackets.put(pcapData.transportHeader.SeqNum, packetNumber);
					tcpConnections.put(key, tcpConnectionPackets);
				} else {
					tcpConnectionPackets = tcpConnections.get(key);
					tcpConnectionPackets.put(pcapData.transportHeader.SeqNum, packetNumber);
					tcpConnections.replace(key, tcpConnectionPackets);
				}
			}

			Utils.updateOutputValues(pcapData, out, taskNumber);

			packetNumber++;
			// break;
		}
		// System.out.println(tcpConnections.size());

		out.Task1.TotalPackets = packetNumber - 1;
		out.Task1.TCPConnections = tcpConnections.size();

		// if(taskNumber != 1) {
		// Utils.sort(tcpConnections);
		// }

		out.display(taskNumber, allPCAPDataPackets, tcpConnections);

		// System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
		// "ms");
	}

}
