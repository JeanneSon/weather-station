
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This class manages the client
 *
 * @author J.Krug, H.Schall
 */
public class TCPClient extends TCPPort {

    private static final String SERVER_NAME = "127.0.0.1";
    private static final int DESTINATION_PORT_ID = 10001;
    private static final int WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT = 1500;
    private static final int BUFFER_SIZE = 1024;

    private static Socket serviceAccessPoint;

    /**
     * create a stream socket and connect it to the server socket
     *
     * @throws TCPException
     */
    public TCPClient() throws TCPException {
        try {
            serviceAccessPoint = new Socket();
            serviceAccessPoint.connect(
                    new InetSocketAddress(SERVER_NAME, DESTINATION_PORT_ID),
                    WAIT_TIME_FOR_CONNECTION_ESTABLISHMENT
            );
        } catch (IOException ex) {
            throw new TCPException("------------ connection failed; could not connect to a sensor ------");
        } catch (Exception e) {
            throw new TCPException("TCP: Connect failed - other exception");
        }
    }

    /**
     * sends a message
     *
     * @param responseMessage the message to be send
     * @throws TCPException if sending failed
     */
    public void sendMessage(String responseMessage) throws TCPException {
        sendMessage(TCPClient.serviceAccessPoint, responseMessage);
    }

    /**
     * awaits a message from peer socket
     *
     * @return message
     * @throws TCPException
     */
    public String awaitMessage() throws TCPException {
        return awaitMessage(TCPClient.serviceAccessPoint, BUFFER_SIZE);
    }

    /**
     * close the socket connected to the server
     *
     * @throws TCPException
     */
    public void closeSocket() throws TCPException {
        closeSocket(serviceAccessPoint);
    }

    /**
     * check if server is inactive
     *
     * @return true if socket on server side is closed , else false
     */
    public boolean serverIsInactive() {
        return serviceAccessPoint.isClosed();
    }
}
