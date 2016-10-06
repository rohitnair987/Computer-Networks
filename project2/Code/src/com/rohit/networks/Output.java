package com.rohit.networks;

class Task1 {
	int TotalPackets = 0;
	int IPPackets = 0;
	int TCPPackets = 0;
	int UDPPackets = 0;
	int TCPConnections = 0;
}

public class Output {
	Task1 Task1 = new Task1();

	void display(int taskNumber) {
		switch(taskNumber){
		case 1:
//			System.out.println("Total Packets = " + Task1.TotalPackets);
//			System.out.println("IP Packets = " + Task1.IPPackets);
//			System.out.println("TCP Packets = " + Task1.TCPPackets);
//			System.out.println("UDP Packets = " + Task1.UDPPackets);
//			System.out.println("TCP Connections = " + Task1.TCPConnections);
			String outString =  Task1.TotalPackets + " " + Task1.IPPackets + " " + Task1.TCPPackets + " " + Task1.TCPConnections;
			System.out.println(outString);
			break;
		}
		
	}
}
