package com.rohit.networks;

public class TransportHeader {
	/* Common fields */
	// 2 bytes
	int CheckSum;

	/* TCP - Total 8 bytes */
	// 2 bytes
	int SourcePort;
	// 2 bytes
	int DestinationPort;
	// 4 bytes
	long SeqNum;
	// 4 bytes
	long AckNum;
	// 4 bits
	int Offset;
	// 4 bits
	// reserved
	// 1 byte
	String TCPFlags;
	// 2 bytes
	int Window;
	// 2 bytes
	// String CheckSum; - declared globally
	// 2 bytes
	int UrgentPointer;
	// variable length options of length (offset - 20) bytes
	String Options;

	/* UDP - Total 8 bytes */
	// 2 bytes
	// int SourcePort; - declared under TCP
	// 2 bytes
	// int ; - declared under TCP
	// 2 bytes
	int Length;
	// 2 bytes
	// String CheckSum; - declared globally

	/*
	 * ICMP - Total 8 bytes - NOT USED RIGHT NOW
	 */
	// 1 byte
	// int Type;
	// 1 byte
	// int Code;
	// 2 bytes
	// int CheckSum; - declared globally
	// 4 bytes
	// long OtherMsgSpecificInfo;
}
