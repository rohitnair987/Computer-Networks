package com.rohit.networks;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPReader {

	// Read data from the request and update the HTTPRequest instance
	public static boolean readRequest(HTTPRequest request, PCAPData reqPkt, int taskNumber) {
		String[] requestLines = readLines(reqPkt).split("\r\n");

		// First line from the request
		String[] line1split = requestLines[0].split(" ");
		request.Method = line1split[0];
		request.URL = line1split[1].toLowerCase();

		if (taskNumber == 4) {
			int len = request.URL.length();
			if (len > 4) {
				String pattern = "(.jPg|.png|.Jpeg|.giF|.webp)$";
				Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				Matcher m = r.matcher(request.URL);
				if (!m.find()) {
					request.FileExtension = request.URL.substring(len - 4, len);
					return false;
				}
				// System.out.println(request.URL.substring(len - 4, len));
			} else {
				return false;
			}
		}
		request.Protocol = line1split[2];

		// Search if there's a Host header
		for (String line : requestLines) {
			if (line.startsWith("Host:")) {
				request.Host = line.split(" ")[1];
			}
		}

		return true;

	}

	// To-do: check if this disturbs task 3
	public static String readLines(PCAPData pkt) {
		return new String(Arrays.copyOfRange(pkt.Data, pkt.startInd, pkt.Data.length), Charset.forName("ISO-8859-1"));
	}

	// Read data from the response and update the HTTPResponse instance
	public static String readResponse(HTTPResponse response, PCAPData resPkt, byte[] resData) {
		String first8Chars = new String(Arrays.copyOfRange(resData, 0, 8));

		if (first8Chars.equals("HTTP/1.1")) {
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
					response.Content = new StringBuilder(
							rawResponseString.substring(rawResponseString.indexOf("\r\n\r\n") + 4));
					return "Content-Length";
				} else if (line.startsWith("Transfer-Encoding:")) {
					return rawResponseString.substring(rawResponseString.indexOf("\r\n\r\n") + 4);
				}
			}

		}
		return readLines(resPkt);

	}

	public static String readResponseTask4(HTTPResponse response, PCAPData resPkt, byte[] resData) {
		if (response.ContentType.equals("Content-Length")) {
			return readLines(resPkt);
		}
		if (response.ContentType.equals("Transfer-Encoding")) {
			StringBuilder sb = new StringBuilder();
			String rawResponseString = readLines(resPkt);
			String[] responseLines = rawResponseString.split("\r\n\r\n");
			for (int i = 0; i < responseLines.length; i += 2) {
				sb.append(responseLines[i]);
			}
			// To-do: try to convert this to return stringbuilder
			return sb.toString();
		}

		String first4Chars = new String(Arrays.copyOfRange(resData, 0, 4));
		String rawResponseString = readLines(resPkt);

		if (first4Chars.equals("HTTP")) {
			String[] responseLines = rawResponseString.split("\r\n");

			// First line from the response
			String[] line1split = responseLines[0].split(" ");
			if (line1split.length >= 3) {
				response.StatusCode = Utils.stringToNumber(line1split[1]);
				if (response.StatusCode != 200) {
					return "StatusNot200";
				}
			}
			for (String line : responseLines) {
				// Search if there's a Content-Length header
				if (line.startsWith("Content-Length")) {
					response.Content.append(rawResponseString.substring(rawResponseString.indexOf("\r\n\r\n") + 4));
					// response.Content.append(rawResponseString);
					response.ContentLength = Utils.stringToNumber(line.split(" ")[1]);
					return "Content-Length";
				}
				if (line.startsWith("Transfer-Encoding:")) {
					response.ContentType = "Transfer-Encoding:";
					return rawResponseString.substring(rawResponseString.indexOf("\r\n\r\n") + 4);
				}
			}

		}
		if (response.ContentType.equals("StatusNot200")) {
			return "StatusNot200";
		}
		return rawResponseString;

	}

	public static int countDataBytes(String chunkedData) {
		int dataLen = 0;
		int pos = 0;
		while (true) {
			int newLine = chunkedData.indexOf("\r\n");
			if (newLine == -1) {
				break;
			}
			String chunkSizeHex = chunkedData.substring(0, newLine);
			pos = chunkSizeHex.length() + 2;
			int chunkSize = (int) Utils.hexToDecimal(chunkSizeHex);
			if (chunkSize == 0) {
				break;
			}
			dataLen += chunkSize;
			pos += chunkSize + 2;
			if (pos >= chunkedData.length()) {
				break;
			}
			chunkedData = chunkedData.substring(pos);
		}

		return dataLen;
	}

}
