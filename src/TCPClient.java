
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author j
 */
public class TCPClient extends TCPPort {

    private static final String SERVER_NAME = "127.0.0.1";
    private static final int DESTINATION_PORT_ID = 10001;
    //private static final int WAIT_FOR_MESSAGE_TIME_MILLI_SEC = 3000;
    private static final int WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT = 1500;
    private static final int BUFFER_SIZE = 1024;

    private static Socket serviceAccessPoint;

    public TCPClient() throws TCPException {
        // 1. Create a stream socket and connect it to the server socket
        // (port number, IP address)
        try {
            serviceAccessPoint = new Socket();
            serviceAccessPoint.connect(
                                        new InetSocketAddress(SERVER_NAME, DESTINATION_PORT_ID), 
                                        WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT
                                );
            System.out.println(serviceAccessPoint.toString());
        } catch (IOException ex) {
            throw new TCPException("------------ conenction failed; could not connect to a sensor ------");
        } catch (Exception e) {
            throw new TCPException("TCP: Connect failed - other exception");
        }
    }

    public void sendMessage(String responseMessage) throws TCPException {
        sendMessage(TCPClient.serviceAccessPoint, responseMessage);
    }

    public String awaitMessage() throws TCPException {
        return awaitMessage(TCPClient.serviceAccessPoint, BUFFER_SIZE);
    }

    public void closeSocket() throws TCPException{
        closeSocket(serviceAccessPoint);   
    }

    public boolean serverIsInactive() {
        return serviceAccessPoint.isClosed();
    }
}
