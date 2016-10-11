package com.rohit.networks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Task1 {
	int TotalPackets = 0;
	int IPPackets = 0;
	int TCPPackets = 0;
	int UDPPackets = 0;
	int TCPConnections = 0;
}

public class Output {
	Task1 Task1 = new Task1();

	void display(int taskNumber, Map<Integer, PCAPData> allPCAPDataPackets,
			HashMap<Integer, TreeMap<Long, Integer>> tcpConnections) throws IOException {

		switch (taskNumber) {
		case 1:
			String outString = Task1.TotalPackets + " " + Task1.IPPackets + " " + Task1.TCPPackets + " "
					+ Task1.TCPConnections;
			System.out.println(outString);
			break;

		case 2:
			HashMap<Integer, TCPConnectionTupleAndBytes> downLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();
			HashMap<Integer, TCPConnectionTupleAndBytes> upLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();

			for (TreeMap<Long, Integer> seqNoPacketNo : tcpConnections.values()) {
				for (int packetNo : seqNoPacketNo.values()) {
					PCAPData pcapData = allPCAPDataPackets.get(packetNo);
					if (pcapData.transportHeader.SourcePort == 80) {
						// downlink

						TCPConnectionTuple down = new TCPConnectionTuple();
						down.ClientIP = pcapData.ipHeader.Destination;
						down.ClientPort = pcapData.transportHeader.DestinationPort;
						down.ServerIP = pcapData.ipHeader.Source;
						down.ServerPort = pcapData.transportHeader.SourcePort;

						int key = down.calculateHashCode();
						TCPConnectionTupleAndBytes downTupleWithBytes = new TCPConnectionTupleAndBytes();

						if (!downLink.containsKey(key)) {
							downTupleWithBytes.tcpConnectionTuple = down;
							if (pcapData.Data != null) {
								downTupleWithBytes.downstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							downLink.put(key, downTupleWithBytes);
						} else {
							downTupleWithBytes = downLink.get(key);
							downTupleWithBytes.tcpConnectionTuple = down;
							if (pcapData.Data != null) {
								downTupleWithBytes.downstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							downLink.replace(key, downTupleWithBytes);
						}

					} else if (pcapData.transportHeader.DestinationPort == 80) {
						// uplink

						TCPConnectionTuple up = new TCPConnectionTuple();
						up.ClientIP = pcapData.ipHeader.Source;
						up.ClientPort = pcapData.transportHeader.SourcePort;
						up.ServerIP = pcapData.ipHeader.Destination;
						up.ServerPort = pcapData.transportHeader.DestinationPort;

						int key = up.calculateHashCode();
						TCPConnectionTupleAndBytes upTupleWithBytes = new TCPConnectionTupleAndBytes();

						if (!upLink.containsKey(key)) {
							upTupleWithBytes.tcpConnectionTuple = up;
							if (pcapData.Data != null) {
								upTupleWithBytes.upstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							upLink.put(key, upTupleWithBytes);
						} else {
							upTupleWithBytes = upLink.get(key);
							upTupleWithBytes.tcpConnectionTuple = up;
							if (pcapData.Data != null) {
								upTupleWithBytes.upstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							upLink.replace(key, upTupleWithBytes);
						}
					}
				}
			}

			TreeMap<String, ArrayList<byte[]>> task2Out = new TreeMap<String, ArrayList<byte[]>>();
			StringBuffer sb;
			for (int hashCode : upLink.keySet()) {
				// task2 part 1
				sb = new StringBuffer();
				TCPConnectionTupleAndBytes tcpConnectionTupleAndBytes = upLink.get(hashCode);
				sb.append(tcpConnectionTupleAndBytes.tcpConnectionTuple.tupleString());
				TCPConnectionTupleAndBytes upData = upLink.get(hashCode);
				sb.append(Utils.getNoOfBytes(upData, 'u') + " ");
				TCPConnectionTupleAndBytes downData = downLink.get(hashCode);
				sb.append(Utils.getNoOfBytes(downData, 'd'));

				// task2 part 2
				ArrayList<byte[]> data = new ArrayList<byte[]>();
				for (byte[] b : upData.upstreamBytes) {
					data.add(b);
				}
				for (byte[] b : downData.downstreamBytes) {
					data.add(b);
				}

				task2Out.put(sb.toString(), data);
			}

			FileOutputStream fos = new FileOutputStream(
					"/Users/rohit/Drive_Sync/Sem3/CN/Networks/project2/mytask2.out");

			// Write Tuple and number of upstream and downstream bytes
			for (String tupleUpDown : task2Out.keySet()) {
				fos.write((tupleUpDown + "\n").getBytes("ISO-8859-1"));
			}

			// Write Data
			StringBuffer dataPrint = new StringBuffer();
			for (ArrayList<byte[]> dataArray : task2Out.values()) {
				for (byte[] data : dataArray) {
					fos.write(data);
					for (int i = 0; i < data.length; i++) {
						dataPrint.append(data[i]);
					}
				}
			}
			fos.close();

			break; // break case 2
		}

	}

}
