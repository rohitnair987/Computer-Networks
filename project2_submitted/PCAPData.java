

public class PCAPData {
	long PacketTimeStamp;
	LinkHeader linkHeader = new LinkHeader();
	IPHeader ipHeader = new IPHeader();
	TransportHeader transportHeader = new TransportHeader();
	byte[] Data;
	int startInd;
}
