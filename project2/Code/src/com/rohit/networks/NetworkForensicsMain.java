package com.rohit.networks;

import java.io.IOException;
import java.util.*;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
		// long d1 = new Date().getTime();
		// if(args.length != 1) {
		// System.out.println("Please enter task number");
		// System.exit(1);
		// }
		//
		// int taskNumber = Integer.parseInt(args[0]);
		// if(!(taskNumber == 1 || taskNumber == 2 || taskNumber == 3 ||
		// taskNumber == 4)) {
		// System.out.println("Please enter task number (1, 2 , 3 or 4)");
		// System.exit(1);
		// }

		int taskNumber = 1;

		PCAPGlobalHeader pcapGlobalHeader = PCAPReader.readPCAPGlobalHeader();
		// System.out.println("Snaplength = " + pcapGlobalHeader.Snaplength);
		// System.out.println("LinkLayerProtocol = " +
		// pcapGlobalHeader.LinkLayerProtocol);

		PCAPHeader pcapHeader;
		PCAPData pcapData;
		TCPConnectionTuple tcpConnectionTuple;
		ArrayList<Integer> tcpConnectionKey;
		Output out = new Output();

		Map<Integer, PCAPData> allPCAPDataPackets = new HashMap<Integer, PCAPData>();
		HashMap<Integer, ArrayList<Integer>> tcpConnections = new HashMap<Integer, ArrayList<Integer>>();

		int packetNumber = 1;

		while (System.in.available() > 0) {
			// while (packetNumber < 20) {
			System.out.print("\n" + packetNumber + ": ");
			pcapHeader = PCAPReader.readPCAPHeader();

			if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
				System.out.println("\nNot a valid packet!");
				System.exit(0);
			}

			pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket);

			allPCAPDataPackets.put(packetNumber, pcapData);

			Utils.updateOutputValues(pcapData, out, taskNumber);

			if (pcapData.ipHeader.TransportLayerProtocol == ConstantsEnum.TCP) {
				tcpConnectionTuple = new TCPConnectionTuple();

				StringBuilder src = new StringBuilder();
				src.append(pcapData.ipHeader.Source).append(":").append(pcapData.transportHeader.SourcePort);
				StringBuilder dest = new StringBuilder();
				dest.append(pcapData.ipHeader.Destination).append(":").append(pcapData.transportHeader.DestinationPort);

				System.out.println("\nsrc :" + src);
				System.out.println("dest:" + dest);

				int key = src.toString().hashCode() + dest.toString().hashCode();
				if (!tcpConnections.containsKey(key)) {
					tcpConnectionKey = new ArrayList<Integer>(packetNumber);
					tcpConnections.put(key, tcpConnectionKey);
				} else {
					tcpConnectionKey = tcpConnections.get(key);
					tcpConnectionKey.add(packetNumber);
					tcpConnections.replace(key, tcpConnectionKey);
				}
			}
			
			packetNumber++;
			// break;
		}
		System.out.println();

		if (taskNumber == 1) {
			out.Task1.TotalPackets = packetNumber - 1;
			out.Task1.TCPConnections = tcpConnections.size();
		}
		 out.display(taskNumber);

		// System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
		// "ms");
	}

}
