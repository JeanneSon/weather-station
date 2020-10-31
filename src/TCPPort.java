
// This class contains all methods used by TCPServer and TCPClient
import java.net.Socket;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class TCPPort {
    //improve - tcp server erbt von tcp port; ebenso tcp client

    /**
     * If you would like to prevent instantiation would be to declare the constructor 
     * of the parent class as protected. 
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

    public static void sendMessage(Socket connectionEndPoint, String responseMessage) throws TCPException {
        try {
            // serialize data
            byte[] responsePDU = responseMessage.getBytes();

            OutputStream connectionEndPointOut = connectionEndPoint.getOutputStream();
            //Use TCP DATA service: TCP_DATA.response(responsePDU)
            connectionEndPointOut.write(responsePDU);

        } catch (IOException e) {
            throw new TCPException("sending failed");
        }
    }

    public static String awaitMessage(Socket socket, int BUFFER_SIZE) throws TCPException {

        try {
            // 3. receive a message on this client socket.
            byte[] requestPDU = new byte[BUFFER_SIZE];
            InputStream connectionEndPointIn = socket.getInputStream();

            int bytesRead = connectionEndPointIn.read(requestPDU);
            if (bytesRead >= BUFFER_SIZE) {
                throw new TCPException("reception failed since buffer too small");
            }
            if (bytesRead == -1) {
                socket.close();
                throw new TCPException("the other closed - so I closed as well");
            }
            // 4. process request
            // deserialize and return request
            return new String(requestPDU).trim();
        } catch (SocketException e) {
            e.printStackTrace();
            throw new TCPException("waiting expired - timeout");
        } catch (UnknownHostException e) {
            throw new TCPException("host unknown");
        } catch (IOException e) {
            if (!e.getMessage().equals("Read timed out"))
                throw new TCPException(e.getMessage());
        }
        return "";
    }

    public static void closeSocket(Socket point) throws TCPPort.TCPException {
        try {
            point.close();
        } catch (IOException e) {
            throw new TCPException("closing failed");
        }
    }
}
