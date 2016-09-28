package com.rohit.networks;

import java.io.*;

import com.rohit.networks.Utils;
import com.rohit.networks.PCAPGlobalHeader;
import com.rohit.networks.PCAPHeader;

public class PCAPReader {

	public static PCAPGlobalHeader readPCAPGlobalHeader() throws IOException {
		Utils.skipBytes(16);

		PCAPGlobalHeader pcapHeader = new PCAPGlobalHeader();
		// Get Snaplength
		String snapLength = Utils.readBytesAsStringLittleEndian(4);
		pcapHeader.Snaplength = Utils.hexToDecimal(snapLength);

		// Get Link-Layer Protocol
		long linkLayerProtocol = Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));
		if (linkLayerProtocol == 1) {
			pcapHeader.LinkLayerProtocol = LinkLayerProtocolEnum.ETHERNET;
		}

		return pcapHeader;
	}

	public static PCAPHeader readPCAPHeader() throws IOException {
		Utils.skipBytes(8);

		PCAPHeader pcapHeader = new PCAPHeader();
		pcapHeader.NumberOfOctetsOfPacket = (int) Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));
		pcapHeader.ActualLengthOfPacket = (int) Utils.hexToDecimal(Utils.readBytesAsStringLittleEndian(4));

		return pcapHeader;
	}

	public static PCAPData readPCAPData() throws IOException {
//		Utils.displayNextIBytes(14);
		PCAPData pcapData = new PCAPData();
		
		return null;
	}

}
