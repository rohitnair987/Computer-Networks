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
		Map<String, Integer> tcpConnections = new HashMap<String, Integer>();
		HashSet<String> tcpConnections4Tuple = new HashSet<String>();
		HashMap<Integer, ArrayList<Integer>> tcpConnections2 = new HashMap<Integer, ArrayList<Integer>>();

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
				System.out.println("key :" + key + " = " + src.toString().hashCode() + " + " + dest.toString().hashCode());
				
				if (!tcpConnections2.containsKey(key)) {
					tcpConnectionKey = new ArrayList<Integer>(packetNumber);
					tcpConnections2.put(key, tcpConnectionKey);
				} else {
					tcpConnectionKey = tcpConnections2.get(key);
					tcpConnectionKey.add(packetNumber);
					tcpConnections2.replace(key, tcpConnectionKey);
				}

				String s1 = src.append(dest).toString();
				String s2 = dest.append(src).toString();

				// testing
				// s1 = "\n" + s1;
				// s2 = "\n" + s2;

				tcpConnections4Tuple.add(s1);
				tcpConnections4Tuple.add(s2);

				if (tcpConnections.containsKey(s1)) {
					tcpConnections.put(s1, tcpConnections.get(s1) + 1);
					tcpConnections.put(s2, tcpConnections.get(s2) + 1);
				} else {
					tcpConnections.put(s1, 1);
					tcpConnections.put(s2, 1);
				}

				packetNumber++;
			}

			// System.out.println("\n" + pcapData.ipHeader.Source + " " +
			// pcapData.transportHeader.SourcePort + " ");
			// System.out.println(pcapData.ipHeader.Destination + " " +
			// pcapData.transportHeader.DestinationPort);

			// break;
		}
		System.out.println();

		if (taskNumber == 1) {
			out.Task1.TotalPackets = packetNumber - 1;
			System.out.println("tcpConnections:" + tcpConnections.size());
			// System.out.println(tcpConnections);
			System.out.println("tcpConnections2:" + tcpConnections2.size());
			out.Task1.TCPConnections = (int) Math.ceil(tcpConnections.size() / 2.0);
		}
		// out.display(taskNumber);

		// System.out.println("\nTime taken = " + (new Date().getTime() - d1) +
		// "ms");
	}

}
