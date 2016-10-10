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
		HashMap<Long, Integer> tcpConnectionPackets = new HashMap<Long, Integer>();
		Output out = new Output();

		Map<Integer, PCAPData> allPCAPDataPackets = new HashMap<Integer, PCAPData>();

		/*
		 * src.hashcode + dest.hashcode seqNo, packetNo
		 */
		HashMap<Integer, HashMap<Long, Integer>> tcpConnections = new HashMap<Integer, HashMap<Long, Integer>>();

		int packetNumber = 1;

		while (System.in.available() > 0) {
			// while (packetNumber < 63) {
			// System.out.println("\n" + packetNumber + ": ");

			// Read PCAP Header of each packet
			pcapHeader = PCAPReader.readPCAPHeader();
			if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
				System.out.println("\nNot a valid packet!");
				System.exit(0);
			}

			// Read the packet
			pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket, taskNumber);

			// Store the packet
			allPCAPDataPackets.put(packetNumber, pcapData);

			if (pcapData.ipHeader.TransportLayerProtocol == ConstantsEnum.TCP) {
				int key = new StringBuilder().append(pcapData.ipHeader.Source).append(":")
						.append(pcapData.transportHeader.SourcePort).toString().hashCode()
						+ new StringBuilder().append(pcapData.ipHeader.Destination).append(":")
								.append(pcapData.transportHeader.DestinationPort).toString().hashCode();

				if (!tcpConnections.containsKey(key)) {
					tcpConnectionPackets = new HashMap<Long, Integer>();
					tcpConnectionPackets.put(pcapData.transportHeader.SeqNum, packetNumber);
					// System.out.println("new:"+tcpConnectionPackets);
					tcpConnections.put(key, tcpConnectionPackets);
				} else {
					tcpConnectionPackets = tcpConnections.get(key);
					// if
					// (!tcpConnectionPackets.containsKey(pcapData.transportHeader.SeqNum))
					// {
					tcpConnectionPackets.put(pcapData.transportHeader.SeqNum, packetNumber);
					tcpConnections.replace(key, tcpConnectionPackets);
					// }
				}
			}

			Utils.updateOutputValues(pcapData, out, taskNumber);

			if (packetNumber == 557 || packetNumber == 559 || packetNumber == 489 || packetNumber == 540) {
				System.out.println(packetNumber + " len:" + pcapHeader.ActualLengthOfPacket + " " + (14+pcapData.ipHeader.TotalLength));
			}

			packetNumber++;
			// break;
		}
		System.out.println(tcpConnections.size());

		out.Task1.TotalPackets = packetNumber - 1;
		out.Task1.TCPConnections = tcpConnections.size();

		out.display(taskNumber, allPCAPDataPackets, tcpConnections);

		// System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
		// "ms");
	}

}
