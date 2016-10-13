package com.rohit.networks;

public class HTTPRequest {
	String TimeStamp = "";
	String Method = "";
	String URL = "";
	String Protocol = "";
	String Host = "n/a";
	long SeqNo = -1;
	int DataLength = 0;

	void display() {
		System.out.println("HTTP Request:");
		System.out.println("\tTimeStamp: " + TimeStamp);
		System.out.println("\tMethod: " + Method);
		System.out.println("\tURL: " + URL);
		System.out.println("\tProtocol: " + Protocol);
		System.out.println("\tHost: " + Host);
		System.out.println("\tSeqNo: " + SeqNo);
		System.out.println("\tDataLength: " + DataLength);
		System.out.println();
	}
}
