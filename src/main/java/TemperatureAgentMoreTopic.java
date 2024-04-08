import jade.core.Agent;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class TemperatureAgentMoreTopic extends Agent {
    private static final String BROKER_URL = "tcp://broker.emqx.io:1883";
    private static final String[] TOPICS = {
            "temperature/room1",
            "temperature/room2",
            "temperature/room3",
            // Add more topics as needed
    };
    private MqttClient mqttClient;
    protected void setup() {
        try {
            // Connect to MQTT broker
            mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
            mqttClient.connect();
            // Subscribe to temperature topics
            for (String topic : TOPICS) {
                mqttClient.subscribe(topic);
            }
            // Set up callback for incoming messages
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection to MQTT broker lost!");
                }
                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    String temperatureData = new String(mqttMessage.getPayload());
                    processTemperatureData(topic, temperatureData);
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    // Not used in this example
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void processTemperatureData(String topic, String data) {
        // Implement your logic to process temperature data here
        System.out.println("Received temperature data for topic " + topic + ": " + data);
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
