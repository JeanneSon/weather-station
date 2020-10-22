

// class is final as no class should inherit from this class
public final class Sensor {
    // attributes are final as they can only be set once when the Sensor is instanciated
    private final int productId;
    private final int vendorId;
    private final String location;
    private static final double MAX_TEMP =  50.0; 
    private static final double MIN_TEMP = -20.0; 
    
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
    /**
     * start application
     * init Sensor
     * init Station
     * link Sensor with Station
     * 
     */
}
