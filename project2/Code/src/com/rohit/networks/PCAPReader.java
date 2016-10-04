package com.rohit.networks;

import java.io.IOException;

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

		Utils.skipBytes(8);
		pcapHeader.NumberOfOctetsOfPacket = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));
		pcapHeader.ActualLengthOfPacket = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));

		return pcapHeader;
	}

	public static PCAPData readPCAPData(long packetLengthFromPCAPHeader) throws IOException {
		PCAPData pcapData = new PCAPData();

		// System.out.println("l:" + packetLengthFromPCAPHeader);
		String packet = Utils.readBytesAsStringBigEndian(packetLengthFromPCAPHeader);
		// System.out.print("p:" + packet.substring(0, 14 * 2) + " ");

		// Fixed Link Header Length .... (* 2 is because each byte is 2
		// characters)
		int linkLayeLength = 14 * 2;
		int currentOffset = 0;
		readLinkHeader(pcapData.linkHeader, packet.substring(currentOffset, linkLayeLength));
		currentOffset += linkLayeLength;

		// To-do: pass upper limit of string
		readIPHeader(pcapData.ipHeader, packet.substring(currentOffset));
		currentOffset += 2 * pcapData.ipHeader.HeaderLength;

		// To-do: pass upper limit of string
		readTransportHeader(pcapData.transportHeader, pcapData.ipHeader, packet.substring(currentOffset));

		return pcapData;
	}

	private static void readLinkHeader(LinkHeader linkHeader, String packetLinkHeader) throws IOException {
		// Destination and source - skip first 12 bytes
		if (packetLinkHeader.substring(12 * 2).equals("0800")) {
			linkHeader.ipVersion = 4;
		} else {
			linkHeader.ipVersion = 6;
		}
	}

	private static void readIPHeader(IPHeader ipHeader, String packetIPHeader) {
		int CurrentOffset = 0;

		ipHeader.Version = Utils.hexToDecimal(packetIPHeader.charAt(CurrentOffset++));

		ipHeader.HeaderLength = 4 * Utils.hexToDecimal(packetIPHeader.charAt(CurrentOffset++));

		// TypeOfService skip indices 2 and 3 (1 byte)
		CurrentOffset += 2;

		ipHeader.TotalLength = (int) Utils.hexToDecimal(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
		CurrentOffset += 2 * 2;

		ipHeader.Identification = (int) Utils
				.hexToDecimal(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
		CurrentOffset += 2 * 2;

		// flags and fragment offset
		CurrentOffset += 2 * 2;

		ipHeader.TTL = (int) Utils.hexToDecimal(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2));
		CurrentOffset += 2;

		ipHeader.TransportLayerProtocol = ConstantsEnum
				.setValue((int) Utils.hexToDecimal(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2)));
		CurrentOffset += 2;

		ipHeader.HeaderChecksum = (int) Utils
				.hexToDecimal(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
		CurrentOffset += 2 * 2;

		ipHeader.Source = Utils.stringToIP(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2 * 4));
		CurrentOffset += 2 * 4;

		ipHeader.Destination = Utils.stringToIP(packetIPHeader.substring(CurrentOffset, CurrentOffset + 2 * 4));

	}

	private static void readTransportHeader(TransportHeader transportHeader, IPHeader ipHeader,
			String packetTCPHeader) {
		int CurrentOffset = 0;
		System.out.println(ipHeader.TransportLayerProtocol);

		switch (ipHeader.TransportLayerProtocol) {

		case UDP:
			transportHeader.SourcePort = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;
			transportHeader.DestinationPort = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;
			transportHeader.Length = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;
			transportHeader.CheckSum = packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2);

			// UDP Data - we're not using this right now
			// Utils.skipBytes(transportHeader.Length - 8);

			break;

		case TCP:
			transportHeader.SourcePort = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;

			transportHeader.DestinationPort = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;

			transportHeader.SeqNum = Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 4));
			CurrentOffset += 2 * 4;

			transportHeader.AckNum = Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 4));
			CurrentOffset += 2 * 4;

			// for offset, take only top 4 bits n ignore next 4
			transportHeader.Offset = 4
					* (int) Utils.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 1));
			CurrentOffset += 2;

			transportHeader.TCPFlags = packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2);
			CurrentOffset += 2;

			transportHeader.Window = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;

			transportHeader.CheckSum = packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2);
			CurrentOffset += 2 * 2;

			transportHeader.UrgentPointer = (int) Utils
					.hexToDecimal(packetTCPHeader.substring(CurrentOffset, CurrentOffset + 2 * 2));
			CurrentOffset += 2 * 2;

			// Skip Options for now
			transportHeader.Options = packetTCPHeader.substring(CurrentOffset,
					CurrentOffset + transportHeader.Offset - 20);

			break;

		case ICMP:
			// Utils.skipBytes(ipHeader.TotalLength - ipHeader.HeaderLength);
			break;

		default:
			System.out.println("The Transport Layer Protocol " + ipHeader.TransportLayerProtocol + " is not supported");

		}
	}

}
