public class WeatherStation {

    private double minTemp;
    private double maxTemp;
    private double currentTemp;
    private long currentTempTime;
    private boolean measuredOneValid;

    public WeatherStation() {
        reset();
    }
    
    public boolean getMeasuredOneValid() {
        return measuredOneValid;
    }

    public void setCurrentTemp(double currentTemp, long currentTempTime) {
        this.currentTemp = currentTemp;
        this.currentTempTime = currentTempTime;
        measuredOneValid = true;
        updateMinTempAndMaxTemp();
    }

    public double getCurrentTemp() {
        //throw exception if not measured -> weatherstation exception
        return currentTemp;
    }


    public long getCurrentTempTime() {
        return currentTempTime;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    private void updateMinTempAndMaxTemp() {
        if (currentTemp > maxTemp) {
            maxTemp = currentTemp;
        }
        if (currentTemp < minTemp) {
            minTemp = currentTemp;
        }

    }

    public void reset() {
        minTemp = 50.0;
        maxTemp = -20.0;
        currentTemp = Double.NaN;
        currentTempTime = 0L;
        measuredOneValid = false;
    }

    public String minMaxInfo() {
        if (measuredOneValid) {
            return "Since last reset / start of station\n\tMinimum: " + minTemp + "  Maximum: " + maxTemp;
        } else {
            return "No valid value measured until now.";
        }
    }

}
