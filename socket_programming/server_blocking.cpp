#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <errno.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <sys/timeb.h>
#include <fcntl.h>
#include <stdarg.h>

typedef unsigned char BYTE;
typedef unsigned int DWORD;
typedef unsigned short WORD;

#define MAX_REQUEST_SIZE 10000000

void Error(const char * format, ...) {
	char msg[4096];
	va_list argptr;
	va_start(argptr, format);
	vsprintf(msg, format, argptr);
	va_end(argptr);
	fprintf(stderr, "Error: %s\n", msg);
	exit(-1);
}

void Log(const char * format, ...) {
	char msg[2048];
	va_list argptr;
	va_start(argptr, format);
	vsprintf(msg, format, argptr);
	va_end(argptr);
	fprintf(stderr, "%s\n", msg);
}

void CheckData(BYTE * buf, int size) {
	for (int i=0; i<size; i++) if (buf[i] != 'A' + i % 26) {
		Error("Received wrong data.");
	}
}

int Send_Blocking(int sockFD, const BYTE * data, int len) {
	int nSent = 0;
	while (nSent < len) {
		int n = send(sockFD, data + nSent, len - nSent, 0);
		if (n >= 0) {
			nSent += n;
		} else if (n < 0 && (errno == ECONNRESET || errno == EPIPE)) {
			Log("Connection closed.");
			close(sockFD);
			return -1;
		} else {
			Error("Unexpected error %d: %s.", errno, strerror(errno));
		}
	}
	return 0;
}

int Recv_Blocking(int sockFD, BYTE * data, int len) {
	int nRecv = 0;
	while (nRecv < len) {
		int n = recv(sockFD, data + nRecv, len - nRecv, 0);
		if (n > 0) {
			nRecv += n;
		} else if (n == 0 || (n < 0 && errno == ECONNRESET)) {
			Log("Connection closed.");
			close(sockFD);
			return -1;
		} else {
			Error("Unexpected error %d: %s.", errno, strerror(errno));
		}
	}
	return 0;
}

void DoServer(int svrPort) {
	int i;	
	BYTE * buf = (BYTE *)malloc(MAX_REQUEST_SIZE);	
	BYTE request[4];
	
	int listenFD = socket(AF_INET, SOCK_STREAM, 0);
	if (listenFD < 0) {
		Error("Cannot create listening socket.");
	}
	
	struct sockaddr_in serverAddr;
	memset(&serverAddr, 0, sizeof(struct sockaddr_in));	
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons((unsigned short) svrPort);
	serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);

	//prepare data
	for (int i=0; i<MAX_REQUEST_SIZE; i++) {
		buf[i] = 'A' + i % 26;
	}

	int optval = 1;
	int r = setsockopt(listenFD, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));
	if (r != 0) {
		Error("Cannot enable SO_REUSEADDR option.");
	}
	
	if (bind(listenFD, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) != 0) {
		Error("Cannot bind to port %d.", svrPort);
	}
	
	if (listen(listenFD, 16) != 0) {
		Error("Cannot listen to port %d.", svrPort);
	}
	
	int connID = 0;
	while (1) {	//the main loop
		struct sockaddr_in clientAddr;
		socklen_t clientAddrLen = sizeof(clientAddr);			
		int fd = accept(listenFD, (struct sockaddr *)&clientAddr, &clientAddrLen);
		if (fd == -1) {
			Error("Cannot accept an incoming connection request.");	
		}
		
		connID++;
								
		int size;
		if (Recv_Blocking(fd, (BYTE *)&size, 4) < 0) continue;
		
		if (size <= 0 || size > MAX_REQUEST_SIZE) {
			Error("Invalid size: %d.", size);
		}
		
		Log("Transaction %d: %d bytes", connID, size);
		
		if (Send_Blocking(fd, buf, size) < 0) continue;
		close(fd);
	}	
}

int main(int argc, char * * argv) {
	
	if (argc != 2) {
		Log("Usage: %s [server Port]", argv[0]);
		return -1;
	}
	
	int port = atoi(argv[1]);
	DoServer(port);
	
	return 0;
}

