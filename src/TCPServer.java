
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
public class TCPServer {

    public static ServerSocket serviceAccessPoint;
    public static final int SERVER_PORT = 10001;
    private static Socket connectionEndPoint;
    private static final int BUFFER_SIZE = 1024;

    // create ServerSocket
    public TCPServer() {
        try {
            serviceAccessPoint = new ServerSocket(SERVER_PORT);

        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message) {
        try {
            // Construct a packet for sending the response
            String responseMessage = message;
            // serialize data
            byte[] responsePDU = responseMessage.getBytes();

            OutputStream connectionEndPointOut = connectionEndPoint.getOutputStream();
            //Use TCP DATA service: TCP_DATA.response(responsePDU)
            System.out.println("Server sending response: " + responseMessage);
            connectionEndPointOut.write(responsePDU);

            

        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String awaitMessage() {

        try {
            // 1. create a server socket, bound to the server port.
            System.out.println("Server running on port "
                    + serviceAccessPoint.getLocalPort());

            // 2. Listen for a connection to be made to this socket and accept
            // it.
            //wait for connection request
            //TCP_CONNECT_IND
            connectionEndPoint = serviceAccessPoint.accept();

            System.out.println("connection establishment with "
                    + connectionEndPoint.getInetAddress().getHostAddress()
                    + ":" + connectionEndPoint.getPort());

            // 3. receive a message on this client socket.
            byte[] requestPDU = new byte[BUFFER_SIZE];
            InputStream connectionEndPointIn = connectionEndPoint.getInputStream();

            //Use TCP DATA service: TCP_DATA.indication(requestPDU)
            int bytesRead = connectionEndPointIn.read(requestPDU);
            if (bytesRead >= BUFFER_SIZE) {
                System.out.println("buffer to small");
            }
            // 4. process request
            // deserialize request
            String requestMessage = new String(requestPDU).trim();
            System.out.println("Server received request: " + requestMessage);

            

            return requestMessage;
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public void closer() {
        try {
            // 5. release the connection - close the client socket
            //TCP_DISCONNECT_REQUEST
            connectionEndPoint.close();

            // 6. close the server socket
            serviceAccessPoint.close();
        }
        catch (IOException ex) {
            //
        }
    }

}
