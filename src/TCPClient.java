
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        try {
            serviceAccessPoint = new Socket();
            serviceAccessPoint.connect(new InetSocketAddress(SERVER_NAME, DESTINATION_PORT_ID), WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT);
        } catch (IOException ex) {
            System.err.println("TCP: Connect response failed - no connection");
        }
    }

    public void sendMessage(String requestMessage) {
        try {
            //phase : CONNECTION ESTABLISHMENT

            // 1. Create a stream socket and connect it to the server socket
            // (port number, IP address)
            //Socket serviceAccesPoint = new Socket(SERVER_NAME, DESTINATION_PORT_ID);
            //
            //use TCP Service : set timer for connection establishment
            //TCP_TIMEOUT_REQ(timeout)
            //TCP_CONNECT_REQ(Peer-SAP address, quality of service)
            //TCP_CONNECT_CONFIRM(ok)
            //phase : DATA TRANSFER
            // 2. Construct a packet for sending a request
            // serialize data / encoding
            byte[] requestPDU = requestMessage.getBytes();

            // 3. Send the packet request to this socket
            OutputStream connectionEndPointOut = serviceAccessPoint.getOutputStream();
            //System.out.println("Client sending request: " + requestMessage);
            //use TCP DATA service: TCP_DATA_REQ(requestPDU)
            connectionEndPointOut.write(requestPDU);

        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String awaitMessage() {
        try {

            // 4. Wait for response
            // build response buffer
            try {
                //use TCP service: TCP_TIMEOUT_REQUEST(timeout)
                serviceAccessPoint.setSoTimeout(WAIT_FOR_MESSAGE_TIME_MILLI_SEC);
                InputStream connectionEndPointIn = serviceAccessPoint.getInputStream();
                //use TCP DATA service: TCP_DATA_CNF(responsePDU)  
                byte[] responsePDU = new byte[BUFFER_SIZE];
                int bytesRead = connectionEndPointIn.read(responsePDU);
                //if (bytesRead == -1) { System.out.println("Timeout Workaround"); timeout = true; }
                //System.out.println("bytes read: " + bytesRead);

                String responseMessage = new String(responsePDU).trim();
                //System.out.println("Client received response: " + responseMessage);

                return responseMessage;
            } catch (SocketTimeoutException e) {

                //System.out.println("Timeout action!");
            }

            //phase : CONNECTION RELEASE
        } catch (UnknownHostException e) {
            //TCP_CONNECT_CNF(fail)	//P_ABORD_IND
            e.printStackTrace();
        } catch (IOException ex) {
            System.err.println("TCP: Connect response failed - no connection");
        }

        return null;
    }

    public void closer() {
        try {
            // 6. release connection - close socket
            //TCP_DISCONNECT_REQ
            serviceAccessPoint.close();
        } catch (IOException e) {
            //TODO: handle exception
        }
    }
}
