
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.net.UnknownHostException;

public class WeatherStation {
    private static final String SERVER_NAME = "127.0.0.1";
    private static final int DESTINATION_PORT_ID = 10001;
    
    public static void main(String[] args) {
		try {
			//phase : CONNECTION ESTABLISHMENT
			
			// 1. Create a stream socket and connect it to the server socket
			// (port number, IP address)
			//Socket serviceAccesPoint = new Socket(SERVER_NAME, DESTINATION_PORT_ID);
			Socket serviceAccessPoint = new Socket();
			//use TCP Service : set timer for connection establishment
			//TCP_TIMEOUT_REQ(timeout)
			//TCP_CONNECT_REQ(Peer-SAP address, quality of service)
			serviceAccessPoint.connect(new InetSocketAddress(SERVER_NAME, DESTINATION_PORT_ID), WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT);
            //TCP_CONNECT_CONFIRM(ok)
            serviceAccessPoint.close();
		} catch (UnknownHostException e) {
			//TCP_CONNECT_CNF(fail)	//P_ABORD_IND
			e.printStackTrace();
		} catch (IOException e) {
			//TCP_TIMEOUT_IND
			System.out.println("TCP: Connect response failed - no connection");
		}
}
