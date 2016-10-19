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

		if (taskNumber == 2) {
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
						upTupleWithBytes.PacketTimeStamp = pcapData.PacketTimeStamp;

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

			Map<String, ArrayList<byte[]>> task2Out = new TreeMap<String, ArrayList<byte[]>>();
			StringBuffer sb;
			for (int hashCode : upLink.keySet()) {
				// task2 part 1
				TCPConnectionTupleAndBytes tcpConnectionTupleAndBytes = upLink.get(hashCode);
				TCPConnectionTupleAndBytes upData = upLink.get(hashCode);
				TCPConnectionTupleAndBytes downData = downLink.get(hashCode);
				sb = new StringBuffer();
				sb.append(tcpConnectionTupleAndBytes.tcpConnectionTuple.tupleString());
				sb.append(Utils.getNoOfBytes(upData, 'u') + " ");
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

			// Write Tuple and number of upstream and downstream bytes
			for (String tupleUpDown : task2Out.keySet()) {
				System.out.println(tupleUpDown);
			}

			// Write Data
			for (ArrayList<byte[]> dataArray : task2Out.values()) {
				for (byte[] data : dataArray) {
					System.out
							.print(new String(Arrays.copyOfRange(data, 0, data.length), Charset.forName("ISO-8859-1")));
				}
			}

		}

		// ----------------------------------------------------------------

		else {

			if (taskNumber == 3) {
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
								downLink.put(key, downTupleWithBytes);
							} else {
								downTupleWithBytes = downLink.get(key);
								downTupleWithBytes.PacketNumber.add(packetNo);
								downTupleWithBytes.tcpConnectionTuple = down;
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
								upLink.put(key, upTupleWithBytes);
							} else {
								upTupleWithBytes = upLink.get(key);
								upTupleWithBytes.PacketNumber.add(packetNo);
								upTupleWithBytes.tcpConnectionTuple = up;
								upLink.replace(key, upTupleWithBytes);
							}
						}
					}
				}

				// Sort up-links by time-stamp
				TreeMap<Long, TCPConnectionTupleAndBytes> upLinksSortedByTimeStamp = Utils.sortByTimeStamp(upLink);
				// Free the memory since it's not gonna be used
				upLink = null;

				Map<Long, String> task3 = new TreeMap<>();
				for (TCPConnectionTupleAndBytes reqConn : upLinksSortedByTimeStamp.values()) {
					StringBuilder sbb = new StringBuilder();
					for (int p : reqConn.PacketNumber) {
						PCAPData reqPkt = allPCAPDataPackets.get(p);
						if (reqPkt.Data.length > 0) {
							HTTPRequest request = new HTTPRequest();
							request.SeqNo = reqPkt.transportHeader.SeqNum;
							request.DataLength = reqPkt.Data.length - reqPkt.startInd;

							// Read data from the request and update the
							// HTTPRequest instance
							HTTPReader.readRequest(request, reqPkt, taskNumber);
							long ack = request.SeqNo + request.DataLength;

							// Look for this sequence number in the
							// responses
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
														.append(response.StatusCode).append(" ");
												if (request.Method.equalsIgnoreCase("HEAD")
														|| response.StatusCode == 204 || response.StatusCode == 302
														|| Integer.toString(response.StatusCode).charAt(0) == '1') {
													sbb.append("0");
												} else {
													sbb.append(response.ContentLength);
												}

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
													.append(response.StatusCode).append(" ");
											if (request.Method.equalsIgnoreCase("HEAD") || response.StatusCode == 204
													|| response.StatusCode == 302
													|| Integer.toString(response.StatusCode).charAt(0) == '1') {
												sbb.append("0");
											} else {
												sbb.append(response.ContentLength);
											}
											task3.put(reqPkt.PacketTimeStamp, sbb.toString());
										}

									}

								}

							}

						}

					}
				}

				for (String str : task3.values()) {
					System.out.println(str);
				}

			}

			// ----------------------------------
			else if (taskNumber == 4) {
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
								downLink.put(key, downTupleWithBytes);
							} else {
								downTupleWithBytes = downLink.get(key);
								downTupleWithBytes.PacketNumber.add(packetNo);
								downTupleWithBytes.tcpConnectionTuple = down;
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
								upLink.put(key, upTupleWithBytes);
							} else {
								upTupleWithBytes = upLink.get(key);
								upTupleWithBytes.PacketNumber.add(packetNo);
								upTupleWithBytes.tcpConnectionTuple = up;
								upLink.replace(key, upTupleWithBytes);
							}
						}
					}
				}

				// Sort up-links by time-stamp
				TreeMap<Long, TCPConnectionTupleAndBytes> upLinksSortedByTimeStamp = Utils.sortByTimeStamp(upLink);
				// Free the memory since it's not gonna be used
				upLink = null;

				Map<Long, HTTPResponse> task4 = new TreeMap<>();

				for (TCPConnectionTupleAndBytes reqConn : upLinksSortedByTimeStamp.values()) {
					for (int p : reqConn.PacketNumber) {
						PCAPData reqPkt = allPCAPDataPackets.get(p);
						if (reqPkt.Data.length > 0) {
							HTTPRequest request = new HTTPRequest();
							request.TimeStamp = reqPkt.PacketTimeStamp;
							request.SeqNo = reqPkt.transportHeader.SeqNum;
							request.DataLength = reqPkt.Data.length - reqPkt.startInd;

							// Read data from the request and update the
							// HTTPRequest instance
							boolean isImage = HTTPReader.readRequest(request, reqPkt, taskNumber);
							if (!isImage) {
								continue;
							}

							long ack = request.SeqNo + request.DataLength;

							// Look for this ack in the responses
							String responseData = "";

							for (TCPConnectionTupleAndBytes resConn : downLink.values()) {
								HTTPResponse response = new HTTPResponse();
								for (int resPktNo : resConn.PacketNumber) {
									PCAPData resPkt = allPCAPDataPackets.get(resPktNo);
									if (resPkt.transportHeader.AckNum == ack) {
										byte[] resData = Arrays.copyOfRange(resPkt.Data, resPkt.startInd,
												resPkt.Data.length);
										if (resData.length > 0) {
											responseData = HTTPReader.readResponseTask4(response, resPkt, resData);
											if (responseData.equals("StatusNot200")) {
												response.ContentType = "StatusNot200";
												continue;
											}
											if (response.ContentType.equals("Content-Length")) {
												response.Content.append(responseData);
											} else if (responseData.equals("Content-Length")) {
												response.ContentType = "Content-Length";
											} else if (response.ContentType.equals("Transfer-Encoding")
													|| responseData.equals("Transfer-Encoding")) {
												response.Content.append(responseData);
											}

										}

									}

								}

								if (response.Content.toString().length() > 0) {
									int len = request.URL.length();
									response.fileName = "/Users/rohit/Drive_Sync/Sem3/CN/Networks/project2/images/";
									String ext = request.URL.substring(len - 4, len);
									if (ext.equalsIgnoreCase("webp")) {
										ext = ".webp";
									}
									response.FileExtension = ext;

									if (!task4.containsKey(request.TimeStamp)) {
										task4.put(request.TimeStamp, response);
									}
								}

							}

						}

					}

				}

				int fileNum = 0;
				StringBuilder task4Out = new StringBuilder();
				for (HTTPResponse response : task4.values()) {
					String outStr = response.Content.toString();

					// Out file
					task4Out.append(Integer.toHexString(outStr.length()));
					task4Out.append("\r\n");
					task4Out.append(outStr);
					task4Out.append("\r\n");

					// Write image
					// String fileName = response.fileName + fileNum +
					// response.FileExtension;
					// Utils.writeToFile(fileName, outStr);

					fileNum++;
				}
				task4Out.append("0\r\n\r\n");

				System.out.print(task4Out.toString());

			}
		}

	}

}