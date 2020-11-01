
import java.io.IOException;
import java.net.ServerSocket;
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

// extract code snippets they have in common

public class TCPServer extends TCPPort {

    public static ServerSocket serviceAccessPoint;
    public static final int SERVER_PORT = 10001;
    private static Socket connectionEndPoint;
    private static final int BUFFER_SIZE = 1024;
    //private static final int WAIT_FOR_MESSAGE_TIME_MILLI_SEC = 4000;

    public Socket getConnectionEndPoint() {
        return connectionEndPoint;
    }

    // create ServerSocket
    public TCPServer() throws TCPException {
        // 1. create a server socket, bound to the server port.
        try {
            serviceAccessPoint = new ServerSocket(SERVER_PORT);

        } catch (IOException ex) {


//when does new ServerSocket throw IOException?

            throw new TCPException("starting server failed");
        }
    }

    public void sendMessage(String responseMessage) throws TCPException {
        System.out.println("I send .. " + responseMessage);
        TCPPort.sendMessage(TCPServer.connectionEndPoint, responseMessage);
    }

    public String awaitConnection() throws TCPException {
        try {
            String toReturn = "Server running on port " + serviceAccessPoint.getLocalPort();

            // Listen for a connection to be made to this socket and accept it
            // wait for connection request
            connectionEndPoint = serviceAccessPoint.accept();
            return toReturn + "\nconnection establishment with " + connectionEndPoint.getInetAddress().getHostAddress()
                    + ":" + connectionEndPoint.getPort();

        } catch (IOException e) {
            throw new TCPException("waiting failed");
        }
    }

    public String awaitMessage() throws TCPException {
        return TCPPort.awaitMessage(TCPServer.connectionEndPoint, BUFFER_SIZE);
    }

    public void closeAll() throws TCPException {
        closeSocket(connectionEndPoint);
        closeServerSocket();
    }

    public void closeCurrentSocket() throws TCPException {
        closeSocket(connectionEndPoint);
    }

    public void closeServerSocket() throws TCPException {
        try {
            serviceAccessPoint.close();
        } catch (IOException e) {
            throw new TCPException("closing server socket failed");
        }
    }

}
