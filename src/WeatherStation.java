
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
     * @throws Exception
     * @return currentTemp
     */
    public double getCurrentTemp() {
        //throw exception if not measured -> weatherstation exception
        return currentTemp;
    }

    /**
     * @throws Exception
     * @return currentTempTime
     */
    public long getCurrentTempTime() {
        return currentTempTime;
    }

    /**
     * @throws Exception
     * @return minTemp
     */
    public double getMinTemp() {
        return minTemp;
    }

    /**
     * @throws Exception
     * @return maxTemp
     */
    public double getMaxTemp() {
        return maxTemp;
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
    public void reset() {
        currentTemp = Double.NaN;
        currentTempTime = 0L;
        minTemp = Double.MAX_VALUE;
        maxTemp = -Double.MAX_VALUE;
        measuredOneValid = false;
    }

}
