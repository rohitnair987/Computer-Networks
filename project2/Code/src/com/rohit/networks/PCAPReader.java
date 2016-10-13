package com.rohit.networks;

import java.io.IOException;
import java.util.Arrays;

public class PCAPReader {

	// Read PCAP Global Header
	public static PCAPGlobalHeader readPCAPGlobalHeader() throws IOException {
		PCAPGlobalHeader pcapHeader = new PCAPGlobalHeader();

		Utils.skipBytes(16);

		// Get Snaplength
		String snapLength = Utils.readBytesAsStringLittleEndian(4);
		pcapHeader.Snaplength = Utils.hexToDecimal(snapLength);

		// Get Link-Layer Protocol
		long linkLayerProtocol = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));
		if (linkLayerProtocol == 1) {
			pcapHeader.LinkLayerProtocol = LinkLayerProtocolEnum.ETHERNET;
		}

		return pcapHeader;
	}

	// Read PCAP Header for each packet
	public static PCAPHeader readPCAPHeader() throws IOException {
		PCAPHeader pcapHeader = new PCAPHeader();

		pcapHeader.PacketTimeStamp = (long) (Math.pow(10, 6) * Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4)) + Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4)));
		pcapHeader.NumberOfOctetsOfPacket = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));
		pcapHeader.ActualLengthOfPacket = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));

		return pcapHeader;
	}

	public static PCAPData readPCAPData(long packetLengthFromPCAPHeader, int taskNumber) throws IOException {
		PCAPData pcapData = new PCAPData();

		// Link - Fixed Header Length of 14 bytes
		readLinkHeader(pcapData);

		int remainingBytes = (int) packetLengthFromPCAPHeader - 14;
		byte[] buffer = new byte[remainingBytes];
		for (int i = 0; i < remainingBytes; i++) {
			buffer[i] = (byte) System.in.read();
		}

		// IP header
		readIPHeader(pcapData, buffer);
		// Temp.display(pcapData.ipHeader);

		// TCP Header
		readTransportHeader(pcapData, buffer, pcapData.ipHeader.HeaderLength);
		// Temp.display(pcapData.transportHeader,
		// pcapData.ipHeader.TransportLayerProtocol);

		// Data
		if (taskNumber != 1) {
			int dataStartInd = pcapData.ipHeader.HeaderLength + pcapData.transportHeader.Offset
					+ pcapData.transportHeader.Length;
			readData(pcapData, buffer, dataStartInd, packetLengthFromPCAPHeader);
		}
		return pcapData;
	}

	// Read Link header and update pcapData
	private static void readLinkHeader(PCAPData pcapData) throws IOException {
		// Destination and source - skip first 12 bytes
		Utils.skipBytes(12);

		// IP version
		byte[] buffer = new byte[2];
		System.in.read(buffer, 0, 2);
		if (buffer[0] == 8 && buffer[1] == 0) {
			pcapData.linkHeader.ipVersion = 4;
		} else {
			pcapData.linkHeader.ipVersion = 6;
		}
	}

	// Read IP header and update pcapData
	private static void readIPHeader(PCAPData pcapData, byte[] buffer) {
		pcapData.ipHeader.Version = Utils.getHigherNibble(buffer[0]);

		pcapData.ipHeader.HeaderLength = 4 * Utils.getLowerNibble(buffer[0]);

		// TypeOfService - skip index 1 (1 byte)

		pcapData.ipHeader.TotalLength = Utils.bytesToInt2(buffer, 2);

		pcapData.ipHeader.Identification = Utils.bytesToInt2(buffer, 4);

		// flags and fragment offset - skip 2 bytes

		pcapData.ipHeader.TTL = buffer[8];

		pcapData.ipHeader.TransportLayerProtocol = ConstantsEnum.setValue(buffer[9]);

		pcapData.ipHeader.HeaderChecksum = Utils.bytesToInt2(buffer, 10);

		pcapData.ipHeader.Source = Utils.getIP(buffer, 12);

		pcapData.ipHeader.Destination = Utils.getIP(buffer, 16);

	}

	// Read Transport header and update pcapData
	private static void readTransportHeader(PCAPData pcapData, byte[] buffer, int startInd) {
		switch (pcapData.ipHeader.TransportLayerProtocol) {

		case UDP:
			pcapData.transportHeader.SourcePort = Utils.bytesToInt2(buffer, startInd);

			pcapData.transportHeader.DestinationPort = Utils.bytesToInt2(buffer, startInd + 2);

			pcapData.transportHeader.Length = Utils.bytesToInt2(buffer, startInd + 4);

			pcapData.transportHeader.CheckSum = Utils.bytesToInt2(buffer, startInd + 6);

			// UDP Data - we're not using this right now

			break;

		case TCP:
			pcapData.transportHeader.SourcePort = Utils.bytesToInt2(buffer, startInd);

			pcapData.transportHeader.DestinationPort = Utils.bytesToInt2(buffer, startInd + 2);

			pcapData.transportHeader.SeqNum = Utils.bytesToInt4(buffer, startInd + 4);

			pcapData.transportHeader.AckNum = Utils.bytesToInt4(buffer, startInd + 8);

			// for offset, take only top 4 bits n ignore next 4
			pcapData.transportHeader.Offset = 4 * Utils.getHigherNibble(buffer[startInd + 12]);

			// Whenever I wanna use
			// pcapData.transportHeader.TCPFlags =
			// Utils.bytesToBinaryString(buffer, startInd + 13, 1);

			// Whenever I wanna use
			// pcapData.transportHeader.Window = Utils.bytesToInt2(buffer,
			// startInd+14);

			// Whenever I wanna use
			// pcapData.transportHeader.CheckSum = Utils.bytesToInt2(buffer,
			// startInd+16);

			// Whenever I wanna use
			// pcapData.transportHeader.UrgentPointer =
			// Utils.bytesToInt2(buffer, startInd+18);

			// Whenever I wanna use
			// pcapData.transportHeader.Options =
			// Utils.ByteArrayToString(buffer,startInd+ 20,
			// pcapData.transportHeader.Offset - 20);

			break;

		case ICMP:
			// Not working with these as of now
			break;

		default:
			// Not working with these as of now

		}

	}

	// Read Packet data
	private static void readData(PCAPData pcapData, byte[] buffer, int startInd, long packetLengthFromPCAPHeader) {
		if (pcapData.ipHeader.TransportLayerProtocol == ConstantsEnum.TCP) {
			if (startInd != buffer.length) {
				int diff = (int) packetLengthFromPCAPHeader - (14 + pcapData.ipHeader.TotalLength);
				if (diff == 0) {
					pcapData.Data = buffer;
				} else {
					pcapData.Data = Arrays.copyOfRange(buffer, 0, buffer.length - diff);
				}
				pcapData.startInd = startInd;
			} else {
				pcapData.Data = new byte[0];
				pcapData.startInd = 0;
			}
		}
	}

}
