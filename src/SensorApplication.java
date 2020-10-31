
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

    private static DataSenderRunnable dsr;
    private static Thread dataSenderThread;

    private static AwaitMessageRunnable amr;
    private static Thread awaitMessageThread;


    public static void main(String[] args) {

        dsr = new DataSenderRunnable();
        amr = new AwaitMessageRunnable();
        awaitMessageThread = new Thread(amr);

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
                    //dataSenderThread.start();
                    awaitMessageThread.start();
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            }


                //server.sendMessage(input);
            else if (input.equals(STOP_SENSOR_COMMAND) && sensorRunning) {
                try {
                    interruptAndRemoveThread(dataSenderThread);
                    interruptAndRemoveThread(awaitMessageThread);
                    sensorRunning = false;
                    sensor = null;
                    server.closeAll();
                    server = null;
                    System.out.println("stopping sensor...");
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }
            } else if (!input.equals(EXIT_DIALOG_COMMAND)) {
                System.out.println("Invalid input!");
            }
        }
        //or interrupt both threads!
        
        sensor = null;
        server = null;
        sc.close();
        System.exit(0);
    }

    static class AwaitMessageRunnable implements Runnable {

        public volatile boolean running = true;
        public volatile boolean exit = false;
        public long delay = 100;

        

        public void run() {

            while (!exit) {
                
                if (server.getConnectionEndPoint().isClosed()) {
                    System.out.println("\t\t\t\t\tgetConnectionEndPoint is closed");
                    break;
                }
                if (running) {
                    try {
                        String message = server.awaitMessage();
                        if (message.equals(INFO_COMMAND)) {
                            server.sendMessage(sensor.info());

                        } else if (message.matches(DATA_COMMAND_REGEX)) {
                            interruptAndRemoveThread(dataSenderThread);
                            dataSenderThread = new Thread(dsr);
                            dataSenderThread.start();
                            dsr.running = true;
                            dsr.delay = 1000 * Long.parseLong(message.split(" ", 2)[1]);

                        } else if (message.equals(DATA_STOP_COMMAND)) {

                            // running

                            dsr.running = false;
                            interruptAndRemoveThread(dataSenderThread);
                        }
                    } catch (TCPPort.TCPException e) {
                        System.out.println(e.getMessage() + " I am in await message of sensor");
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

    static class DataSenderRunnable implements Runnable {

        public volatile boolean running = false;
        public long delay = 100;

        public void run() {
            while (true) { //instead of true
                if (running) {

                    try {
                        server.sendMessage(sensor.getCurrentTemp());
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                    } catch (TCPPort.TCPException e) {
                        System.out.println(e.getMessage());
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
