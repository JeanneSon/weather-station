
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
    private static final String MIN_MAX_COMMAND = "MM";
    private static final String MIN_MAX_RESET_COMMAND = "MM RESET";
    private static final String EXIT_DIALOG_COMMAND = "EXIT";

    private static WeatherStation weatherStation;               
    private static TCPClient tcpClient;

    private static boolean weatherStationRunning;

    public static void main(String[] args) {

        AwaitMessageRunnable amr = new AwaitMessageRunnable();
        Thread awaitMessageThread = new Thread(amr);

        //verify first that connection is established
        weatherStationRunning = false;
        String input = "";
        while (!input.equals(EXIT_DIALOG_COMMAND)) {

            weatherStationRunning = weatherStation != null;

            printMenu();
            input = sc.nextLine();

            if (input.equals(START_STATION_COMMAND) && !weatherStationRunning) {
                try {
                    tcpClient = new TCPClient();
                    weatherStation = new WeatherStation();
                    awaitMessageThread.start();
                    amr.running = true;
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(STOP_STATION_COMMAND) && weatherStationRunning) {
                weatherStation = null;
                tcpClient = null;
                //awaitMessageThread.stop();
                amr.running = false;

            } else if (input.equals(MIN_MAX_COMMAND) && weatherStationRunning) {
                //hier body von Methode direkt reinschreiben -> über getMin...
                System.out.println(weatherStation.minMaxInfo());

            } else if (input.equals(MIN_MAX_RESET_COMMAND) && weatherStationRunning) {
                weatherStation.reset();

            } else if ((input.equals(INFO_COMMAND) || input.matches(DATA_COMMAND_REGEX))
                    && weatherStationRunning) {
                System.out.println("sending...");
                try {
                    tcpClient.sendMessage(input);
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    // fix menu
    // data-stop
    // use closer

    static void printMenu() {
        System.out.println("\n\n\n\n\n\n\n\n");
        System.out.println("WEATHERSTATIONAPPLICATION:");
        System.out.println("WEATHERSTATION:");
        if (weatherStationRunning && weatherStation.getMeasuredOneValid()) {
            System.out.println("Most recent transmitted temp: \n\t" + weatherStation.getCurrentTemp() + 
                                "°C at " + GeneralManager.timeInMillisToDate(weatherStation.getCurrentTempTime()));
            System.out.println(weatherStation.minMaxInfo());
        } else { 
            System.out.println("No valid value measured until now.");
        }
        System.out.println("_______________________________________________");
        System.out.println("MENU:");
        if (!weatherStationRunning) {
            System.out.println("- Type \"" + START_STATION_COMMAND + "\" to start the weatherstation");
        }
        if (weatherStationRunning) {
            System.out.println("- Type \"" + STOP_STATION_COMMAND + "\" to stop the weatherstation");
            System.out.println("- Type \"" + INFO_COMMAND + "\" for sensor information");
            System.out.println("- Type \"" + DATA_COMMAND + " <interval in s>\" for periodic sensor data");
            //DATA_STOP
            System.out.println("- Type \"" + MIN_MAX_COMMAND + "\" to get lowest and highest messured temperature");
            System.out.println("- Type \"" + MIN_MAX_RESET_COMMAND + "\" to reset lowest and highest messured temperature");
        }

        System.out.println("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu");
        System.out.println("_______________________________________________");

        System.out.println("Input: ");
    }

    static class AwaitMessageRunnable implements Runnable {

        public volatile boolean running = true;
        public long delay = 1000;

        public void run() {

            while (true) {
                if (running) {
                    String message = null;
                    try {
                        message = tcpClient.awaitMessage();
                    } catch (TCPPort.TCPException e) {
                        System.out.println(e.getMessage());
                    }
                    if (message != null && weatherStation != null) {//muss weg
                        //System.out.println("Received: " + message);
                        if (GeneralManager.isDoubleAndLong(message)) {
                            weatherStation.setCurrentTemp(Double.parseDouble(message.split(":")[0]), Long.parseLong(message.split(":")[1]));
                            //System.out.println(weatherStation.minMaxInfo());
                            printMenu();
                        }
                        else {
                            System.out.println(message);
                        }
                    } try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }

                }
            }
        }
    }

}
