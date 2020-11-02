import java.net.Socket;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * This class contains the methods used by both classes - TCPServer and TCPClient
 * @author J.Krug, H.Schall
 */
public class TCPPort {

    /**
     * prevent instantiation -> declare the constructor 
     * of the parent class as protected
     * That way, it will not be visible to outside classes other than the ones which extend it.
     * (https://softwareengineering.stackexchange.com/questions/271410/should-abstract-classes-be-used-to-prevent-instantiation)
     */
    protected TCPPort() {}

    public static class TCPException extends Exception {

        /**
         * Exceptions should always be serializale --> serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        public TCPException() {
            super();    
        }
    
        public TCPException(String msg) {
            super(msg);
        }
    }

    /**
     * sends the given string via TCP over the given socket
     * @param connectionEndPoint the given socket
     * @param responseMessage the given string
     * @throws TCPException if sending failed
     */
    public static void sendMessage(Socket connectionEndPoint, String responseMessage) throws TCPException {
        try {
            // serialize data
            //throws UnsupportedEncodingException (is caught by catching IOException)
            byte[] responsePDU = responseMessage.getBytes(); 

            OutputStream connectionEndPointOut = connectionEndPoint.getOutputStream(); //throws IOException

            connectionEndPointOut.write(responsePDU); //throws IOException

            // OutputStream is not closed since "Closing the returned 
            // OutputStream will close the associated socket."(JavaDoc)
        } catch (IOException e) {
            throw new TCPException("sending failed");
        }
    }

    /**
     * given socket awaits a message from peer socket
     * @param socket
     * @param BUFFER_SIZE
     * @return received message or empty string
     * @throws TCPException
     */
    public static String awaitMessage(Socket socket, int BUFFER_SIZE) throws TCPException {

        try {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            // timeout ensures that InputStream is not blocked in reading forever in case of not receiving a message
            socket.setSoTimeout(200); // throws SocketException if error in TCP protocol

            byte[] requestPDU = new byte[BUFFER_SIZE];
            InputStream connectionEndPointIn = socket.getInputStream(); // throws IOException
            int bytesRead = connectionEndPointIn.read(requestPDU); // throws IOException
            if (bytesRead > BUFFER_SIZE) {
                // this should not happen
                throw new TCPException("--------reception failed since buffer too small----------");
            }
            if (bytesRead == -1) {
                // -1 indicates that peer socket is closed
                socket.close(); // throws IOException
                throw new TCPException("----- closing the socket in reaction to closed peer socket ----");
            }
            
            // deserialize and return request
            return new String(requestPDU).trim();

        } catch (SocketException e) {
            if (e.getMessage().equals("Socket closed")) {
                System.out.println("---------------- the socket is now closed -------------");
            } else if (e.getMessage().equals("Connection reset")) {
                System.out.println("---- connection reset ---");
                closeSocket(socket);
            }
            else {
                throw new TCPException(e.getMessage());
            }
        } catch (UnknownHostException e) {
            throw new TCPException("host unknown");
        } catch (IOException e) {
            if (!e.getMessage().equals("Read timed out"))
                throw new TCPException(e.getMessage());
        }
        return "";
    }

    /**
     * close a socket
     * @param socket
     * @throws TCPPort.TCPException if closing failed
     */
    public static void closeSocket(Socket socket) throws TCPPort.TCPException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new TCPException("closing failed");
        }
    }
}
