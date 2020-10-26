
import com.sun.nio.sctp.SendFailedNotification;

public final class Sensor {

    // attributes are final as they can only be set once when the Sensor is instanciated
    private final int productId;
    private final int vendorId;
    private final String location;
    private static final double MAX_TEMP = 50.0;
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

    public String getCurrentTemp() {
        double value = ConnectionManager.round(MIN_TEMP + (MAX_TEMP - MIN_TEMP) * ImportManager.generateRandomDouble(), 2);
        //improve cutter
        return String.valueOf(value + ":" + System.currentTimeMillis());
    }

}
