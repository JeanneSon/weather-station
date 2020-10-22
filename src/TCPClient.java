
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author j
 */
public class TCPClient {

    private static final String SERVER_NAME = "127.0.0.1";
    private static final int DESTINATION_PORT_ID = 10001;
    private static final int WAIT_FOR_MESSAGE_TIME_MILLI_SEC = 3000;
    private static final int WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT = 5000;
    private static final int BUFFER_SIZE = 1024;

    private static Socket serviceAccessPoint;

    public TCPClient() {
        serviceAccessPoint = new Socket();
    }

    public static void sendMessage(String message) {
        try {
            //phase : CONNECTION ESTABLISHMENT

            // 1. Create a stream socket and connect it to the server socket
            // (port number, IP address)
            //Socket serviceAccesPoint = new Socket(SERVER_NAME, DESTINATION_PORT_ID);
            //
            //use TCP Service : set timer for connection establishment
            //TCP_TIMEOUT_REQ(timeout)
            //TCP_CONNECT_REQ(Peer-SAP address, quality of service)
            serviceAccessPoint.connect(new InetSocketAddress(SERVER_NAME, DESTINATION_PORT_ID), WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT);
            //TCP_CONNECT_CONFIRM(ok)

            //phase : DATA TRANSFER
            // 2. Construct a packet for sending a request
            String requestMessage = "QUESTION";
            // serialize data / encoding
            byte[] requestPDU = requestMessage.getBytes();

            // 3. Send the packet request to this socket
            OutputStream connectionEndPointOut = serviceAccessPoint.getOutputStream();
            System.out.println("Client sending request: " + requestMessage);
            //use TCP DATA service: TCP_DATA_REQ(requestPDU)
            connectionEndPointOut.write(requestPDU);

            // 6. release connection - close socket
            //TCP_DISCONNECT_REQ
            serviceAccessPoint.close();
        } catch (IOException ex) {
            System.out.println("TCP: Connect response failed - no connection");
        }

    }

    public static String awaitMessage() {
        try {

            // 4. Wait for response
            // build response buffer
            byte[] responsePDU = new byte[BUFFER_SIZE];

            boolean timeout = false;
            try {
                //use TCP service: TCP_TIMEOUT_REQUEST(timeout)
                serviceAccessPoint.setSoTimeout(WAIT_FOR_MESSAGE_TIME_MILLI_SEC);
                InputStream connectionEndPointIn = serviceAccessPoint.getInputStream();
                //use TCP DATA service: TCP_DATA_CNF(responsePDU)
                int bytesRead = connectionEndPointIn.read(responsePDU);
                //if (bytesRead == -1) { System.out.println("Timeout Workaround"); timeout = true; }
                System.out.println("bytes read: " + bytesRead);
            } catch (SocketTimeoutException e) {
                //TCP_ABORT_INDICATION(timeout)
                timeout = true;
            }

            // 5. process response or timeout
            if (!timeout) {
                // process response
                // deserialize response / decoding
                String responseMessage = new String(responsePDU).trim();
                System.out.println("Client received response: "
                        + responseMessage);

                return responseMessage;

            } else {
                System.out.println("Timeout action!");
            }

            //phase : CONNECTION RELEASE
        } catch (UnknownHostException e) {
            //TCP_CONNECT_CNF(fail)	//P_ABORD_IND
            e.printStackTrace();
        } catch (IOException ex) {
            System.out.println("TCP: Connect response failed - no connection");
        }

        return null;
    }
}
