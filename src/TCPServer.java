
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

//extract code snippets they have in common


public class TCPServer {

    public static ServerSocket serviceAccessPoint;
    public static final int SERVER_PORT = 10001;
    private static Socket connectionEndPoint;
    private static final int BUFFER_SIZE = 1024;

    // create ServerSocket
    public TCPServer() throws TCPPort.TCPException {
        // 1. create a server socket, bound to the server port.
        try {
            serviceAccessPoint = new ServerSocket(SERVER_PORT);

        } catch (IOException ex) {
            throw new TCPPort.TCPException("starting server failed");
        }
    }

    public void sendMessage(String responseMessage) throws TCPPort.TCPException {
        TCPPort.sendMessage(TCPServer.connectionEndPoint, responseMessage);
    }

    public String awaitConnection() throws TCPPort.TCPException {
        try {
            String toReturn = "Server running on port " + serviceAccessPoint.getLocalPort();

            // 2. Listen for a connection to be made to this socket and accept it
            //wait for connection request
            connectionEndPoint = serviceAccessPoint.accept();
            return toReturn + "\nconnection establishment with "
                    + connectionEndPoint.getInetAddress().getHostAddress()
                    + ":" + connectionEndPoint.getPort();

        } catch (IOException e) {
            throw new TCPPort.TCPException("waiting failed");
        }
    }

    public String awaitMessage() throws TCPPort.TCPException {
        return TCPPort.awaitMessage(TCPServer.connectionEndPoint, BUFFER_SIZE);
    }

    public void closer() throws TCPPort.TCPException {
        try {
            // 5. release the connection - close the client socket
            //TCP_DISCONNECT_REQUEST
            connectionEndPoint.close();

            // 6. close the server socket
            serviceAccessPoint.close();
        } catch (IOException ex) {
            throw new TCPPort.TCPException("closing failed");
        }
    }

}
