
/**
 * This class manages the WeatherStation
 *
 * @author J.Krug, H.Schall
 */
public class WeatherStation {

    private double minTemp;
    private double maxTemp;
    private double currentTemp;
    private long currentTempTime;
    private boolean measuredOneValid;

    public WeatherStation() {
        reset();
    }

    /**
     * @return measuredOneValid
     */
    public boolean getMeasuredOneValid() {
        return measuredOneValid;
    }

    /**
     * Sets currentTemp and updates minTemp and maxTemp
     *
     * @param currentTemp
     * @param currentTempTime
     */
    public void setCurrentTemp(double currentTemp, long currentTempTime) {
        this.currentTemp = currentTemp;
        this.currentTempTime = currentTempTime;
        measuredOneValid = true;
        updateMinTempAndMaxTemp();
    }

    /**
     * @return currentTemp
     * @throws WeatherStation.WeatherStationException
     */
    public double getCurrentTemp() throws WeatherStationException {
        if (measuredOneValid) //throw exception if not measured -> weatherstation exception
        {
            return currentTemp;
        } else {
            throw new WeatherStationException("No value measured until now.");
        }
    }

    /**
     * @return currentTempTime
     * @throws WeatherStation.WeatherStationException
     */
    public long getCurrentTempTime() throws WeatherStationException {
        if (measuredOneValid) {
            return currentTempTime;
        } else {
            throw new WeatherStationException("No value measured until now.");
        }
    }

    /**
     * @throws WeatherStation.WeatherStationException
     * @return minTemp
     */
    public double getMinTemp() throws WeatherStationException {
        if (measuredOneValid) {
            return minTemp;
        } else {
            throw new WeatherStationException("No value measured until now.");
        }
    }

    /**
     * @throws WeatherStation.WeatherStationException
     * @return maxTemp
     */
    public double getMaxTemp() throws WeatherStationException {
        if (measuredOneValid) {
            return maxTemp;
        } else {
            throw new WeatherStationException("No value measured until now.");
        }
    }

    /**
     * Updates minTemp and maxTemp using currentTemp
     */
    private void updateMinTempAndMaxTemp() {
        if (currentTemp > maxTemp) {
            maxTemp = currentTemp;
        }
        if (currentTemp < minTemp) {
            minTemp = currentTemp;
        }

    }

    /**
     * Sets currentTemp, currentTempTime, minTemp, maxTemp, measuredOneValid to
     * default values
     */
    public final void reset() {
        currentTemp = Double.NaN;
        currentTempTime = 0L;
        minTemp = Double.MAX_VALUE;
        maxTemp = -Double.MAX_VALUE;
        measuredOneValid = false;
    }

    public static class WeatherStationException extends Exception {

        public WeatherStationException() {
            super();
        }

        public WeatherStationException(String msg) {
            super(msg);
        }
    }
}
