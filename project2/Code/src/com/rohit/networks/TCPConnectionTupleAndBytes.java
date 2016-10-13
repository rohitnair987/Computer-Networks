package com.rohit.networks;

import java.util.ArrayList;

public class TCPConnectionTupleAndBytes {
	TCPConnectionTuple tcpConnectionTuple = new TCPConnectionTuple();
	ArrayList<byte[]> upstreamBytes = new ArrayList<byte[]>();
	ArrayList<byte[]> downstreamBytes = new ArrayList<byte[]>();
	ArrayList<Integer> PacketNumber = new ArrayList<Integer>();
	long PacketTimeStamp;

	public void display() {
		System.out.println(
				tcpConnectionTuple.ClientIP + " " + tcpConnectionTuple.ClientPort + " " + tcpConnectionTuple.ServerIP
						+ " " + tcpConnectionTuple.ServerPort + " " + upstreamBytes + " " + downstreamBytes);
	}

	public String toString() {
		StringBuilder s = new  StringBuilder();
		s.append(tcpConnectionTuple.ClientIP + " " + tcpConnectionTuple.ClientPort + " " + tcpConnectionTuple.ServerIP
				+ " " + tcpConnectionTuple.ServerPort + " ");
		s.append("\n");
		for(int p : PacketNumber) {
			s.append(p).append(" ");
		}
		s.append("\n");
		return s.toString();
	}
}
