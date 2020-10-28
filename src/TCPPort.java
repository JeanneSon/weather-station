// This class contains all methods used by TCPServer and TCPClient
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class TCPPort {
    //improve - tcp server erbt von tcp port; ebenso tcp client
    private TCPPort() {}

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

    public static void sendMessage(Socket connectionEndPoint, String responseMessage) throws TCPPort.TCPException {
        try {
            // serialize data
            byte[] responsePDU = responseMessage.getBytes();

            OutputStream connectionEndPointOut = connectionEndPoint.getOutputStream();
            //Use TCP DATA service: TCP_DATA.response(responsePDU)
            //System.out.println("Server sending response: " + responseMessage);
            connectionEndPointOut.write(responsePDU);

        } catch (IOException e) {
            throw new TCPPort.TCPException("sending failed");
        }
    }

    public static String awaitMessage(Socket connectionEndPoint, int BUFFER_SIZE) throws TCPPort.TCPException {

        try {
            // 3. receive a message on this client socket.
            byte[] requestPDU = new byte[BUFFER_SIZE];
            InputStream connectionEndPointIn = connectionEndPoint.getInputStream();

            int bytesRead = connectionEndPointIn.read(requestPDU);
            if (bytesRead >= BUFFER_SIZE) {
                throw new TCPPort.TCPException("reception failed since buffer too small");
            }
            // 4. process request
            // deserialize and return request
            return new String(requestPDU).trim();
        } catch (UnknownHostException e) {
            throw new TCPPort.TCPException("host unknown");
        } catch (IOException e) {

            // check whether connected to a client

            throw new TCPPort.TCPException("reception failed");
        }

        //return null;

    }
}
