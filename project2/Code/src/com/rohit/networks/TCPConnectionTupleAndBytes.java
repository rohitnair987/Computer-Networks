package com.rohit.networks;

public class TCPConnectionTupleAndBytes {
	TCPConnectionTuple tcpConnectionTuple = new TCPConnectionTuple();
	int upstreamBytes = 0;
	int downstreamBytes = 0;

	public void display() {
		System.out.println(
				tcpConnectionTuple.ClientIP + " " + tcpConnectionTuple.ClientPort + " " + tcpConnectionTuple.ServerIP
						+ " " + tcpConnectionTuple.ServerPort + " " + upstreamBytes + " " + downstreamBytes);
	}
}
