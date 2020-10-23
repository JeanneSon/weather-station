

public class WeatherStation {
    
    public static final WeatherStation station = new WeatherStation();
    private static final TCPClient client = new TCPClient();

    private double minTemp;
    private double maxTemp;
    private double currentValue;
    private boolean measuredOneValid;


    private WeatherStation() {
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
//pro app ein thread
    public static void main(String[] args) {
     //wetterstationAPp (in main while schleife): weatherstation ; tcpclient ; einleseklasse ; (ueses zu weatherstation (rein data und minmax) und tcpclient; keine abhängigkeit zw den 2)
        System.out.println(station.minMaxInfo());
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
