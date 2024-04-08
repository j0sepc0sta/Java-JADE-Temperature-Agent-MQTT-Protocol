import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.UUID;

public class TemperatureAgent extends Agent {
    private static final String BROKER_URL = "tcp://broker.emqx.io:1883"; // Replace with your broker URL
    private static final String CLIENT_ID = UUID.randomUUID().toString(); // Generate a unique client ID
    private static final String TEMPERATURE_TOPIC = "temperature/sensor"; // Specify your topic
    private IMqttClient mqttClient;
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
            // Publish a sample temperature reading
            String sampleTemperature = "25.5°C"; // Replace with actual temperature data
            mqttClient.publish(TEMPERATURE_TOPIC, sampleTemperature.getBytes(), 0, false);
            // Print temperature data within JADE container
            addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    // Retrieve temperature data from your agent's logic
                    String agentTemperatureData = "28.0°C"; // Replace with actual agent data
                    System.out.println("Agent temperature data: " + agentTemperatureData);
                    // Add your custom logic here
                    block();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
