import java.util.Random;
public final class Sensor {
    private static final Random random = new Random();
    
// setter für Standort

    // oder überall static weglassen; singleton
    private static Sensor sensor = new Sensor(1, 1, "Saarbruecken");
    private static TCPServer server = new TCPServer();

    // attributes are final as they can only be set once when the Sensor is instanciated
    private final static int productId = 5;
    private final static int vendorId = 3;
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

    //remove ImportManager
    private String generateTemp() {
        double value = MIN_TEMP + (MAX_TEMP - MIN_TEMP) * ImportManager.generateRandomDouble();
        //improve cutter
        return String.valueOf(value).substring(0, 4);
    }
//sensor -> sensorapplikation
//main will standort -> scanner.next -> thread startet kommunikation -> while schleife, die auf stop wartet
    public static void main(String[] args) {
        //standort angeben 
        //user beendet sensor durch x
        //siehe printing .
        //im Thread TCP-kommunikation (Temperaturübermittlung + info, data, stop)
        String timeIntervalString = server.awaitMessage();
        long timeInterval = ConnectionManager.isLong(timeIntervalString);
        if (timeInterval == -1) {
            server.sendMessage("invalid time interval");
        }
        else {
            long until = System.currentTimeMillis() + timeInterval*20;
            while (System.currentTimeMillis() < until) {
                /*server.sendMessage(
                    System.currentTimeMillis() + " milliseconds, " + sensor.generateTemp() + "°C");*/
                server.sendMessage(sensor.generateTemp());
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
