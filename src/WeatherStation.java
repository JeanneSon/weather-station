

public class WeatherStation {
    
    private static final WeatherStation station = new WeatherStation();
    private static final TCPClient client = new TCPClient();

    private double minTemp;
    private double maxTemp;
    private double currentValue;
    private boolean measuredOneValid;

    public WeatherStation() {
        reset();
    }

    private void reset() {
        minTemp =  50.0;
        maxTemp = -20.0;
        currentValue = Double.NaN;
        measuredOneValid = false;
    }

    public String minMaxInfo() {
        if (measuredOneValid)
            return "\nMinimum: " + minTemp+ "  Maximum: " + maxTemp + "\n";
        else
            return "No valid value measured until now.";
    }

    public static void main(String[] args) {
        // request temperature, interval in milliseconds
        client.sendMessage("3000");
        String currentValueAsString = client.awaitMessage();
        try {
            station.currentValue = Double.parseDouble(currentValueAsString);
            station.measuredOneValid = true;
            if (station.currentValue < station.minTemp) station.minTemp = station.currentValue;
            if (station.currentValue > station.maxTemp) station.maxTemp = station.currentValue;
        } catch (Exception e) {
            //TODO: handle exception
        }
        client.awaitMessage();
        client.awaitMessage();
        client.awaitMessage();
        client.awaitMessage();
    }
}
