public class Sensor {
    private final int productId;
    private final int vendorId;
    private final String location;

    public Sensor(int productId, int vendorId, String location) {
        this.productId = productId;
        this.vendorId = vendorId;
        this.location = location;
    }

    public String info() {
        return "ProductId: " + this.productId + ", VendorId: " + this.vendorId + ", Standort " + this.location;
    }

    /**
     * start application
     * init Sensor
     * init Station
     * link Sensor with Station
     * 
     */
}
