package com.rohit.networks;

public class PCAPHeader {
	int NumberOfOctetsOfPacket;
	int ActualLengthOfPacket;
	
	public PCAPHeader() {
		NumberOfOctetsOfPacket = 0;
		ActualLengthOfPacket = 0;
	}
}
