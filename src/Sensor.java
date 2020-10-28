import java.util.Random;
public final class Sensor {

    // attributes are final as they can only be set once when the Sensor is instanciated
    private final int PRODUCT_ID;
    private final int VENDOR_ID;
    private final String location;
    private static final double MAX_TEMP = 50.0;
    private static final double MIN_TEMP = -20.0;
    private static final Random RANDOM = new Random();

    
    public Sensor(int productId, int vendorId, String location) {
        this.PRODUCT_ID = productId;
        this.VENDOR_ID = vendorId;
        this.location = location;
    }

    public String info() {
        return "ProductId: " + this.PRODUCT_ID + ", VendorId: " + this.VENDOR_ID + ", Location " + this.location;
    }

    public String getCurrentTemp() {
        double value = GeneralManager.round(MIN_TEMP + (MAX_TEMP - MIN_TEMP) * RANDOM.nextDouble(), 2);
        return String.valueOf(value + ":" + System.currentTimeMillis());
    }

}
