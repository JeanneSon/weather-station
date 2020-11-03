
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages the Dialog/SensorApplication
 *
 * @author J.Krug, H.Schall
 */
public class SensorApplication {

    private static final Scanner sc = new Scanner(System.in, "iso-8859-1");

    private static final String START_SENSOR_COMMAND_REGEX = "START [\\wäöüÄÖÜß]+";
    private static final String START_SENSOR_COMMAND = "START";
    private static final String STOP_SENSOR_COMMAND = "STOP";
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
            // awaiting user input
            input = sc.nextLine().trim();

            if (input.matches(START_SENSOR_COMMAND_REGEX) && !sensorRunning) {
                // dividing command into START and <location>
                String location = input.split(" ", 2)[1];
                System.out.println("starting a Sensor " + location);
                // creating a new Sensor, new TCPServer and starting an awaitMessageThread
                sensor = new Sensor(1, 1, location);
                try {
                    server = new TCPServer();
                    System.out.println("waiting for a weather station to connect...");
                    server.awaitConnection();
                    awaitMessageThread = new AwaitMessageThread();
                    awaitMessageThread.start();
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            } else if (input.equals(STOP_SENSOR_COMMAND) && sensorRunning) {
                stopSensorApplication();
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

        stopSensorApplication();
        sc.close();
        System.out.println("exiting...");
        System.exit(0);
    }

    /**
     * Stopping all instances of: - Sensor - TCPServer - DataSenderThread -
     * AwaitMessageThread
     */
    private static void stopSensorApplication() {
        try {
            if (dataSenderThread != null) {
                dataSenderThread.kill();
                dataSenderThread = null;
            }
            if (awaitMessageThread != null) {
                awaitMessageThread.kill();
                awaitMessageThread = null;
            }
            sensorRunning = false;
            sensor = null;
            if (server != null) {
                server.closeAll();
                server = null;
            }
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

                if (server == null) {
                    System.out.println("------------ connection does not exist; server is down ------------");
                    break;
                }
                if (server.getConnectionEndPoint().isClosed()) {
                    System.out.println("----- no weatherstation connected\nstop the sensor");
                    System.out.println("-------- connection does not exist\n"
                            + "\t-> please stop the sensor (\""
                            + STOP_SENSOR_COMMAND + "\") ---------\n"
                            + "\t-> or exit the whole sensor application (\""
                            + EXIT_DIALOG_COMMAND + "\") ---------");
                    break;
                }
                try {
                    String message = server.awaitMessage();
                    if (message.equals(GlobalCommands.INFO_COMMAND)) {
                        server.sendMessage(sensor.info());

                    } else if (message.matches(GlobalCommands.DATA_COMMAND_REGEX)) {
                        if (dataSenderThread != null) {
                            dataSenderThread.kill();
                        }
                        // dividing command into DATA and the delay
                        // converting delay from s to ms
                        long delayCommand = 1000 * Long.parseLong(message.split(" ", 2)[1]);
                        dataSenderThread = new DataSenderThread(delayCommand);
                        dataSenderThread.start();

                    } else if (message.equals(GlobalCommands.DATA_STOP_COMMAND)) {
                        // Stopping DataSenderThread if running
                        if (dataSenderThread != null) {
                            dataSenderThread.kill();
                            dataSenderThread = null;
                        }

                    } else if (message.equals(GlobalCommands.STOP_SENSOR_REQUEST)) {
                        // Stopping entire SensorApplication
                        if (dataSenderThread != null) {
                            dataSenderThread.kill();
                            dataSenderThread = null;
                        }
                        sensorRunning = false;
                        sensor = null;
                        if (server != null) {
                            try {
                                server.closeAll();
                            } catch (TCPPort.TCPException e) {
                                System.out.println("--- failed to shutdown server correctly ---");
                            }
                            server = null;
                        }
                        System.out.println("stopping sensor...");
                        printMenu();
                        break;

                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                }

            }

        }
    }

    static class DataSenderThread extends Thread {

        // multiple Threads can modify running so it needs to be atomic to ensure clean/predictable field modifications
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final long delay;

        public DataSenderThread(long delay) {
            this.delay = delay;
        }

        /**
         * killing current instance of this Thread
         */
        public void kill() {
            // since there is a Thread.sleep() in run(), the Thread could remain in it if running is set to false
            // the Thread must be interrupted
            this.running.set(false);
            this.interrupt();
            try {
                this.join();
            } catch (InterruptedException e) {
                //
            }
        }

        /**
         * Is constantly sending new message containing the current messured
         * temperature
         */
        @Override
        public void run() {
            while (running.get()) {
                try {
                    server.sendMessage(sensor.getCurrentTemp());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println("---- sending process was interrupted -----");
                    }
                } catch (TCPPort.TCPException e) {
                    System.out.println(e.getMessage());
                    System.out.println("server is not sending anymore, please stop server");
                    break;
                }

            }
        }
    }

    /**
     * prints the SensorApplication menu on the console
     */
    static void printMenu() {
        StringBuilder menu = new StringBuilder();
        menu.append("\nSENSORAPPLICATION:\nSENSOR:\n");
        menu.append("_______________________________________________\nMENU:\n");
        if (!sensorRunning) {
            menu.append("- Type \"" + START_SENSOR_COMMAND + " <locationname>\" to start the sensor\n");
        }
        if (sensorRunning) {
            menu.append("- Type \"" + STOP_SENSOR_COMMAND + "\" to stop the sensor\n");
        }
        menu.append("- Type \"" + EXIT_DIALOG_COMMAND + "\" to close this menu\n");
        menu.append("_______________________________________________\n" + "Input: ");
        System.out.println(menu.toString());
    }
}
