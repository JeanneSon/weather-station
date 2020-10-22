

public class WeatherStation {

    private double minTemp;
    private double maxTemp;
    private double currentValue;
    private boolean measuredOneValid;

    public WeatherStation() {
        minTemp =  50.0;
        maxTemp = -20.0;
        currentValue = Double.NaN;
        measuredOneValid = false;
    }

    public void reset() {
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
        WeatherStation station = new WeatherStation();
        TCPClient client = new TCPClient();
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
