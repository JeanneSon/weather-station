import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class manages the server.
 * @author J.Krug, H.Schall
 */
public class TCPServer extends TCPPort {

    public static ServerSocket serviceAccessPoint;
    public static final int SERVER_PORT = 10001;
    private static Socket connectionEndPoint;
    private static final int BUFFER_SIZE = 1024;
    
    /**
     * create a server socket, bound to the server port
     * first step of a TCP connection
     * @throws TCPException
     */
    public TCPServer() throws TCPException {
        try {
            serviceAccessPoint = new ServerSocket(SERVER_PORT);
        } catch (IOException ex) {            
            throw new TCPException("--------- starting server failed ---------");
        }
    }
    
    /**
     * get connectionEndPoint
     * @return connectionEndPoint
     */
    public Socket getConnectionEndPoint() {
        return connectionEndPoint;
    }

    /**
     * sends a message
     * @param responseMessage the message to be send
     * @throws TCPException if sending failed
     */
    public void sendMessage(String responseMessage) throws TCPException {
        TCPPort.sendMessage(connectionEndPoint, responseMessage);
    }

    /**
     * waits for connection and accepts it
     * @return feedback
     * @throws TCPException
     */
    public String awaitConnection() throws TCPException {   
        String toReturn = "Server running on port " + serviceAccessPoint.getLocalPort();
        try {
            connectionEndPoint = serviceAccessPoint.accept();
            return toReturn + "\nconnection establishment with " + connectionEndPoint.getInetAddress().getHostAddress()
                    + ":" + connectionEndPoint.getPort();
        } catch (IOException e) {
            throw new TCPException(toReturn + " but waiting failed");
        }
    }

    /**
     * awaits a message from peer socket
     * @return message
     * @throws TCPException
     */
    public String awaitMessage() throws TCPException {
        return TCPPort.awaitMessage(TCPServer.connectionEndPoint, BUFFER_SIZE);
    }

    /**
     * close all sockets on server side
     * @throws TCPException if closing failed
     */
    public void closeAll() throws TCPException {
        closeSocket(connectionEndPoint);
        closeServerSocket();
    }

    /**
     * close the current socket connected to the client
     * @throws TCPException
     */
    public void closeCurrentSocket() throws TCPException {
        closeSocket(connectionEndPoint);
    }

    /**
     * close the ServerSocket connectionEndPoint
     * @throws TCPException
     */
    public void closeServerSocket() throws TCPException {
        try {
            serviceAccessPoint.close();
        } catch (IOException e) {
            throw new TCPException("closing server socket failed");
        }
    }

}
