package com.rohit.networks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			HashMap<Integer, HashMap<Long, Integer>> tcpConnections) throws IOException {
		// System.out.println("taskNumber" + taskNumber);
		switch (taskNumber) {
		case 1:
			String outString = Task1.TotalPackets + " " + Task1.IPPackets + " " + Task1.TCPPackets + " "
					+ Task1.TCPConnections;
			System.out.println(outString);
			break;

		case 2:
			// change to syso later coz o/p redirection
			// String filename =
			// "/Users/rohit/Drive_Sync/Sem3/CN/Networks/project2/testdata/task2.test1.2.out";
			// File file = new File(filename);
			//
			// // creates the file
			// file.createNewFile();
			//
			// // creates a FileWriter Object
			// FileWriter writer = new FileWriter(file);

			HashMap<Integer, TCPConnectionTupleAndBytes> downLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();
			HashMap<Integer, TCPConnectionTupleAndBytes> upLink = new HashMap<Integer, TCPConnectionTupleAndBytes>();

			// check in tcpConnections for data length
			for (HashMap<Long, Integer> t : tcpConnections.values()) {
				for (int pkt : t.values()) {
					PCAPData p = allPCAPDataPackets.get(pkt);
					// 140.182.214.80 47694 162.248.19.136 80
					if (p.ipHeader.Source.equals("162.248.19.136") && p.transportHeader.SourcePort == 80) {
						if (p.ipHeader.Destination.equals("140.182.214.80")
								&& p.transportHeader.DestinationPort == 47694) {
							if (p.Data != null) {
								System.out.print("pkt:" + pkt + " " + (p.Data.length - p.startInd));
								System.out.println(" mod:");
							} else {
								// System.out.println(pair.getKey() + " = " +
								// 0);
							}
						}
					}
					// if (pkt == 469) {
					// System.out.println("\n\nawesome:"
					// + (66 + allPCAPDataPackets.get(p).Data.length -
					// allPCAPDataPackets.get(p).startInd));
					// } else {
					// System.out.print(p + " ");
					// }
				}
			}

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

			for (HashMap<Long, Integer> seqNoPacketNo : tcpConnections.values()) {
				List<Long> sortedseqNosList = new ArrayList<Long>(seqNoPacketNo.keySet());
				int be = sortedseqNosList.size();
				Set<Long> hs = new HashSet<Long>();
				hs.addAll(sortedseqNosList);
				sortedseqNosList.clear();
				sortedseqNosList.addAll(hs);
				int af = sortedseqNosList.size();
				if (af != be) {
					System.out.println("\n\ndups");
					System.out.println();
				}

				Collections.sort(sortedseqNosList);
				// System.exit(0);

				for (long seqNo : sortedseqNosList) {
					int packetNo = seqNoPacketNo.get(seqNo);
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

					// test start
					ArrayList<Integer> k = new ArrayList<Integer>();
					HashMap<Integer, Integer> v = new HashMap<Integer, Integer>();
					if (pcapData.transportHeader.SourcePort == 80 || pcapData.transportHeader.DestinationPort == 80) {
						// System.out.println(packetNo);
						k.add(packetNo);
						// if (pcapData.Data != null) {
						// v.put(packetNo, pcapData.Data.length -
						// pcapData.startInd);
						//// System.out.println(packetNo + ": " +
						// (pcapData.Data.length - pcapData.startInd));
						// }
						// else {
						// v.put(packetNo, 0);
						//// System.out.println(packetNo + ": 0");
						// }
					}
					// System.out.println(k);
					Collections.sort(k);
					// System.out.println(k);
					for (int key : k) {
						// System.out.println(key);
						// System.out.println(key + ": " + v.get(key));
					}
					// test end

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
								downTupleWithBytes.downstreamBytes = pcapData.Data.length - pcapData.startInd;
							}
							downLink.put(key, downTupleWithBytes);
						} else {
							downTupleWithBytes = downLink.get(key);
							downTupleWithBytes.tcpConnectionTuple = down;
							if (pcapData.Data != null) {
								int b = downTupleWithBytes.downstreamBytes + pcapData.Data.length - pcapData.startInd;
								downTupleWithBytes.downstreamBytes = downTupleWithBytes.downstreamBytes
										+ pcapData.Data.length - pcapData.startInd;
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
								upTupleWithBytes.upstreamBytes = pcapData.Data.length - pcapData.startInd;
							}
							upLink.put(key, upTupleWithBytes);
						} else {
							upTupleWithBytes = upLink.get(key);
							upTupleWithBytes.tcpConnectionTuple = up;
							if (pcapData.Data != null) {
								upTupleWithBytes.upstreamBytes = upTupleWithBytes.upstreamBytes + pcapData.Data.length
										- pcapData.startInd;
							}
							upLink.replace(key, upTupleWithBytes);
						}

					}
				}
				// break;
			}

			ArrayList<String> task2Out = new ArrayList<String>();
			StringBuffer sb;
			for (int hashCode : upLink.keySet()) {
				sb = new StringBuffer();
				TCPConnectionTupleAndBytes tcpConnectionTupleAndBytes = upLink.get(hashCode);
				sb.append(tcpConnectionTupleAndBytes.tcpConnectionTuple.tupleString());
				sb.append(upLink.get(hashCode).upstreamBytes + " ");
				sb.append(downLink.get(hashCode).downstreamBytes);
				task2Out.add(sb.toString());
			}
			Collections.sort(task2Out);
			for (String s : task2Out) {
				System.out.println(s);
			}

			// writer.write("This\n is\n an\n example\n");
			// writer.flush();
			// writer.close();

			break; // break case 2
		}

	}

}
