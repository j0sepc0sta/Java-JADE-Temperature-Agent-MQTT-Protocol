import jade.core.Agent;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Random;

public class TemperatureAgentMoreTopicRandom extends Agent {
    private static final String BROKER_URL = "tcp://broker.emqx.io:1883";
    private static final String[] TOPICS = {
            "temperature/room1",
            "temperature/room2",
            "temperature/room3",
    };
    private MqttClient mqttClient;
    private Random random = new Random();
    protected void setup() {
        try {
            // Connect to MQTT broker
            mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.connect();
            // Publish random temperature data to each topic at regular intervals
            for (String topic : TOPICS) {
                publishRandomTemperature(topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void publishRandomTemperature(String topic) {
        new Thread(() -> {
            while (true) {
                try {
                    // Generate random temperature data
                    double temperature = generateRandomTemperature();
                    // Publish temperature data to MQTT broker
                    mqttClient.publish(topic, new MqttMessage(Double.toString(temperature).getBytes()));
                    // Sleep for a specified interval (e.g., 5 seconds)
                    Thread.sleep(5000); // Change the interval as needed
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private double generateRandomTemperature() {
        // Generate a random temperature between 0 and 100
        return random.nextDouble() * 100;
    }
    protected void takeDown() {
        try {
            // Disconnect from MQTT broker
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
