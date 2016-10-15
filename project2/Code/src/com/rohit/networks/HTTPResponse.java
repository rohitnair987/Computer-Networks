package com.rohit.networks;

public class HTTPResponse {
	int StatusCode;
	String Description = "";
	int ContentLength = 0;
	String ContentType = "Unknown";
	StringBuilder Content = new StringBuilder();
	String fileName = "";
	String FileExtension = ".jpg";
	
	void display() {
		System.out.println("HTTP Response:");
		System.out.println("\tStatusCode: " + StatusCode);
		System.out.println("\tDescription: " + Description);
		System.out.println("\tContentLength: " + ContentLength);

	}
}
