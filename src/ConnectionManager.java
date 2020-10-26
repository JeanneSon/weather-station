
import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConnectionManager {

    public static final int SERVER_PORT = 10001;

    private ConnectionManager() {
    }

    /**
     *
     * @param s
     * @return long if valid and -1 if invalid
     */
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isDoubleAndLong(String s) {
        try {
            Double.parseDouble(s.split(":")[0]);
            Long.parseLong(s.split(":")[1]);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
