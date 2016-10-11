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
		// System.out.println("taskNumber" + taskNumber);
		switch (taskNumber) {
		case 1:
			String outString = Task1.TotalPackets + " " + Task1.IPPackets + " " + Task1.TCPPackets + " "
					+ Task1.TCPConnections;
			System.out.println(outString);
			break;

		case 2:
			HashMap<Integer, TCPConnectionTupleAndBytes> downLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();
			HashMap<Integer, TCPConnectionTupleAndBytes> upLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();

			// first line data
			// int hash = ("140.182.214.80" + 34959).hashCode() +
			// ("216.58.216.66" + 80).hashCode();
			// HashMap<Long, Integer> d = tcpConnections.get(hash);
			// for(int f : d.values()) {
			// PCAPData p = allPCAPDataPackets.get(f);
			// if (p.Data != null && (p.Data.length -p.startInd) > 0) {
			//// System.out.println("pkt:" + 1 + " len:"+(p.Data.length -
			// p.startInd));
			// for(int i = p.startInd; i < p.Data.length; i++) {
			// System.out.print((char)p.Data[i]);
			// }
			//// System.out.println();
			// }
			// }
			// System.exit(0);

			// first http pkt
			// PCAPData h = allPCAPDataPackets.get(14);
			// for(int i = h.startInd; i < h.Data.length; i++) {
			// System.out.print((char) h.Data[i]);
			// }
			// System.exit(0);

			// All data from all packets
			// for(PCAPData p : allPCAPDataPackets.values()) {
			// if (p.Data != null && (p.Data.length -p.startInd) > 0) {
			//// System.out.println("pkt:" + 1 + " len:"+(p.Data.length -
			// p.startInd));
			// for(int i = p.startInd; i < p.Data.length; i++) {
			// System.out.print((char)p.Data[i]);
			// }
			//// System.out.println();
			// }
			// }
			// System.exit(0);

			// for (HashMap<Long, Integer> t : tcpConnections.values()) {
			// for (int pkt : t.values()) {
			// PCAPData p = allPCAPDataPackets.get(pkt);
			// if (p.ipHeader.Source.equals("162.248.19.136") &&
			// p.transportHeader.SourcePort == 80) {
			// if (p.ipHeader.Destination.equals("140.182.214.80")
			// && p.transportHeader.DestinationPort == 47694) {
			// if (p.Data != null && (p.Data.length -p.startInd) > 0) {
			// System.out.println("pkt:" + pkt + " len:"+(p.Data.length -
			// p.startInd));
			// for(int i = p.startInd; i < p.Data.length; i++) {
			// System.out.print((char)p.Data[i]);
			// }
			// System.out.println();
			// } else {
			// // System.out.println(pair.getKey() + " = " +
			// // 0);
			// }
			// }
			// }
			// // if (pkt == 469) {
			// // System.out.println("\n\nawesome:"
			// // + (66 + allPCAPDataPackets.get(p).Data.length -
			// // allPCAPDataPackets.get(p).startInd));
			// // } else {
			// // System.out.print(p + " ");
			// // }
			// }
			// }

			// System.exit(0);

			// Iterator it = allPCAPDataPackets.entrySet().iterator();
			// while (it.hasNext()) {
			// Map.Entry pair = (Map.Entry) it.next();
			// PCAPData p = (PCAPData) pair.getValue();
			// if (p.ipHeader.Source.equals("216.58.216.66") &&
			// p.transportHeader.SourcePort == 80) {
			// if (p.ipHeader.Destination.equals("140.182.214.80") &&
			// p.transportHeader.DestinationPort == 34959) {
			// if (p.Data != null) {
			// System.out.println(pair.getKey() + " = " + (p.Data.length -
			// p.startInd));
			// } else {
			// // System.out.println(pair.getKey() + " = " + 0);
			// }
			// }
			// }
			//
			// // System.out.println(pair.getKey() + " = " + p.Data.length - );
			// it.remove(); // avoids a ConcurrentModificationException
			// }
			// System.exit(0);

			// Iterator it = allPCAPDataPackets.entrySet().iterator();
			// while (it.hasNext()) {
			// Map.Entry pair = (Map.Entry) it.next();
			// PCAPData p = (PCAPData) pair.getValue();
			// if (p.Data != null) {
			// if (p.Data.length - p.startInd > 0) {
			// System.out.println(pair.getKey() + " = " + (66 + p.Data.length -
			// p.startInd));
			// }
			// }
			// // System.out.println(pair.getKey() + " = " + p.Data.length - );
			// it.remove(); // avoids a ConcurrentModificationException
			// }

			// for (PCAPData p : allPCAPDataPackets.values()) {
			// if (p.Data != null) {
			// if (p.Data.length - p.startInd > 0) {
			// System.out.println((p.Data.length - p.startInd));
			// }
			// }
			// }

			// for (HashMap<Long, Integer> seqpktno : tcpConnections.values()) {
			// for (int pktno : seqpktno.values()) {
			// int datalen = -1;
			//
			// if (allPCAPDataPackets.get(pktno).Data != null) {
			// datalen = allPCAPDataPackets.get(pktno).Data.length -
			// allPCAPDataPackets.get(pktno).startInd;
			// }
			// System.out.println(pktno + " " + datalen);
			// }
			// }
			// System.exit(0);

			for (TreeMap<Long, Integer> seqNoPacketNo : tcpConnections.values()) {
				// List<Long> sortedseqNosList = new
				// ArrayList<Long>(seqNoPacketNo.keySet());
				// int be = sortedseqNosList.size();
				// Set<Long> hs = new HashSet<Long>();
				// hs.addAll(sortedseqNosList);
				// sortedseqNosList.clear();
				// sortedseqNosList.addAll(hs);
				// int af = sortedseqNosList.size();
				// if (af != be) {
				// System.out.println("dups");
				// }
				//
				// Collections.sort(sortedseqNosList);

				for (int packetNo : seqNoPacketNo.values()) {
					// int packetNo = seqNoPacketNo.get(seqNo);
					PCAPData pcapData = allPCAPDataPackets.get(packetNo);
					// System.out.print("seqNo:" + seqNo + " packetNo:" +
					// packetNo + " len:");
					// if(pcapData.Data != null) {
					// System.out.println(pcapData.Data.length -
					// pcapData.startInd);
					// }
					// else {
					// System.out.println("zero");
					// }

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
				// break;
			}

			// loop start

			// tuple, upLink-downLink
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
				for(byte[] b : upData.upstreamBytes) {
					data.add(b);
				}
				for(byte[] b : downData.downstreamBytes) {
					data.add(b);
				}				
				
				
				// int tupleHash =
				// tcpConnectionTupleAndBytes.tcpConnectionTuple.calculateHashCode();
				// TreeMap<Long, Integer> seqNoPacketNo =
				// tcpConnections.get(tupleHash);
//				TreeMap<Long, Integer> seqNoPacketNo = tcpConnections.get(hashCode);
//				
//				for (int packetNo : seqNoPacketNo.values()) {
//					PCAPData pcapData = allPCAPDataPackets.get(packetNo);
//					if (pcapData.Data != null && (pcapData.Data.length - pcapData.startInd) > 0) {
//						data.add(Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
//					}
//				}

				task2Out.put(sb.toString(), data);
			}

			FileOutputStream fos = new FileOutputStream(
					"/Users/rohit/Drive_Sync/Sem3/CN/Networks/project2/mytask2.out");

			// Tuples up-down - workin fine
			for (String tupleUpDown : task2Out.keySet()) {
				fos.write((tupleUpDown + "\n").getBytes("ISO-8859-1"));
			}

			// Data - missing certain things
			StringBuffer dataPrint = new StringBuffer();
			for (ArrayList<byte[]> dataArray : task2Out.values()) {
				for (byte[] data : dataArray) {
					fos.write(data);
					for (int i = 0; i < data.length; i++) {
						dataPrint.append(data[i]);
						System.out.print((char) data[i]);
					}
					// break;
				}
				// break;
			}

			fos.close();

			break; // break case 2
		}

	}

}
