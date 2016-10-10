package com.rohit.networks;

public class TCPConnectionTuple {
	String ClientIP = "";
	int ClientPort = 0;
	String ServerIP = "";
	int ServerPort = 0;

	public int calculateHashCode() {
		return (ClientIP + ClientPort).hashCode() + (ServerIP + ServerPort).hashCode();
	}

	public void display() {
		System.out.print(ClientIP + " " + ClientPort + " " + ServerIP + " " + ServerPort + " ");
	}
	
	public String tupleString() {
		StringBuffer sb = new StringBuffer();
		sb.append(ClientIP);
		sb.append(" ");
		sb.append(ClientPort);
		sb.append(" ");
		sb.append(ServerIP);
		sb.append(" ");
		sb.append(ServerPort);
		sb.append(" ");
		
		return sb.toString();
	}
	
}
