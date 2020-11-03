
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class contains useful functions that do not belong to a particular part
 * of the program
 *
 * @author J.Krug, H.Schall
 */
public class GeneralManager {

    /**
     * The constructor is set to private as no object of this class should be
     * instantiated.
     */
    private GeneralManager() {
    }

    /**
     * checks whether the string is a long
     *
     * @param s the string
     * @return true if long, else false
     */
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * checks whether the string is a double
     *
     * @param s the string
     * @return true if double, else false
     */
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * checks if format "A:B" containing A (double) and B (long) is correct
     *
     * @param s format
     * @return is correct
     */
    public static boolean isDoubleAndLong(String s) {
        try {
            Double.parseDouble(s.split(":")[0]);
            Long.parseLong(s.split(":")[1]);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * rounds a double value
     *
     * @param value double value
     * @param places degree of accuracy
     * @return rounded value
     */
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * converts a time in milliseconds (UTC timestamp) to a more readable string
     * containing hour, minute and second
     *
     * @param timeInMillis time in milliseconds
     * @return readable string of format "hh:mm:ss"
     */
    public static String timeInMillisToDate(long timeInMillis) {
        long second = (timeInMillis / 1000) % 60;
        long minute = (timeInMillis / (1000 * 60)) % 60;
        long hour = (timeInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
