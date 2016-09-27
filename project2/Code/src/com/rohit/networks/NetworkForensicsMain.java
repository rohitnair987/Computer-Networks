package com.rohit.networks;

import java.io.IOException;
import java.util.Date;

import com.rohit.networks.PCAPFileReader;

public class NetworkForensicsMain {

	public static void main(String[] args) throws IOException {
		long d1 = new Date().getTime();
		int i = PCAPFileReader.readPCAPFile();
		
		
		System.out.println("\nTime taken = " + (new Date().getTime() - d1) + "ms");
	}

}
