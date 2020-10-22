import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;

// class is final as no class should inherit from this class
public final class Sensor {
    // attributes are final as they can only be set once when the Sensor is instanciated
    private final int productId;
    private final int vendorId;
    private final String location;
    private static final double MAX_TEMP =  50.0; 
    private static final double MIN_TEMP = -20.0;

    //definition of the service access point address - SAP - local host and port 
	public static final int SERVER_PORT = 10001;
    
    public Sensor(int productId, int vendorId, String location) {
        this.productId = productId;
        this.vendorId = vendorId;
        this.location = location;
    }

    public String info() {
        return "ProductId: " + this.productId + ", VendorId: " + this.vendorId + ", Standort " + this.location;
    }

    private double generateValue() {
        return MIN_TEMP + (MAX_TEMP - MIN_TEMP) * ImportManager.generateRandomDouble();
    }

    public static void main(String[] args) {
		try {
			// 1. create a server socket, bound to the server port.
			ServerSocket serviceAccessPoint = new ServerSocket(SERVER_PORT);
			System.out.println("Server running on port "
					+ serviceAccessPoint.getLocalPort());

			// 2. Listen for a connection to be made to this socket and accept
			// it.
			//wait for connection request
			//TCP_CONNECT_IND
			Socket connectionEndPoint = serviceAccessPoint.accept();

			System.out.println("connection establishment with "
					+ connectionEndPoint.getInetAddress().getHostAddress()
					+ ":" + connectionEndPoint.getPort());

			// 3. receive a message on this client socket.
			final int BUFFER_SIZE = 1024;
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

			// Construct a packet for sending the response
			String responseMessage = "ANSWER";
			// serialize data
			byte[] responsePDU = responseMessage.getBytes();

			boolean lostMessage = (args.length == 0) ? false : true;
			// Send the datagram packet request from this socket
			if (!lostMessage) {
				OutputStream connectionEndPointOut = connectionEndPoint.getOutputStream();
				//Use TCP DATA service: TCP_DATA.response(responsePDU)
				System.out.println("Server sending response: " + responseMessage);
				connectionEndPointOut.write(responsePDU);
			} else {
				try {
					// Wait to close the connection, otherwise in the client
					// read is returning -1 and there is no exception
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			// 5. release the connection - close the client socket
			//TCP_DISCONNECT_REQUEST
			connectionEndPoint.close();
			// 6. close the server socket
			serviceAccessPoint.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /**
     * start application
     * init Sensor
     * init Station
     * link Sensor with Station
     * 
     */
}
