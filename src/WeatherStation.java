

public class WeatherStation {

    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        // request temperature, interval in milliseconds
        client.sendMessage("3000");
        client.awaitMessage();
        client.awaitMessage();
        client.awaitMessage();
        client.awaitMessage();
        client.awaitMessage();
    }
}
