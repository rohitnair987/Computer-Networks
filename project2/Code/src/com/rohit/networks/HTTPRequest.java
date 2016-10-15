package com.rohit.networks;

public class HTTPRequest {
	// To-do: check if this disturbs task 3
	long TimeStamp = 0;
	String Method = "";
	String URL = "";
	String Protocol = "";
	String Host = "n/a";
	long SeqNo = -1;
	int DataLength = 0;
	String FileExtension = ".jpg";

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
