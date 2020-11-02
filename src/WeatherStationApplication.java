
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author j
 */
public class WeatherStationApplication {

    private static final Scanner sc = new Scanner(System.in);

    private static final String START_STATION_COMMAND = "START";
    private static final String STOP_STATION_COMMAND = "STOP";
    private static final String INFO_COMMAND = "INFO";
    private static final String DATA_COMMAND = "DATA";
    private static final String DATA_COMMAND_REGEX = "DATA [0-9]+";
    private static final String DATA_STOP_COMMAND = "DATA STOP";
    private static final String MIN_MAX_COMMAND = "MM";
    private static final String MIN_MAX_RESET_COMMAND = "MM RESET";
    private static final String EXIT_DIALOG_COMMAND = "EXIT";

    //try to reconnect to sensor if tcpClient isClosed

    private static WeatherStation weatherStation;
    private static TCPClient tcpClient;

    private static boolean weatherStationRunning;

    private static AwaitMessageRunnable amr;
    private static Thread awaitMessageThread;

    public static void main(String[] args) {

        amr = new AwaitMessageRunnable();

        //verify first that connection is established
        weatherStationRunning = false;
        String input = "";
        do {

            weatherStationRunning = (weatherStation != null);

            printMenu();
            input = sc.nextLine().toUpperCase().trim();

            if (input.equals(START_STATION_COMMAND) && !weatherStationRunning) {
                try {
                    tcpClient = new TCPClient();
                    weatherStation = new WeatherStation();
                    awaitMessageThread = new Thread(amr);
                    awaitMessageThread.start();
                    amr.running = true;
                    System.out.println("I start again.....................");
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(STOP_STATION_COMMAND) && weatherStationRunning) {
                try {
                    tcpClient.closeSocket();

// change to kill


                    interruptAndRemoveThread(awaitMessageThread);
                    weatherStation = null;
                    tcpClient = null;
                    amr.running = false;

                    // give stop process time before re-displaying menu
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        System.out.println("I wanted to give stop process time. Something went wrong.");
                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(MIN_MAX_COMMAND) && weatherStationRunning) {
                try {
                    System.out.println("Since last reset / start of station: Minimum: "
                            + weatherStation.getMinTemp() + "  Maximum: " + weatherStation.getMaxTemp());
                } catch (WeatherStation.WeatherStationException e) {
                    System.out.println("No valid value measured until now.");
                }

                // give user time to read result before re-displaying menu
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    System.out.println("I wanted to give user time. Something went wrong.");
                }

            } else if (input.equals(MIN_MAX_RESET_COMMAND) && weatherStationRunning) {
                weatherStation.reset();

            } else if ((input.equals(INFO_COMMAND)
                    || input.matches(DATA_COMMAND_REGEX)
                    || input.equals(DATA_STOP_COMMAND))
                    && weatherStationRunning) {
                System.out.println("sending...");
                try {
                    tcpClient.sendMessage(input);
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

                // give user time to read result before re-displaying menu
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            
            } else if (input.equals(EXIT_DIALOG_COMMAND)) {
                System.exit(0);
                
            } else {
                System.out.println("Invalid input!");
                // give user time to read result before re-displaying menu
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        } while (!input.equals(EXIT_DIALOG_COMMAND));
    }

    // fix menu
    // data-stop
    // use closer
    static void printMenu() {
        StringBuffer menu = new StringBuffer("\n\n\n\n\n\n\n\nWEATHERSTATIONAPPLICATION:\nWEATHERSTATION:\n");
        if (weatherStationRunning && weatherStation.getMeasuredOneValid()) {
            try {
                menu.append("Most recent transmitted temp: \t" + weatherStation.getCurrentTemp() + 
                        "°C at " + GeneralManager.timeInMillisToDate(weatherStation.getCurrentTempTime()) +
                        "\nSince last reset / start of station: Minimum: " + 
                        weatherStation.getMinTemp() + "°C  Maximum: " + weatherStation.getMaxTemp() + "°C\n");
            } catch (WeatherStation.WeatherStationException e) {
                menu.append("No valid value measured until now.\n");
            }
        } 
        menu.append("_______________________________________________\nMENU:\n");
        if (!weatherStationRunning) {
            menu.append("- Type \"" + START_STATION_COMMAND + "\" to start the weatherstation\n");
        }
        if (weatherStationRunning) {
            menu.append("- Type \"" + STOP_STATION_COMMAND  + "\" to stop the weatherstation\n"               +
                        "- Type \"" + INFO_COMMAND          + "\" for sensor information\n"                   +
                        "- Type \"" + DATA_COMMAND          + " <interval in s>\" for periodic sensor data\n" +
                        "- Type \"" + DATA_STOP_COMMAND     + "\" to stop periodic sending\n"                 +
                        "- Type \"" + MIN_MAX_COMMAND       + "\" to get lowest and highest measured temperature\n"+
                        "- Type \"" + MIN_MAX_RESET_COMMAND + 
                                                    "\" to reset lowest and highest measured temperature\n"
                    );
        }
        menu.append("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu\n" +
                    "_______________________________________________\n" +
                    "Input: ");
        System.out.println(menu.toString());
    }

    static class AwaitMessageRunnable implements Runnable {

        public volatile boolean running = true;
        public long delay = 1000;

        public void run() {

            while (true) {
                if (running) {
                    String message = null;
                    if (tcpClient.serverIsInactive()) {
                        System.out.println("Interrupting AwaitMessageRunnable of WS");
                        break;
                    }
                    try {
                        message = tcpClient.awaitMessage();
                    } catch (TCPPort.TCPException e) {
                        System.out.println(e.getMessage());
                    }
                    if (message != null && weatherStation != null) {//muss weg
                        //System.out.println("Received: " + message);
                        if (GeneralManager.isDoubleAndLong(message)) {
                            weatherStation.setCurrentTemp(Double.parseDouble(message.split(":")[0]), 
                            Long.parseLong(message.split(":")[1]));
                            //System.out.println(weatherStation.minMaxInfo());
                            printMenu();
                        } else if (!message.equals("")) {
                            System.out.println(message);
                        }
                    }
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                }
            }
        }
    }

    private static void interruptAndRemoveThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
    }

}
