
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final Scanner sc = new Scanner(System.in, "iso-8859-1");

    private static final String START_SENSOR_COMMAND_REGEX = "START [\\wäöüÄÖÜß]+";
    private static final String START_SENSOR_COMMAND = "START";
    private static final String STOP_SENSOR_COMMAND = "STOP";
    private static final String INFO_COMMAND = "INFO";
    private static final String DATA_COMMAND_REGEX = "DATA [0-9]+";
    private static final String DATA_STOP_COMMAND = "DATA STOP";
    private static final String EXIT_DIALOG_COMMAND = "EXIT";

    private static Sensor sensor;
    private static TCPServer server;

    private static boolean sensorRunning;

    private static DataSenderThread dataSenderThread;

    private static AwaitMessageThread awaitMessageThread;

    public static void main(String[] args) {

        
        sensorRunning = false;
        String input = "";
        while (!input.equals(EXIT_DIALOG_COMMAND)) {

            sensorRunning = (sensor != null);

            printMenu();
            input = sc.nextLine().toUpperCase();

            if (input.matches(START_SENSOR_COMMAND_REGEX) && !sensorRunning) {
                String location = input.split(" ", 2)[1];
                System.out.println("starting a Sensor " + location);
                sensor = new Sensor(1, 1, location);
                try {
                    server = new TCPServer();
                    System.out.println("waiting for a weather station to connect...");
                    server.awaitConnection();
                    // dataSenderThread.start();
                    awaitMessageThread = new AwaitMessageThread();
                    awaitMessageThread.start();
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            }

            // server.sendMessage(input);
            else if (input.equals(STOP_SENSOR_COMMAND) && sensorRunning) {
                try {
                    if (dataSenderThread != null) {
                        dataSenderThread.kill();
                        dataSenderThread = null;
                    }
                    awaitMessageThread.kill();
                    awaitMessageThread = null;
                    sensorRunning = false;
                    sensor = null;
                    server.closeAll();
                    server = null;
                    System.out.println("stopping sensor...");

                    // give time to stop and close server and sensor
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("I wanted to give stop process time. Something went wrong.");
                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage() + "stopping failed");
                }
            } else if (!input.equals(EXIT_DIALOG_COMMAND)) {
                System.out.println("Invalid input!");
                // give user time to read result before re-displaying menu
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("I wanted to give user time. Something went wrong.");
                }
            }
        }
        // or interrupt both threads!

        sensor = null;
        server = null;
        sc.close();
        System.exit(0);
    }

    static class AwaitMessageThread extends Thread {

        public AtomicBoolean running = new AtomicBoolean(true);
        public volatile boolean exit = false;
        public long delay = 100;

        public void kill() {
            this.running.set(false);
            try {
                this.join();
            } catch (InterruptedException e) {
                //
            }
        }

        public void run() {

            while (running.get()) {

                if (server == null) {
                    System.out.println("------------ connection does not exist; server is down ------------");
                    break;
                }
                if (server.getConnectionEndPoint().isClosed()) {
                    System.out.println("-------- connection does not exist; please stop the sensor ---------");
                    break;
                }
                try {
                    String message = server.awaitMessage();
                    if (message.equals(INFO_COMMAND)) {
                        server.sendMessage(sensor.info());

                    } else if (message.matches(DATA_COMMAND_REGEX)) {
                        // interruptAndRemoveThread(dataSenderThread);
                        if (dataSenderThread != null) {
                            dataSenderThread.kill();
                        }
                        long delay = 1000 * Long.parseLong(message.split(" ", 2)[1]);
                        dataSenderThread = new DataSenderThread(delay);
                        dataSenderThread.start();

                    } else if (message.equals(DATA_STOP_COMMAND)) {

                        // running
                        if (dataSenderThread != null) {
                            dataSenderThread.kill();
                            dataSenderThread = null;
                        }

                        // interruptAndRemoveThread(dataSenderThread);
                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            }

        }
    }

    static class DataSenderThread extends Thread {

        private AtomicBoolean running = new AtomicBoolean(true);
        private final long delay;

        public DataSenderThread(long delay) {
            this.delay = delay;
        }

        public void kill() {
            this.running.set(false);
            this.interrupt();
            try {
                this.join();
            } catch (InterruptedException e) {
                //
            }
        }

        public void run() {
            while (running.get()) {
                try {
                    server.sendMessage(sensor.getCurrentTemp());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println("interrupt sending as it was requested");
                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                    System.out.println("server is not sending anymore, please stop server");
                    break;
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
