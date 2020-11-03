
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages the Dialog/WeatherStationApplication
 *
 * @author J.Krug, H.Schall
 */
public class WeatherStationApplication {

    private static final Scanner sc = new Scanner(System.in, "iso-8859-1");

    private static final String START_STATION_COMMAND = "START";
    private static final String STOP_STATION_COMMAND = "STOP";
    private static final String MIN_MAX_COMMAND = "MM";
    private static final String MIN_MAX_RESET_COMMAND = "MM RESET";
    private static final String EXIT_DIALOG_COMMAND = "EXIT";

    private static WeatherStation weatherStation;
    private static TCPClient tcpClient;

    private static boolean weatherStationRunning;

    private static AwaitMessageThread awaitMessageThread;

    public static void main(String[] args) {

        weatherStationRunning = false;
        String input = "";
        while (!input.equals(EXIT_DIALOG_COMMAND)) {

            weatherStationRunning = (weatherStation != null);

            printMenu();
            // awaiting user input
            input = sc.nextLine().trim();

            if (input.equals(START_STATION_COMMAND) && !weatherStationRunning) {
                // creating a new WeatherStation, new TCPClient and starting an awaitMessageThread
                try {
                    tcpClient = new TCPClient();
                    weatherStation = new WeatherStation();
                    awaitMessageThread = new AwaitMessageThread();
                    awaitMessageThread.start();
                    System.out.println("starting.....................");
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(STOP_STATION_COMMAND) && weatherStationRunning) {
                try {
                    tcpClient.sendMessage(GlobalCommands.STOP_SENSOR_REQUEST);
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("stopping weather station....");
                stopWeatherStation();

            } else if (input.equals(MIN_MAX_COMMAND) && weatherStationRunning) {
                try {
                    System.out.println("Since last reset / start of station: Minimum: " + weatherStation.getMinTemp()
                            + "  Maximum: " + weatherStation.getMaxTemp());
                } catch (WeatherStation.WeatherStationException e) {
                    System.out.println("No valid value measured until now.");
                }

            } else if (input.equals(MIN_MAX_RESET_COMMAND) && weatherStationRunning) {
                weatherStation.reset();
                System.out.println("resetting weather station...");

            } else if ((input.equals(GlobalCommands.INFO_COMMAND) || input.matches(GlobalCommands.DATA_COMMAND_REGEX)
                    || input.equals(GlobalCommands.DATA_STOP_COMMAND)) && weatherStationRunning) {
                System.out.println("sending...");
                try {
                    tcpClient.sendMessage(input);
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(EXIT_DIALOG_COMMAND)) {
                // loop should stop now automatically and end main

            } else {
                System.out.println("Invalid input!");
            }

            if (!input.equals(EXIT_DIALOG_COMMAND)) {
                // give user time to read result before re-displaying menu
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }

        }
        System.out.println("exiting...");
        sc.close();
        stopWeatherStation();
    }

    /**
     * Stopping all instances of: - WeatherStation - TCPClient -
     * AwaitMessageThread
     */
    private static void stopWeatherStation() {
        try {
            if (tcpClient != null) {
                tcpClient.closeSocket(); // throws TCPException
                tcpClient = null;
            }
            if (awaitMessageThread != null) {
                awaitMessageThread.kill();
                awaitMessageThread = null;
            }
            weatherStation = null;
        } catch (TCPPort.TCPException e) {
            System.out.println(e.getMessage() + "stopping failed");
        }
    }

    static class AwaitMessageThread extends Thread {

        // multiple Threads can modify running so it needs to be atomic to ensure clean/predictable field modifications
        private final AtomicBoolean running = new AtomicBoolean(true);

        /**
         * killing current instance of this Thread
         */
        public void kill() {
            // since there is no Thread.sleep() in run(), the Thread will run out if running is set to false
            this.running.set(false);
            try {
                this.join();
            } catch (InterruptedException e) {
                //
            }
        }

        /**
         * Is constantly listening for new message received by the server
         */
        @Override
        public void run() {

            while (running.get()) {
                String message = null;
                if (tcpClient.serverIsInactive()) {
                    System.out.println("----- Sensor server closed connection.-----\n"
                            + "----------Stop and restart WeatherStation to request reconnection.-------------");

                    // give user time to read result before re-displaying menu
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    printMenu();
                    break;
                }
                try {
                    message = tcpClient.awaitMessage();
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }
                if (message != null && weatherStation != null) {
                    if (GeneralManager.isDoubleAndLong(message)) {
                        // dividing received data into temp and time
                        weatherStation.setCurrentTemp(Double.parseDouble(message.split(":")[0]),
                                Long.parseLong(message.split(":")[1]));
                        printMenu();
                    } else if (!message.equals("")) {
                        System.out.println(message);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

            }
        }
    }

    /**
     * prints the WeatherStationApplication menu on the console
     */
    private static void printMenu() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nWEATHERSTATIONAPPLICATION:\nWEATHERSTATION:\n");
        if (weatherStationRunning && weatherStation.getMeasuredOneValid()) {
            try {
                menu.append("Most recent transmitted temp: \t" + weatherStation.getCurrentTemp() + "°C at "
                        + GeneralManager.timeInMillisToDate(weatherStation.getCurrentTempTime()));
            } catch (WeatherStation.WeatherStationException e) {
                menu.append("No valid value measured until now.\n");
            }
        }
        menu.append("_______________________________________________\nMENU:\n");
        if (!weatherStationRunning) {
            menu.append("- Type \"" + START_STATION_COMMAND + "\" to start the weatherstation\n");
        }
        if (weatherStationRunning) {
            menu.append("- Type \"" + STOP_STATION_COMMAND + "\" to stop the weatherstation\n");
            menu.append("- Type \"" + GlobalCommands.INFO_COMMAND + "\" for sensor information\n");
            menu.append("- Type \"" + GlobalCommands.DATA_COMMAND + " <interval in s>\" for periodic sensor data\n");
            menu.append("- Type \"" + GlobalCommands.DATA_STOP_COMMAND + "\" to stop periodic sending\n");
            menu.append("- Type \"" + MIN_MAX_COMMAND + "\" to get lowest and highest measured temperature\n");
            menu.append("- Type \"" + MIN_MAX_RESET_COMMAND + "\" to reset lowest and highest measured temperature\n");
        }
        menu.append("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu\n");
        menu.append("_______________________________________________\n" + "Input: ");
        System.out.println(menu.toString());
    }

}
