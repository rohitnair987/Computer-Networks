package com.rohit.networks;

import java.util.Arrays;

public class HTTPReader {

	// Read data from the request and update the HTTPRequest instance
	public static void readRequest(HTTPRequest request, PCAPData reqPkt) {
		String[] requestLines = readLines(reqPkt).split("\r\n");

		// First line from the request
		String[] line1split = requestLines[0].split(" ");
		request.Method = line1split[0];
		request.URL = line1split[1].toLowerCase();
		request.Protocol = line1split[2];

		// Search if there's a Host header
		for (String line : requestLines) {
			if (line.startsWith("Host:")) {
				request.Host = line.split(" ")[1];
			}
		}

	}

	// Convert Data from byte[] to String
	public static String readLines(PCAPData pkt) {
		StringBuffer sb = new StringBuffer();
		int i = pkt.startInd;
		while (i < pkt.Data.length) {
			sb.append((char) pkt.Data[i++]);
		}

		return sb.toString();
	}

	// Read data from the response and update the HTTPResponse instance
	public static String readResponse(HTTPResponse response, PCAPData resPkt, byte[] resData) {
		String first4Chars = new String(Arrays.copyOfRange(resData, 0, 4));

		if (first4Chars.equals("HTTP")) {
			String rawResponseString = readLines(resPkt);
			String[] responseLines = rawResponseString.split("\r\n");

			// First line from the response
			String[] line1split = responseLines[0].split(" ");
			if (line1split.length >= 3) {
				response.StatusCode = Utils.stringToNumber(line1split[1]);
				response.Description = line1split[2];
			}
			for (String line : responseLines) {
				// Search if there's a Content-Length header
				if (line.startsWith("Content-Length")) {
					response.ContentLength = Utils.stringToNumber(line.split(" ")[1]);
					return "Content-Length";
				} else if (line.startsWith("Transfer-Encoding:")) {
					return rawResponseString.substring(rawResponseString.indexOf("\r\n\r\n") + 3);
				}
			}

		}
		return readLines(resPkt);

	}

	public static int countDataBytes(String chunkedData) {
		String[] data = chunkedData.split("\r\n");

		int res = (int) Utils.hexToDecimal(data[0].substring(1, data[0].length()));
		for (int i = 2; i < data.length; i += 2) {
			res += Utils.hexToDecimal(data[i]);
		}

		return res;
	}

}
