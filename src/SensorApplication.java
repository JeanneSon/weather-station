
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
public class SensorApplication {

    private static final Scanner sc = new Scanner(System.in);

    private static final String START_SENSOR_COMMAND_REGEX = "START [\\wäüöß]+";
    private static final String START_SENSOR_COMMAND = "START";
    private static final String STOP_SENSOR_COMMAND = "STOP";
    private static final String INFO_COMMAND = "INFO";
    private static final String DATA_COMMAND_REGEX = "DATA [0-9]+";
    private static final String EXIT_DIALOG_COMMAND = "EXIT";

    private static Sensor sensor;
    private static TCPServer server;

    private static boolean sensorRunning;

    //order of displaying menu
    public static void main(String[] args) {

        DataSenderRunnable dsr = new DataSenderRunnable();
        Thread dataSenderThread = new Thread(dsr);

        AwaitMessageRunnable amr = new AwaitMessageRunnable(dsr);
        Thread awaitMessageThread = new Thread(amr);

        sensorRunning = false;
        String input = "";
        while (!input.equals(EXIT_DIALOG_COMMAND)) {

            sensorRunning = sensor != null;

            printMenu();
            input = sc.nextLine();

            if (input.matches(START_SENSOR_COMMAND_REGEX) && !sensorRunning) {
                String location = input.split(" ", 2)[1];
                System.out.println("starting a Sensor " + location);
                sensor = new Sensor(1, 1, location);
                server = new TCPServer();
                server.awaitConnection();
                dataSenderThread.start();
                awaitMessageThread.start();

                //server.sendMessage(input);
            } else if (input.equals(STOP_SENSOR_COMMAND) && sensorRunning) {
                sensor = null;
                server = null;
                amr.running = false;
                dsr.running = false;
                System.out.println("stopping sensor...");

            } else {
                System.out.println("Invalid input!");
            }
        }
    }

    static class AwaitMessageRunnable implements Runnable {

        DataSenderRunnable dsr;

        public volatile boolean running = true;
        public long delay = 100;

        public AwaitMessageRunnable(DataSenderRunnable dsr) {
            this.dsr = dsr;
        }

        public void run() {

            while (true) {
                if (running) {
                    String message = server.awaitMessage();

                    if (message.equals(INFO_COMMAND)) {
                        server.sendMessage(sensor.info());

                    } else if (message.matches(DATA_COMMAND_REGEX)) {
                        dsr.running = true;
                        dsr.delay = 1000 * Long.parseLong(message.split(" ", 2)[1]);

                        // }   else if (message.equals("STOP")) {
                        //    dsr.running = false;
                    }
                }
            }
        }
    }

    static class DataSenderRunnable implements Runnable {

        public volatile boolean running = false;
        public long delay = 100;

        public void run() {

            while (true) {
                if (running) {

                    server.sendMessage(sensor.getCurrentTemp());

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }

    static void printMenu() {
        System.out.println("\n\n\n\n\n\n\n\n");
        System.out.println("SENSORAPPLICATION:");
        System.out.println("SENSOR:");
        if (!sensorRunning) {
            System.out.println("- Type \"" + START_SENSOR_COMMAND + " <locationname>\" to start the sensor");
        }
        if (sensorRunning) {
            System.out.println("- Type \"" + STOP_SENSOR_COMMAND + "\" to stop the sensor");
        }
        System.out.println("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu");
        System.out.println("_______________________________________________");

        System.out.println("Input: ");
    }
}
