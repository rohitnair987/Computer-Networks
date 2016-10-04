package com.rohit.networks;

public enum ConstantsEnum {
	TCP, UDP, ICMP, OTHER;

	static ConstantsEnum setValue(int v) {
		switch (v) {
		case 6:
			return TCP;
		case 17:
			return UDP;
		case 1:
			return ICMP;
		default:
			return OTHER;
		}
	}
}
