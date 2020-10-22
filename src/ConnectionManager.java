import java.lang.Math;

public class ConnectionManager {

    public static final int SERVER_PORT = 10001;

    private ConnectionManager() {
    }

    /**
     * 
     * @param s
     * @return long if valid and -1 if invalid
     */
    public static long isLong(String s) {
        try {
            return Math.abs(Long.parseLong(s));
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }
}
