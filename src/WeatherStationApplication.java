
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

    public static void main(String[] args) {

        weatherStation = new WeatherStation();
        tcpClient = new TCPClient();

        AwaitMessageRunnable amr = new AwaitMessageRunnable();
        Thread awaitMessageThread = new Thread(amr);
        //verify first that connection is established
        System.out.println("WEATHERSTATIONAPPLICATION:");
        System.out.println("- Type \"" + START_STATION_COMMAND + "\" to start the weatherstation");
        System.out.println("- Type \"" + STOP_STATION_COMMAND + "\" to stop the weatherstation");
        System.out.println("- Type \"" + INFO_COMMAND + "\" for sensor information");
        System.out.println("- Type \"" + DATA_COMMAND + " <interval in s>\" for periodic sensor data");
        System.out.println("- Type \"" + MIN_MAX_COMMAND + "\" to get lowest and highest messured temperature");
        System.out.println("- Type \"" + MIN_MAX_RESET_COMMAND + "\" to reset lowest and highest messured temperature");
        System.out.println("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu");

        String input = "";
        while (!input.equals(EXIT_DIALOG_COMMAND)) {
            input = sc.nextLine();

            if (input.equals(START_STATION_COMMAND)) {
                awaitMessageThread.start();

            } else if (input.equals(STOP_STATION_COMMAND)) {
                awaitMessageThread.stop();

            } else if (input.equals(MIN_MAX_COMMAND)) {
                System.out.println(weatherStation.minMaxInfo());

            } else if (input.equals(MIN_MAX_RESET_COMMAND)) {
                weatherStation.reset();

            } else if (input.equals(INFO_COMMAND)
                    || input.matches(DATA_COMMAND_REGEX)) {
                System.out.println("sending...");
                tcpClient.sendMessage(input);
                
            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    static class AwaitMessageRunnable implements Runnable {

        public volatile boolean running = true;
        public long delay = 1000;

        public void run() {

            while (true) {
                if (running) {
                    String message = tcpClient.awaitMessage();
                    if (message != null) {//muss weg
                        System.out.println("Received: " + message);
                        if (ConnectionManager.isDoubleAndLong(message)) {
                            weatherStation.setCurrentTemp(Double.parseDouble(message.split(":")[0]), Long.parseLong(message.split(":")[1]));
                            System.out.println(weatherStation.minMaxInfo());
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
    }

}
