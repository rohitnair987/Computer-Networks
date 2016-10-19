import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
		long d1 = new Date().getTime();
		int taskNumber = Integer.parseInt(args[0]);
		if (!(taskNumber == 1 || taskNumber == 2 || taskNumber == 3 || taskNumber == 4)) {
			System.out.println("Please enter task number (1, 2 , 3 or 4)");
			System.exit(1);
		}

		// int taskNumber = 1;

		// Skip global header
		Utils.skipBytes(24);
		// PCAPGlobalHeader pcapGlobalHeader =
		// PCAPReader.readPCAPGlobalHeader();
		// System.out.println("Snaplength = " + pcapGlobalHeader.Snaplength);
		// System.out.println("LinkLayerProtocol = " +
		// pcapGlobalHeader.LinkLayerProtocol);

		PCAPHeader pcapHeader = null;
		PCAPData pcapData = null;
		Set<Integer> tcpConnectionsCount = new HashSet<Integer>();
		Output out = new Output();

		if (taskNumber == 1) {
			int packetNumber = 1;

			while (System.in.available() > 0) {
				// Read PCAP Header of each packet
				pcapHeader = PCAPReader.readPCAPHeader();

				if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
					System.out.println("\nNot a valid packet!");
				}

				// Read the packet
				pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket, taskNumber);

				if (pcapData.ipHeader.TransportLayerProtocol == ConstantsEnum.TCP) {
					int key = new StringBuilder().append(pcapData.ipHeader.Source)
							.append(pcapData.transportHeader.SourcePort).toString().hashCode()
							+ new StringBuilder().append(pcapData.ipHeader.Destination)
									.append(pcapData.transportHeader.DestinationPort).toString().hashCode();

					if (!tcpConnectionsCount.contains(key)) {
						tcpConnectionsCount.add(key);
					}
				}

				Utils.updateOutputValues(pcapData, out, taskNumber);
				packetNumber++;

			}

			System.out.println(new StringBuilder().append(packetNumber - 1).append(" ").append(out.Task1.IPPackets)
					.append(" ").append(out.Task1.TCPPackets).append(" ").append(out.Task1.UDPPackets).append(" ")
					.append(tcpConnectionsCount.size()).toString());

		}

		// --------------------------------------------------

		else {
			TreeMap<Long, Integer> tcpConnectionPackets = new TreeMap<Long, Integer>();
			Map<Integer, PCAPData> allPCAPDataPackets = new HashMap<Integer, PCAPData>();
			HashMap<Integer, TreeMap<Long, Integer>> tcpConnections = new HashMap<Integer, TreeMap<Long, Integer>>();

			int packetNumber = 1;

			while (System.in.available() > 0) {
				// while (packetNumber < 63) {
				// System.out.println("\n" + packetNumber + ": ");

				// Read PCAP Header of each packet
				pcapHeader = PCAPReader.readPCAPHeader();

				if (pcapHeader.NumberOfOctetsOfPacket != pcapHeader.ActualLengthOfPacket) {
					System.out.println("\nNot a valid packet!");
				}

				// Read the packet
				pcapData = PCAPReader.readPCAPData(pcapHeader.ActualLengthOfPacket, taskNumber);
				if (taskNumber > 2) {
					pcapData.PacketTimeStamp = pcapHeader.PacketTimeStamp;
				}

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

				packetNumber++;

			}

			out.display(taskNumber, allPCAPDataPackets, tcpConnections);
		}
//		System.out.println("\nTime taken = " + (new Date().getTime() - d1) + "ms");
	}

}
