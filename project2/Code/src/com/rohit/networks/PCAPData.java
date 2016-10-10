package com.rohit.networks;

public class PCAPData {
	LinkHeader linkHeader = new LinkHeader();
	IPHeader ipHeader = new IPHeader();
	TransportHeader transportHeader = new TransportHeader();
	byte[] Data;
	int startInd;
}
