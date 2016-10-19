import java.util.HashMap;

public class Temp {

	public static void display(String s, byte[] packetInBytes, int startInd, int endInd) {
		System.out.print(s + ": ");
		for (int i = startInd; i <= endInd; i++) {
			System.out.printf(String.format("%02x ", packetInBytes[i]));
		}
		System.out.println();
	}

	public static void display(IPHeader ipHeader) {
		System.out.println("\nIP header: ");
		System.out.println("ver: " + ipHeader.Version);
		System.out.println("hlen: " + ipHeader.HeaderLength);
		// System.out.println("TypeOfService: " + ipHeader);
		System.out.println("totlen: " + ipHeader.TotalLength);
		System.out.println("Identification: " + ipHeader.Identification);
		// System.out.println("flags and fragment offset: " + ipHeader);
		System.out.println("TTL: " + ipHeader.TTL);
		System.out.println("TransProtocol: " + ipHeader.TransportLayerProtocol);
		System.out.println("Checksum: " + ipHeader.HeaderChecksum);
		System.out.println("Source: " + ipHeader.Source);
		System.out.println("Destination: " + ipHeader.Destination);
	}

	public static void display(TransportHeader transportHeader, ConstantsEnum transportLayerProtocol) {
		System.out.println("\nTransport header: ");
		switch (transportLayerProtocol) {
		case UDP:
			System.out.println("SourcePort: " + transportHeader.SourcePort);
			System.out.println("DestinationPort: " + transportHeader.DestinationPort);
			System.out.println("Length: " + transportHeader.Length);
			System.out.println("CheckSum: " + transportHeader.CheckSum);
			break;

		case TCP:
			System.out.println("SourcePort: " + transportHeader.SourcePort);
			System.out.println("DestinationPort: " + transportHeader.DestinationPort);
			System.out.println("SeqNum: " + transportHeader.SeqNum);
			System.out.println("AckNum: " + transportHeader.AckNum);
			System.out.println("Offset: " + transportHeader.Offset);
			System.out.println("TCPFlags: " + transportHeader.TCPFlags);
			System.out.println("Window: " + transportHeader.Window);
			System.out.println("CheckSum: " + transportHeader.CheckSum);
			System.out.println("UrgentPointer: " + transportHeader.UrgentPointer);
			System.out.println("Options: " + transportHeader.Options);
			break;

		default:
			System.out.println(transportLayerProtocol + " not supported right now");
		}
	}

}
