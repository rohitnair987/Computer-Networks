package com.rohit.networks;

public class HTTPResponse {
	int StatusCode;
	String Description = "";
	int ContentLength = 0;

	void display() {
		System.out.println("HTTP Response:");
		System.out.println("\tStatusCode: " + StatusCode);
		System.out.println("\tDescription: " + Description);
		System.out.println("\tContentLength: " + ContentLength);

	}
}
