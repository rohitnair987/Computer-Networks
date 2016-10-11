package com.rohit.networks;

import java.util.ArrayList;

public class TCPConnectionTupleAndBytes {
	TCPConnectionTuple tcpConnectionTuple = new TCPConnectionTuple();
	ArrayList<byte[]> upstreamBytes = new ArrayList<byte[]>();
	ArrayList<byte[]> downstreamBytes = new ArrayList<byte[]>();

	public void display() {
		System.out.println(
				tcpConnectionTuple.ClientIP + " " + tcpConnectionTuple.ClientPort + " " + tcpConnectionTuple.ServerIP
						+ " " + tcpConnectionTuple.ServerPort + " " + upstreamBytes + " " + downstreamBytes);
	}
}
