
/**
 * This class manages the global commands of the communication
 *
 * @author J.Krug, H.Schall
 */
public abstract class GlobalCommands {

    private GlobalCommands() {
    }

    public static final String INFO_COMMAND = "INFO";
    public static final String DATA_COMMAND = "DATA";
    public static final String DATA_COMMAND_REGEX = "DATA [1-9][0-9]*";
    public static final String DATA_STOP_COMMAND = "DATA STOP";
    public static final String STOP_SENSOR_REQUEST = "STOP REQUEST";

}
