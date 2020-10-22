
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

    private String generateValue() {
        double value = MIN_TEMP + (MAX_TEMP - MIN_TEMP) * ImportManager.generateRandomDouble();
        //improve cutter
        return String.valueOf(value).substring(0, 4);
    }

    public static void main(String[] args) {
        Sensor sensor = new Sensor(1, 1, "Saarbruecken");
        TCPServer server = new TCPServer();
        String timeIntervalString = server.awaitMessage();
        long timeInterval = ConnectionManager.isLong(timeIntervalString);
        if (timeInterval == -1) {
            server.sendMessage("invalid time interval");
        }
        else {
            long until = System.currentTimeMillis() + timeInterval*20;
            while (System.currentTimeMillis() < until) {
                /*server.sendMessage(
                    System.currentTimeMillis() + " milliseconds, " + sensor.generateValue() + "°C");*/
                server.sendMessage(sensor.generateValue());
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    //TODO: handle exception
                }
            }
        }
        server.sendMessage("Hallo");
        server.closer();
    }

    /**
     * start application init Sensor init Station link Sensor with Station
     *
     */
}
