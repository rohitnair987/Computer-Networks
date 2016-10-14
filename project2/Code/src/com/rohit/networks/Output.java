package com.rohit.networks;

import java.io.IOException;
import java.nio.charset.Charset;
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

		if (taskNumber == 1) {
			String outString = Task1.TotalPackets + " " + Task1.IPPackets + " " + Task1.TCPPackets + " "
					+ Task1.TCPConnections;
			System.out.println(outString);
		}

		if (taskNumber == 2 || taskNumber == 3) {
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
							downTupleWithBytes.PacketNumber.add(packetNo);
							if (pcapData.Data != null) {
								downTupleWithBytes.downstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							downLink.put(key, downTupleWithBytes);
						} else {
							downTupleWithBytes = downLink.get(key);
							downTupleWithBytes.PacketNumber.add(packetNo);
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
						upTupleWithBytes.PacketTimeStamp = pcapData.PacketTimeStamp;

						if (!upLink.containsKey(key)) {
							upTupleWithBytes.tcpConnectionTuple = up;
							upTupleWithBytes.PacketNumber.add(packetNo);
							if (pcapData.Data != null) {
								upTupleWithBytes.upstreamBytes.add(
										Arrays.copyOfRange(pcapData.Data, pcapData.startInd, pcapData.Data.length));
							}
							upLink.put(key, upTupleWithBytes);
						} else {
							upTupleWithBytes = upLink.get(key);
							upTupleWithBytes.PacketNumber.add(packetNo);
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

			if (taskNumber == 2) {
				write(task2Out);
			}

			if (taskNumber == 3) {
				// Sort up-links by time-stamp
				TreeMap<Long, TCPConnectionTupleAndBytes> upLinksSortedByTimeStamp = Utils.sortByTimeStamp(upLink);
				// free the memory of this object since it's not gonna be used
				upLink = null;

				Map<Long, String> task3 = new TreeMap<>();
				int reqNo = 1;
				for (TCPConnectionTupleAndBytes reqConn : upLinksSortedByTimeStamp.values()) {
					// System.out.println("reqNo = " + reqNo);
					StringBuilder sbb = new StringBuilder();
					for (int p : reqConn.PacketNumber) {
						PCAPData reqPkt = allPCAPDataPackets.get(p);
						if (reqPkt.Data.length > 0) {
							HTTPRequest request = new HTTPRequest();
							request.SeqNo = reqPkt.transportHeader.SeqNum;
							request.DataLength = reqPkt.Data.length - reqPkt.startInd;

							// Read data from the request and update the
							// HTTPRequest instance
							HTTPReader.readRequest(request, reqPkt);
							long ack = request.SeqNo + request.DataLength;

							ArrayList<HTTPResponse> responses = new ArrayList<HTTPResponse>();
							// Look for this sequence number in the responses
							String responseData = "";

							for (TCPConnectionTupleAndBytes resConn : downLink.values()) {
								if (responseData.equals("Content-Length")) {
									break;
								}

								HTTPResponse response = new HTTPResponse();
								StringBuilder chunkedData = new StringBuilder();
								for (int resPktNo : resConn.PacketNumber) {

									if (responseData.equals("Content-Length")) {
										break;
									}
									PCAPData resPkt = allPCAPDataPackets.get(resPktNo);
									if (resPkt.transportHeader.AckNum == ack) {
										// To-do: sub-array not req until we
										// reach here, remove from downstream
										// bytes
										byte[] resData = Arrays.copyOfRange(resPkt.Data, resPkt.startInd,
												resPkt.Data.length);
										if (resData.length > 0) {
											// Read data from the response
											// and update the
											// HTTPResponse instance
											responseData = HTTPReader.readResponse(response, resPkt, resData);
											if (responseData.equals("Content-Length")) {
												sbb = new StringBuilder();
												sbb.append(request.URL).append(" ").append(request.Host).append(" ")
														.append(response.StatusCode).append(" ")
														.append(response.ContentLength);
												task3.put(reqPkt.PacketTimeStamp, sbb.toString());

												break;
											} else {
												chunkedData.append(responseData);
											}

										}
										if (!responseData.equals("Content-Length")) {
											sbb = new StringBuilder();
											response.ContentLength = HTTPReader.countDataBytes(chunkedData.toString());
											sbb.append(request.URL).append(" ").append(request.Host).append(" ")
													.append(response.StatusCode).append(" ")
													.append(response.ContentLength);
											task3.put(reqPkt.PacketTimeStamp, sbb.toString());
										}

									}

								}

							}

						}

					}
					reqNo++;
				}

				for (String str : task3.values()) {
					System.out.println(str);
				}
			}

		}

	}

	private void write(TreeMap<String, ArrayList<byte[]>> task2Out) {
		// Write Tuple and number of upstream and downstream bytes
		for (String tupleUpDown : task2Out.keySet()) {
			System.out.println(tupleUpDown);
		}

		// Write Data
		// To-do - System.out.write(per byte)
		for (ArrayList<byte[]> dataArray : task2Out.values()) {
			for (byte[] data : dataArray) {
				System.out.print(new String(data, Charset.forName("ISO-8859-1")));
			}
		}
	}

}
