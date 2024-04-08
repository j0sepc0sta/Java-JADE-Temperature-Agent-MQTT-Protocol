import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Random;
import java.util.UUID;

public class TemperatureAgentRandom extends Agent {
    private static final String BROKER_URL = "tcp://broker.emqx.io:1883"; // Replace with your broker URL
    private static final String CLIENT_ID = UUID.randomUUID().toString(); // Generate a unique client ID
    private static final String TEMPERATURE_TOPIC = "temperature/sensorrandom"; // Specify your topic
    private IMqttClient mqttClient;
    private Random random = new Random();
    protected void setup() {
        try {
            // Initialize MQTT client
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10); // Set your desired timeout
            mqttClient.connect(options);
            // Subscribe to temperature updates
            mqttClient.subscribe(TEMPERATURE_TOPIC, (topic, message) -> {
                String receivedData = new String(message.getPayload());
                System.out.println("Received temperature data: " + receivedData);
                // Handle the received temperature data here
            });
            // Publish random temperature readings periodically
            addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    double randomTemperature = generateRandomTemperature(); // Get a random temperature
                    String formattedTemperature = String.format("%.1f°C", randomTemperature);
                    try {
                        mqttClient.publish(TEMPERATURE_TOPIC, formattedTemperature.getBytes(), 0, false);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Published random temperature: " + formattedTemperature);
                    // Add any other agent logic here
                    block();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private double generateRandomTemperature() {
        // Generate a random temperature between 20°C and 30°C
        return 20.0 + random.nextDouble(); //* 10.0;
    }
    protected void takeDown() {
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
